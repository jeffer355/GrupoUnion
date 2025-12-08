package utp.edu.pe.GrupoUnion.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.payload.LoginRequest;
import utp.edu.pe.GrupoUnion.payload.LoginResponse;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;
import utp.edu.pe.GrupoUnion.service.EmailService;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // PASO 1: Validar credenciales iniciales
    @PostMapping("/login-step1")
    public ResponseEntity<?> loginStep1(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (Boolean.TRUE.equals(usuario.getRequiereCambioPass())) {
                return ResponseEntity.ok(Map.of("status", "CHANGE_PASSWORD_REQUIRED", "message", "Debe cambiar su contraseña."));
            }

            return generarYEnviarToken(usuario);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Credenciales incorrectas"));
        }
    }

    // PASO 2: Cambiar contraseña (Primer ingreso)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("newPassword");

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        usuario.setHashPass(passwordEncoder.encode(newPassword));
        usuario.setRequiereCambioPass(false); // Ya no es primer ingreso
        usuarioRepository.save(usuario);

        return generarYEnviarToken(usuario);
    }

    // PASO 3: Verificar Token y Login Final
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> request,
                                       HttpServletRequest req, HttpServletResponse res) {
        String username = request.get("username");
        String tokenIngresado = request.get("token");

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        if (usuario != null && tokenIngresado.equals(usuario.getToken2fa())) {
            // Token correcto: Limpiamos y logueamos
            usuario.setToken2fa(null);
            usuarioRepository.save(usuario);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    usuario.getUsername(), null, List.of(() -> usuario.getRol().getNombre())
            );

            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, req, res);

            String role = usuario.getRol().getNombre();
            String redirectUrl = role.equals("ADMIN") ? "/admin" : "/empleado-dashboard";

            return ResponseEntity.ok(new LoginResponse("success", "Bienvenido", role, redirectUrl, username));
        }

        return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Código incorrecto"));
    }

    private ResponseEntity<?> generarYEnviarToken(Usuario usuario) {
        String token = String.valueOf(new Random().nextInt(900000) + 100000);
        usuario.setToken2fa(token);
        usuarioRepository.save(usuario);

        // ENVIO REAL AL CORREO
        emailService.enviarToken(usuario.getUsername(), token);

        return ResponseEntity.ok(Map.of("status", "2FA_REQUIRED", "message", "Código enviado a " + usuario.getUsername()));
    }
}