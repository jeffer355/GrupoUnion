package utp.edu.pe.GrupoUnion.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthHandlers {

    @Component
    public static class CustomSuccessHandler implements AuthenticationSuccessHandler {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.isEmpty() ? "" : authorities.iterator().next().getAuthority();
            String redirectUrl = "/";

            // Lógica de redirección (Angular leerá esto)
            if ("ROLE_ADMIN".equals(role)) {
                redirectUrl = "/admin/dashboard";
            } else if ("ROLE_EMPLEADO".equals(role)) {
                redirectUrl = "/empleado/dashboard";
            }

            Map<String, Object> data = new HashMap<>();
            data.put("status", "success");
            data.put("message", "Login exitoso");
            data.put("role", role);
            data.put("redirectUrl", redirectUrl);
            data.put("username", authentication.getName());

            response.getWriter().write(objectMapper.writeValueAsString(data));
        }
    }

    @Component
    public static class CustomFailureHandler implements AuthenticationFailureHandler {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Error
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("message", "Credenciales incorrectas o usuario inactivo");

            response.getWriter().write(objectMapper.writeValueAsString(data));
        }
    }
}