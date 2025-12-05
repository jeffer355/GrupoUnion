package utp.edu.pe.GrupoUnion.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.repository.UsuarioRepository;

@Configuration
public class AdminFixer {

    @Bean
    CommandLineRunner resetAdminPassword(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "admin@grupounion.com";
            String newPassword = "admin123";

            System.out.println(">>> 🔧 INICIANDO REPARACIÓN DE CUENTA ADMIN...");

            Usuario admin = usuarioRepository.findByUsername(email).orElse(null);

            if (admin != null) {
                // 1. Encriptamos la contraseña "fresca"
                String newHash = passwordEncoder.encode(newPassword);

                // 2. Sobrescribimos en el objeto
                admin.setHashPass(newHash);
                admin.setActivo(true);

                // 3. Guardamos en BD (Esto elimina cualquier caracter basura anterior)
                usuarioRepository.save(admin);
                System.out.println("admin@grupounion.com");
                System.out.println(">>> ✅ ÉXITO: Contraseña de admin actualizada a: " + newPassword);

            } else {
                System.out.println(">>> ❌ ERROR: No se encontró el usuario " + email + " en la BD.");
            }
            System.out.println(">>> ----------------------------------------------------");
        };
    }
}
