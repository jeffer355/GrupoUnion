package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.repository.DepartamentoRepository; // Importante
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository; // Nuevo

    public DashboardController(UsuarioRepository usuarioRepository,
                               EmpleadoRepository empleadoRepository,
                               DepartamentoRepository departamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    // Endpoint para ADMIN
    @GetMapping("/admin/dashboard-data")
    public ResponseEntity<?> getAdminData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        Map<String, Object> data = new HashMap<>();

        // 1. Datos del Perfil (Barra Lateral)
        if (usuario != null) {
            data.put("nombreCompleto", usuario.getPersona().getNombres());
            data.put("rol", "Administrador");
            data.put("email", usuario.getUsername());

            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersona(usuario.getPersona());
            if (empleadoOpt.isPresent()) {
                data.put("departamento", empleadoOpt.get().getDepartamento().getNombre());
            } else {
                data.put("departamento", "Administración General");
            }
        }

        // 2. DATOS ESTADÍSTICOS REALES (KPIs)
        // Usamos .count() para contar los registros en la BD
        data.put("stats_users", usuarioRepository.count());
        data.put("stats_areas", departamentoRepository.count());
        data.put("stats_employees", empleadoRepository.count());

        // Si tienes tabla de asistencias, inyecta su repositorio y usa .count()
        // Por ahora lo dejamos en 0 o un valor fijo si no tienes el repo a mano
        data.put("stats_attendances", 0);

        return ResponseEntity.ok(data);
    }

    // Endpoint para EMPLEADO (Sin cambios)
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
            }
        }
        return ResponseEntity.ok(data);
    }
}