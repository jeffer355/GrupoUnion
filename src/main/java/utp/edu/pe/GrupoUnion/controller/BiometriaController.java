package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.payload.BiometriaDTO;
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;
import utp.edu.pe.GrupoUnion.service.BiometriaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/biometria")
public class BiometriaController {

    private final BiometriaService biometriaService;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public BiometriaController(BiometriaService biometriaService, UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository) {
        this.biometriaService = biometriaService;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    private Empleado getEmpleadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        return empleadoRepository.findByPersona(usuario.getPersona())
                .orElseThrow(() -> new RuntimeException("Usuario no es empleado"));
    }

    @GetMapping("/estado")
    public ResponseEntity<?> verificarEstado() {
        Empleado emp = getEmpleadoActual();
        boolean enrolado = biometriaService.estaEnrolado(emp.getIdEmpleado());
        return ResponseEntity.ok(Map.of("enrolado", enrolado));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarRostro(@RequestBody BiometriaDTO dto) {
        try {
            Empleado emp = getEmpleadoActual();
            biometriaService.enrolarEmpleado(emp.getIdEmpleado(), dto.getEmbedding());
            return ResponseEntity.ok(Map.of("message", "Rostro registrado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al registrar: " + e.getMessage()));
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarAsistencia(@RequestBody BiometriaDTO dto) {
        try {
            Empleado emp = getEmpleadoActual();
            String resultado = biometriaService.procesarAsistencia(emp.getIdEmpleado(), dto.getEmbedding(), dto.getTipo());

            if ("MATCH".equals(resultado)) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "Asistencia registrada. Hola " + emp.getPersona().getNombres()));
            } else {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Rostro no coincide."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}