package utp.edu.pe.GrupoUnion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // 👈 NECESARIO para STATELESS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 👈 NECESARIO para el filtro JWT
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utp.edu.pe.GrupoUnion.service.impl.UserDetailsServiceImpl;
import utp.edu.pe.GrupoUnion.filter.JwtAuthFilter; // 👈 IMPORTANTE: Debes tener esta clase creada

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthHandlers.CustomSuccessHandler successHandler;
    private final AuthHandlers.CustomFailureHandler failureHandler;
    private final JwtAuthFilter jwtAuthFilter; // 👈 Inyección del filtro JWT

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          AuthHandlers.CustomSuccessHandler successHandler,
                          AuthHandlers.CustomFailureHandler failureHandler,
                          JwtAuthFilter jwtAuthFilter) { // 👈 Constructor actualizado
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.jwtAuthFilter = jwtAuthFilter; // 👈 Asignación
    }

    // ELIMINAMOS el @Bean public CookieSerializer() {...}
    // Ya no se necesita al migrar a JWT.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración de CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Desactivar CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3. CRÍTICO: Configurar el manejo de sesión como SIN ESTADO (STATELESS)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Reglas de Autorización
                .authorizeHttpRequests(auth -> auth
                        // --- RUTAS PÚBLICAS Y LOGIN (acceso al AuthController) ---
                        .requestMatchers("/actuator/**", "/api/public/**", "/api/auth/**").permitAll()

                        // --- RUTAS PROTEGIDAS POR ROL (Ahora protegidas por JWT) ---
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/empleado/**").hasAuthority("EMPLEADO")

                        // Todo lo demás requiere un token válido
                        .anyRequest().authenticated()
                )

                // 5. INYECCIÓN DEL FILTRO JWT (CRÍTICO para validar el token)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Eliminamos el .logout que manipulaba la cookie JSESSIONID, ya que no aplica para JWT.
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://front-grupo-union.vercel.app"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (Necesarios para enviar el token JWT: Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

        // Permitir credenciales (cookies) -> Cambiado a 'false' ya que usamos JWT
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}