package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
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

    public DashboardController(UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // Endpoint para ADMIN
    @GetMapping("/admin/dashboard-data")
    public ResponseEntity<?> getAdminData() {
        // 1. Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // El email

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        Map<String, Object> data = new HashMap<>();
        if (usuario != null) {
            data.put("nombreCompleto", usuario.getPersona().getNombres());
            data.put("rol", "Administrador");
            data.put("email", usuario.getUsername());
            // Puedes agregar más datos si la entidad Persona tuviera apellidos
        }

        // Datos estadísticos (puedes conectarlos a BD luego, por ahora quemados o lógica real)
        data.put("stats_users", 15);
        data.put("stats_areas", 4);

        return ResponseEntity.ok(data);
    }

    // Endpoint para EMPLEADO
    @GetMapping("/empleado/dashboard-data")
    public ResponseEntity<?> getEmpleadoData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        Map<String, Object> data = new HashMap<>();

        if (usuario != null) {
            // Datos básicos del usuario
            data.put("username", usuario.getUsername());
            data.put("email", usuario.getPersona().getEmail());
            data.put("telefono", usuario.getPersona().getTelefono());

            // Buscar datos laborales (Empleado) usando la Persona del Usuario
            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersona(usuario.getPersona());

            if (empleadoOpt.isPresent()) {
                Empleado emp = empleadoOpt.get();
                data.put("nombres", emp.getPersona().getNombres()); // Ojo: tu entidad Persona solo tiene 'nombres'
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