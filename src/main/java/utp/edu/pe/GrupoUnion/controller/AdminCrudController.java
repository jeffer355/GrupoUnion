package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoDocumento;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import utp.edu.pe.GrupoUnion.payload.AreaResumenDTO;
import utp.edu.pe.GrupoUnion.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminCrudController {

    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository; // NUEVO
    private final PasswordEncoder passwordEncoder;

    public AdminCrudController(UsuarioRepository usuarioRepository,
                               DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository,
                               PersonaRepository personaRepository,
                               TipoDocumentoRepository tipoDocumentoRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- NUEVO ENDPOINT PARA SELECTOR DE DOCUMENTOS ---
    @GetMapping("/tipos-documento")
    public List<TipoDocumento> getAllTiposDoc() {
        return tipoDocumentoRepository.findAll();
    }

    // ================= MÓDULO USUARIOS =================
    @GetMapping("/usuarios")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        try {
            String email = usuario.getUsername();
            if (usuarioRepository.existsByUsername(email)) {
                return ResponseEntity.badRequest().body(crearMensaje("El usuario (email) ya tiene una cuenta registrada."));
            }
            Optional<Persona> personaOpt = personaRepository.findByEmail(email);
            if (personaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(crearMensaje("No existe ninguna persona registrada con el correo: " + email));
            }
            Optional<Empleado> empleadoOpt = empleadoRepository.findByPersona(personaOpt.get());
            if (empleadoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(crearMensaje("La persona existe pero NO es un empleado activo."));
            }

            usuario.setPersona(personaOpt.get());
            usuario.setHashPass(passwordEncoder.encode(usuario.getHashPass()));
            usuario.setActivo(true);
            return ResponseEntity.ok(usuarioRepository.save(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearMensaje("Error interno."));
        }
    }

    @PutMapping("/usuarios")
    public ResponseEntity<?> updateUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ================= MÓDULO EMPLEADOS =================
    @GetMapping("/empleados")
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    @PostMapping("/empleados")
    public ResponseEntity<?> createEmpleado(@RequestBody Empleado empleado) {
        try {
            Persona persona = empleado.getPersona();
            // Validar correo duplicado
            if (persona.getEmail() != null && personaRepository.findByEmail(persona.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(crearMensaje("El correo " + persona.getEmail() + " ya está registrado."));
            }
            // Validar DNI duplicado
            if (persona.getNroDocumento() != null && personaRepository.findByNroDocumento(persona.getNroDocumento()).isPresent()) {
                return ResponseEntity.badRequest().body(crearMensaje("El Nro Documento " + persona.getNroDocumento() + " ya está registrado."));
            }

            Persona personaGuardada = personaRepository.save(persona);
            empleado.setPersona(personaGuardada);
            return ResponseEntity.ok(empleadoRepository.save(empleado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearMensaje("Error al guardar: " + e.getMessage()));
        }
    }

    @PutMapping("/empleados")
    public Empleado updateEmpleado(@RequestBody Empleado empleado) {
        if (empleado.getPersona() != null) {
            personaRepository.save(empleado.getPersona());
        }
        return empleadoRepository.save(empleado);
    }

    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Integer id) {
        empleadoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ================= MÓDULO ÁREAS =================
    @GetMapping("/areas")
    public List<AreaResumenDTO> getAllAreas() {
        return departamentoRepository.obtenerAreasConConteo();
    }

    @GetMapping("/areas/{id}/empleados")
    public List<Empleado> getEmpleadosPorArea(@PathVariable Integer id) {
        return empleadoRepository.findByDepartamentoIdDepartamento(id);
    }

    @PostMapping("/areas")
    public Departamento createArea(@RequestBody Departamento dep) {
        return departamentoRepository.save(dep);
    }

    @PutMapping("/areas/{id}")
    public Departamento updateArea(@PathVariable Integer id, @RequestBody Departamento depDetails) {
        Departamento dep = departamentoRepository.findById(id).orElseThrow();
        dep.setNombre(depDetails.getNombre());
        return departamentoRepository.save(dep);
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<?> deleteArea(@PathVariable Integer id) {
        long count = empleadoRepository.countByDepartamentoIdDepartamento(id);
        if (count > 0) {
            return ResponseEntity.badRequest().body(crearMensaje("No se puede eliminar el área, tiene " + count + " empleados."));
        }
        departamentoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, String> crearMensaje(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("message", mensaje);
        return response;
    }
}