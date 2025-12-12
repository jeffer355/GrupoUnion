package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.repository.DepartamentoRepository;
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;
import utp.edu.pe.GrupoUnion.payload.AreaResumenDTO; // Asegúrate de importar esto

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;

    public DashboardController(UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository, DepartamentoRepository departamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @GetMapping("/admin/dashboard-data")
    public ResponseEntity<?> getAdminData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        Map<String, Object> data = new HashMap<>();

        if (usuario != null) {
            data.put("nombreCompleto", usuario.getPersona().getNombres());
            data.put("rol", "Administrador");
            data.put("email", usuario.getUsername());
            data.put("fotoUrl", usuario.getPersona().getFotoUrl());

            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersona(usuario.getPersona());
            data.put("departamento", empleadoOpt.map(e -> e.getDepartamento().getNombre()).orElse("Administración General"));
        }
        data.put("stats_users", usuarioRepository.count());
        data.put("stats_areas", departamentoRepository.count());
        data.put("stats_employees", empleadoRepository.count());
        data.put("stats_attendances", 0);
        return ResponseEntity.ok(data);
    }

    // --- NUEVO ENDPOINT: WIDGET CUMPLEAÑOS ---
    @GetMapping("/admin/widgets/birthdays")
    public ResponseEntity<?> getBirthdays() {
        int currentMonth = LocalDate.now().getMonthValue();
        List<Empleado> birthdayEmployees = empleadoRepository.findByBirthdayMonth(currentMonth);

        // Simplificamos la respuesta para el frontend
        List<Map<String, Object>> response = birthdayEmployees.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nombres", e.getPersona().getNombres());
            map.put("fechaNac", e.getPersona().getFechaNac());
            map.put("fotoUrl", e.getPersona().getFotoUrl());
            map.put("dia", e.getPersona().getFechaNac().getDayOfMonth());
            map.put("cargo", e.getCargo().getNombre());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // --- NUEVO ENDPOINT: WIDGET GRÁFICO (REUTILIZANDO TU QUERY EXISTENTE) ---
    @GetMapping("/admin/widgets/chart-areas")
    public ResponseEntity<?> getChartData() {
        List<AreaResumenDTO> stats = departamentoRepository.obtenerAreasConConteo();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/empleado/dashboard-data")
    public ResponseEntity<?> getEmpleadoData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        Map<String, Object> data = new HashMap<>();

        if (usuario != null) {
            data.put("username", usuario.getUsername());
            data.put("email", usuario.getPersona().getEmail());
            data.put("telefono", usuario.getPersona().getTelefono());
            data.put("fotoUrl", usuario.getPersona().getFotoUrl());

            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersona(usuario.getPersona());
            if (empleadoOpt.isPresent()) {
                Empleado emp = empleadoOpt.get();
                data.put("nombres", emp.getPersona().getNombres());
                data.put("cargo", emp.getCargo().getNombre());
                data.put("departamento", emp.getDepartamento().getNombre());
                data.put("fechaIngreso", emp.getFechaIngreso());
            } else {
                data.put("nombres", usuario.getPersona().getNombres());
                data.put("cargo", "Sin asignar");
                data.put("departamento", "General");
            }
        }
        return ResponseEntity.ok(data);
    }

    // --- WIDGETS PANEL EMPLEADO ---

    @GetMapping("/empleado/widgets/birthdays")
    public ResponseEntity<?> getEmployeeBirthdays() {
        // Reutilizamos la lógica del admin para mantener consistencia
        int currentMonth = LocalDate.now().getMonthValue();
        List<Empleado> birthdayEmployees = empleadoRepository.findByBirthdayMonth(currentMonth);

        List<Map<String, Object>> response = birthdayEmployees.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nombres", e.getPersona().getNombres());
            map.put("fotoUrl", e.getPersona().getFotoUrl());
            map.put("dia", e.getPersona().getFechaNac().getDayOfMonth());
            map.put("cargo", e.getCargo().getNombre());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/empleado/widgets/policies")
    public ResponseEntity<?> getPolicies() {
        // Datos simulados o desde BD según preferencia. Aquí configurados en backend como solicitado.
        List<Map<String, String>> policies = List.of(
                Map.of("titulo", "Cierre de Planilla", "mensaje", "El cierre de planilla es el día 25 de cada mes. Registra tus asistencias a tiempo.", "icono", "fa-calendar-check", "color", "#003057"),
                Map.of("titulo", "Solicitud de Vacaciones", "mensaje", "Las vacaciones deben solicitarse con 15 días de anticipación mediante el módulo de solicitudes.", "icono", "fa-plane-departure", "color", "#10b981"),
                Map.of("titulo", "Código de Vestimenta", "mensaje", "Se recuerda asistir con vestimenta casual de negocios de lunes a jueves.", "icono", "fa-user-tie", "color", "#f59e0b"),
                Map.of("titulo", "Horario de Atención RRHH", "mensaje", "Lunes a Viernes de 9:00 AM a 1:00 PM y de 3:00 PM a 6:00 PM.", "icono", "fa-clock", "color", "#6b7280")
        );
        return ResponseEntity.ok(policies);
    }
}