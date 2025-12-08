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
            // --- CAMBIO: AHORA APUNTA A TU CORREO PERSONAL ---
            String email = "jeffer355123@gmail.com";
            String newPassword = "admin123";

            System.out.println(">>> 🔧 INICIANDO REPARACIÓN DE CUENTA ADMIN (" + email + ")...");

            Usuario admin = usuarioRepository.findByUsername(email).orElse(null);

            if (admin != null) {
                // 1. Encriptamos la contraseña "fresca"
                String newHash = passwordEncoder.encode(newPassword);

                // 2. Sobrescribimos en el objeto
                admin.setHashPass(newHash);
                admin.setActivo(true);

                // 3. EVITAMOS QUE PIDA CAMBIO DE CONTRASEÑA AL ENTRAR (Opcional, para admin es útil)
                admin.setRequiereCambioPass(false);
                admin.setToken2fa(null); // Limpiamos cualquier token trabado

                // 4. Guardamos en BD
                usuarioRepository.save(admin);

                System.out.println(">>> ✅ ÉXITO: Usuario " + email + " activo.");
                System.out.println(">>> ✅ CONTRASEÑA RESTAURADA A: " + newPassword);

            } else {
                System.out.println(">>> ❌ ERROR: No se encontró el usuario " + email + " en la tabla 'usuario'.");
                System.out.println(">>> NOTA: Este script solo arregla usuarios existentes. Asegúrate de que el registro exista en la BD.");
            }
            System.out.println(">>> ----------------------------------------------------");
        };
    }
}