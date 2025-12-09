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

    // --- REGLAS DE NEGOCIO ---
    private final LocalTime HORA_TOLERANCIA = LocalTime.of(8, 15); // 08:15 AM
    private final LocalTime HORA_SALIDA_OFICIAL = LocalTime.of(17, 0); // 05:00 PM

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
        return "IP: " + ip; // Aquí podrías agregar hostname si la red lo permite
    }

    // 1. ESTADO DE HOY (Para el panel del empleado)
    @GetMapping("/hoy")
    public ResponseEntity<?> getAsistenciaHoy() {
        try {
            Empleado emp = getEmpleadoActual();
            return ResponseEntity.ok(asistenciaRepository.findByEmpleadoAndFecha(emp, LocalDate.now()).orElse(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 2. MARCAR ASISTENCIA (Lógica de negocio solicitada)
    @PostMapping("/marcar")
    public ResponseEntity<?> marcarAsistencia(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Empleado emp = getEmpleadoActual();
            String tipo = (String) payload.get("tipo");
            boolean confirmado = payload.containsKey("confirmado") && (boolean) payload.get("confirmado");

            LocalDate hoy = LocalDate.now();
            LocalDateTime ahora = LocalDateTime.now();
            String infoCliente = getClientInfo(request);

            Asistencia asistencia = asistenciaRepository.findByEmpleadoAndFecha(emp, hoy).orElse(new Asistencia());
            asistencia.setEmpleado(emp);
            asistencia.setFecha(hoy);

            if ("ENTRADA".equals(tipo)) {
                if (asistencia.getHoraEntrada() != null) return ResponseEntity.badRequest().body(Map.of("message", "Entrada ya registrada."));

                asistencia.setHoraEntrada(ahora);
                asistencia.setOrigenEntrada(infoCliente);

                // Lógica Tardanza
                if (ahora.toLocalTime().isAfter(HORA_TOLERANCIA)) {
                    asistencia.setEstado("TARDANZA");
                } else {
                    asistencia.setEstado("PUNTUAL");
                }

            } else if ("SALIDA".equals(tipo)) {
                if (asistencia.getHoraEntrada() == null) return ResponseEntity.badRequest().body(Map.of("message", "Debe marcar entrada primero."));

                // Lógica Salida Anticipada
                if (ahora.toLocalTime().isBefore(HORA_SALIDA_OFICIAL) && !confirmado) {
                    return ResponseEntity.ok(Map.of(
                            "status", "CONFIRMATION_REQUIRED",
                            "message", "Está intentando salir antes de las 17:00. ¿Confirmar salida anticipada?"
                    ));
                }

                asistencia.setHoraSalida(ahora);
                asistencia.setOrigenSalida(infoCliente);

                if (ahora.toLocalTime().isBefore(HORA_SALIDA_OFICIAL)) {
                    // Si sale temprano, actualizamos estado (preservando si ya tenía tardanza)
                    String estadoPrevio = asistencia.getEstado();
                    asistencia.setEstado(estadoPrevio + " / SALIDA ANTICIPADA");
                }
            }

            asistenciaRepository.save(asistencia);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Marcación registrada."));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // 3. REPORTE GENERAL DIARIO (Para Admin - Vista A)
    @GetMapping("/reporte-diario")
    public ResponseEntity<?> getReporteDiario(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate targetDate = (fecha != null) ? fecha : LocalDate.now();

        // Obtenemos TODOS los empleados para saber quién faltó
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

    // 4. HISTORIAL INDIVIDUAL (Para Admin - Vista B)
    @GetMapping("/historial/{idEmpleado}")
    public ResponseEntity<?> getHistorialEmpleado(@PathVariable Integer idEmpleado) {
        // Asumiendo que agregaste findByEmpleadoIdEmpleado en AsistenciaRepository
        // Si no, usa findAll y filtra (menos eficiente pero funciona sin tocar Repo)
        List<Asistencia> lista = asistenciaRepository.findAll().stream()
                .filter(a -> a.getEmpleado().getIdEmpleado().equals(idEmpleado))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha())) // Orden descendente
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    // 5. EDICIÓN / JUSTIFICACIÓN (Para Admin - Vista C)
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarRegistro(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Asistencia asis = asistenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));

        if (payload.containsKey("estado")) asis.setEstado((String) payload.get("estado"));
        if (payload.containsKey("observacion")) asis.setObservacion((String) payload.get("observacion"));

        // Permitir corrección de horas si es necesario
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