package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.repository.DepartamentoRepository;
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCrudController {

    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminCrudController(UsuarioRepository usuarioRepository,
                               DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- CRUD USUARIOS ---
    @GetMapping("/usuarios")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/usuarios") // Ejemplo simplificado
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        usuario.setHashPass(passwordEncoder.encode(usuario.getHashPass()));
        return usuarioRepository.save(usuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- CRUD AREAS (DEPARTAMENTOS) ---
    @GetMapping("/areas")
    public List<Departamento> getAllAreas() {
        return departamentoRepository.findAll();
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
        departamentoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- CRUD EMPLEADOS ---
    @GetMapping("/empleados")
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    // Aquí puedes agregar POST y PUT para empleados siguiendo la misma lógica
}