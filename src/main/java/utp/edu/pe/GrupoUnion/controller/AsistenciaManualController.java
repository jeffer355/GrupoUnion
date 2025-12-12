package utp.edu.pe.GrupoUnion.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.Asistencia;
import utp.edu.pe.GrupoUnion.repository.AsistenciaRepository;
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asistencia-manual")
public class AsistenciaManualController {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    // --- REGLAS DE HORARIO ---
    private final LocalTime HORA_INICIO_JORNADA = LocalTime.of(8, 0);    // 08:00 AM
    private final LocalTime HORA_FIN_TOLERANCIA = LocalTime.of(8, 30);   // 08:30 AM
    private final LocalTime HORA_LIMITE_TARDANZA = LocalTime.of(10, 0);  // 10:00 AM
    private final LocalTime HORA_SALIDA_OFICIAL = LocalTime.of(17, 0);   // 05:00 PM

    public AsistenciaManualController(AsistenciaRepository asistenciaRepository, EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- UTILITARIOS ---
    private Empleado getEmpleadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        return empleadoRepository.findByPersona(usuario.getPersona())
                .orElseThrow(() -> new RuntimeException("Usuario no es empleado"));
    }

    private String getClientInfo(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || "".equals(ip)) ip = request.getRemoteAddr();
        return "IP: " + ip;
    }

    @GetMapping("/hoy")
    public ResponseEntity<?> getAsistenciaHoy() {
        try {
            Empleado emp = getEmpleadoActual();
            return ResponseEntity.ok(asistenciaRepository.findByEmpleadoAndFecha(emp, LocalDate.now()).orElse(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- MÉTODO PRINCIPAL CON LA NUEVA LÓGICA ---
    @PostMapping("/marcar")
    public ResponseEntity<?> marcarAsistencia(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Empleado emp = getEmpleadoActual();
            String tipo = (String) payload.get("tipo");
            String obsUsuario = (String) payload.get("observacion");
            boolean confirmado = payload.containsKey("confirmado") && (boolean) payload.get("confirmado");

            LocalDate hoy = LocalDate.now();
            LocalDateTime ahora = LocalDateTime.now();
            LocalTime horaActual = ahora.toLocalTime();
            String infoCliente = getClientInfo(request);

            Asistencia asistencia = asistenciaRepository.findByEmpleadoAndFecha(emp, hoy).orElse(new Asistencia());
            asistencia.setEmpleado(emp);
            asistencia.setFecha(hoy);

            // ==========================================
            //            LÓGICA DE ENTRADA
            // ==========================================
            if ("ENTRADA".equals(tipo)) {
                // 1. Bloqueo estricto antes de las 8:00 AM
                if (horaActual.isBefore(HORA_INICIO_JORNADA)) {
                    return ResponseEntity.badRequest().body(Map.of("message", "No se permite marcar entrada antes de las 8:00 AM."));
                }

                if (asistencia.getHoraEntrada() != null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Entrada ya registrada."));
                }

                asistencia.setHoraEntrada(ahora);
                asistencia.setOrigenEntrada(infoCliente);

                // --- EVALUACIÓN DE ESTADO SEGÚN HORA ---

                // NUEVA REGLA: Si marca entrada después de la hora de salida (5:00 PM)
                if (horaActual.isAfter(HORA_SALIDA_OFICIAL)) {
                    asistencia.setEstado("FALTA");
                    appendObservacion(asistencia, "Falta injustificada");
                }
                // Regla existente: Después de las 10:00 AM
                else if (horaActual.isAfter(HORA_LIMITE_TARDANZA)) {
                    asistencia.setEstado("FALTA");
                    appendObservacion(asistencia, "Entrada muy tarde injustificada");
                }
                // Regla existente: Entre 8:31 y 10:00
                else if (horaActual.isAfter(HORA_FIN_TOLERANCIA)) {
                    asistencia.setEstado("TARDANZA");
                }
                // Regla existente: Entre 8:00 y 8:30
                else {
                    asistencia.setEstado("PUNTUAL");
                }

            }
            // ==========================================
            //            LÓGICA DE SALIDA
            // ==========================================
            else if ("SALIDA".equals(tipo)) {
                if (asistencia.getHoraEntrada() == null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Debe marcar entrada primero."));
                }
                if (asistencia.getHoraSalida() != null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Salida ya registrada."));
                }

                // Salida antes de las 5:00 PM
                if (horaActual.isBefore(HORA_SALIDA_OFICIAL)) {
                    if (!confirmado) {
                        return ResponseEntity.ok(Map.of(
                                "status", "CONFIRMATION_REQUIRED",
                                "message", "Salida antes de las 5:00 PM se registrará como FALTA (Salida injustificada). ¿Continuar?"
                        ));
                    }
                    // Si confirma, se marca FALTA
                    asistencia.setEstado("FALTA");
                    appendObservacion(asistencia, "Salida injustificada");
                }

                asistencia.setHoraSalida(ahora);
                asistencia.setOrigenSalida(infoCliente);
            }

            // Guardar observación manual del usuario si existe
            if (obsUsuario != null && !obsUsuario.trim().isEmpty()) {
                appendObservacion(asistencia, "[" + tipo + "] " + obsUsuario);
            }

            Asistencia asistenciaGuardada = asistenciaRepository.save(asistencia);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Marcación registrada. Estado: " + asistencia.getEstado());
            response.put("data", asistenciaGuardada);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private void appendObservacion(Asistencia a, String nuevaObs) {
        if (a.getObservacion() != null && !a.getObservacion().isEmpty()) {
            a.setObservacion(a.getObservacion() + " | " + nuevaObs);
        } else {
            a.setObservacion(nuevaObs);
        }
    }

    // --- MÉTODOS DE CONSULTA (SIN CAMBIOS) ---
    @GetMapping("/mis-registros")
    public ResponseEntity<?> getMisRegistros() {
        try {
            Empleado emp = getEmpleadoActual();
            List<Asistencia> lista = asistenciaRepository.findAll().stream()
                    .filter(a -> a.getEmpleado().getIdEmpleado().equals(emp.getIdEmpleado()))
                    .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                    .limit(30)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/reporte-diario")
    public ResponseEntity<?> getReporteDiario(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate targetDate = (fecha != null) ? fecha : LocalDate.now();
        List<Empleado> todosEmpleados = empleadoRepository.findAll();
        List<Map<String, Object>> reporte = new ArrayList<>();

        for (Empleado emp : todosEmpleados) {
            Optional<Asistencia> asisOpt = asistenciaRepository.findByEmpleadoAndFecha(emp, targetDate);
            Map<String, Object> fila = new HashMap<>();
            fila.put("idEmpleado", emp.getIdEmpleado());
            fila.put("empleadoNombre", emp.getPersona().getNombres());
            fila.put("departamento", emp.getDepartamento().getNombre());

            if (asisOpt.isPresent()) {
                Asistencia a = asisOpt.get();
                fila.put("idAsistencia", a.getIdAsistencia());
                fila.put("horaEntrada", a.getHoraEntrada());
                fila.put("horaSalida", a.getHoraSalida());
                fila.put("estado", a.getEstado());
                fila.put("ip", a.getOrigenEntrada());
            } else {
                fila.put("horaEntrada", null);
                fila.put("horaSalida", null);
                fila.put("estado", "INASISTENCIA");
            }
            reporte.add(fila);
        }
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/historial/{idEmpleado}")
    public ResponseEntity<?> getHistorialEmpleado(@PathVariable Integer idEmpleado) {
        List<Asistencia> lista = asistenciaRepository.findAll().stream()
                .filter(a -> a.getEmpleado().getIdEmpleado().equals(idEmpleado))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarRegistro(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Asistencia asis = asistenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
        if (payload.containsKey("estado")) asis.setEstado((String) payload.get("estado"));
        if (payload.containsKey("observacion")) asis.setObservacion((String) payload.get("observacion"));
        if (payload.containsKey("horaEntrada") && payload.get("horaEntrada") != null) {
            asis.setHoraEntrada(LocalDateTime.parse((String)payload.get("horaEntrada")));
        }
        if (payload.containsKey("horaSalida") && payload.get("horaSalida") != null) {
            asis.setHoraSalida(LocalDateTime.parse((String)payload.get("horaSalida")));
        }
        asistenciaRepository.save(asis);
        return ResponseEntity.ok(Map.of("message", "Registro actualizado."));
    }
}