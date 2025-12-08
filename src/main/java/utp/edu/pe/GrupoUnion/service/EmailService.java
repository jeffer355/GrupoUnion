package utp.edu.pe.GrupoUnion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void enviarToken(String destinatario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(destinatario);
            message.setSubject("🔐 Código de Acceso - Grupo Unión");
            message.setText("Hola,\n\n" +
                    "Para ingresar al sistema, usa este código de verificación:\n\n" +
                    "👉 " + token + " 👈\n\n" +
                    "Si es tu primera vez, el sistema te pedirá cambiar tu contraseña.\n\n" +
                    "Saludos,\nSeguridad Grupo Unión");

            javaMailSender.send(message);
            System.out.println(">>> ✅ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println(">>> ❌ Error enviando correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}