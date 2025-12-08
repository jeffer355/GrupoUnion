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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            // --- FOTO URL ---
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
            // --- FOTO URL ---
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
}