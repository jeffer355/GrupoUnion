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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utp.edu.pe.GrupoUnion.payload.LoginRequest;
import utp.edu.pe.GrupoUnion.payload.LoginResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    // --- HERRAMIENTAS PARA GUARDAR LA SESIÓN MANUALMENTE (ESTO FALTABA) ---
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) { // Inyectamos Request y Response
        try {
            // 1. Autenticar credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // 2. Crear el contexto de seguridad
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);

            // 3. --- EL PASO MÁGICO QUE FALTABA ---
            // Guardamos el contexto explícitamente. ESTO GENERA LA COOKIE "JSESSIONID"
            securityContextRepository.saveContext(context, request, response);

            // 4. Obtener rol y redirección
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.isEmpty() ? "" : authorities.iterator().next().getAuthority().replace("ROLE_", "");

            // Aseguramos mayúsculas
            if (role.equalsIgnoreCase("ROLE_ADMIN")) role = "ADMIN";
            if (role.equalsIgnoreCase("ROLE_EMPLEADO")) role = "EMPLEADO";

            String redirectUrl = "/";
            if ("ADMIN".equals(role)) {
                redirectUrl = "/admin-dashboard";
            } else if ("EMPLEADO".equals(role)) {
                redirectUrl = "/empleado-dashboard";
            }

            LoginResponse loginResponse = new LoginResponse(
                    "success",
                    "Login exitoso",
                    role,
                    redirectUrl,
                    authentication.getName()
            );

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(new LoginResponse(
                    "error",
                    "Credenciales incorrectas o usuario inactivo",
                    "", "", ""
            ));
        }
    }
}