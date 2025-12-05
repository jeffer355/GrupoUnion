package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utp.edu.pe.GrupoUnion.payload.LoginRequest;
import utp.edu.pe.GrupoUnion.payload.LoginResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


@RestController
@RequestMapping("/api/auth") // A new base path for auth related endpoints
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.isEmpty() ? "" : authorities.iterator().next().getAuthority().replace("ROLE_", "");
            String redirectUrl = "/";

            if ("ADMIN".equals(role)) {
                redirectUrl = "/admin-dashboard"; // Match frontend route path
            } else if ("EMPLEADO".equals(role)) {
                redirectUrl = "/empleado-dashboard"; // Match frontend route path
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
            // This catches UsernameNotFoundException and BadCredentialsException
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body(new LoginResponse(
                    "error",
                    "Credenciales incorrectas o usuario inactivo",
                    "", "", ""
            ));
        }
    }
}
