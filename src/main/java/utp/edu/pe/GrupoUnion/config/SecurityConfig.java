package utp.edu.pe.GrupoUnion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utp.edu.pe.GrupoUnion.service.impl.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List; // Importación necesaria para List.of

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthHandlers.CustomSuccessHandler successHandler;
    private final AuthHandlers.CustomFailureHandler failureHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          AuthHandlers.CustomSuccessHandler successHandler,
                          AuthHandlers.CustomFailureHandler failureHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración de CORS (Usa el Bean de abajo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Desactivar CSRF (Necesario para APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Reglas de Autorización
                .authorizeHttpRequests(auth -> auth
                        // --- VITAL PARA RENDER ---
                        .requestMatchers("/actuator/**").permitAll() // Permite el Health Check

                        // --- RUTAS PÚBLICAS ---
                        .requestMatchers("/api/public/**", "/api/auth/**").permitAll()

                        // --- RUTAS PROTEGIDAS POR ROL ---
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/empleado/**").hasAuthority("EMPLEADO")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // 4. Soporte Básico (Útil para depurar si falla el JWT)
                .httpBasic(Customizer.withDefaults())

                // 5. Configuración de Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ** APLICANDO SOLUCIÓN CORS ESPECÍFICA PARA TU FRONTEND DE VERCEL **
        // Se reemplaza List.of("*") por la URL exacta para mejor seguridad
        // y compatibilidad con navegadores/autenticación.
        configuration.setAllowedOrigins(List.of("https://front-grupo-union.vercel.app"));
        // NOTA: Si tu frontend está usando http://localhost:PORT para desarrollo,
        // puedes agregar esa URL aquí temporalmente: List.of("https://front-grupo-union.vercel.app", "http://localhost:3000")

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (Necesarios para enviar el token JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

        // Permitir credenciales (cookies, headers de auth)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración CORS a todas las rutas de la API (/**)
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