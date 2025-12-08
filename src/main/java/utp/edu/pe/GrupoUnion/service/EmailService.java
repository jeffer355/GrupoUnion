package utp.edu.pe.GrupoUnion.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            // 1. Crear el mensaje Mime para soportar HTML
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Código de Verificación - Grupo Unión");

            // 2. Construir el contenido HTML profesional
            String htmlContent = construirHtmlCorreo(token);

            // 3. Establecer el contenido como HTML (el 'true' es importante)
            helper.setText(htmlContent, true);

            // 4. Enviar
            javaMailSender.send(mimeMessage);
            System.out.println(">>> ✅ Correo HTML enviado a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println(">>> ❌ Error enviando correo HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String construirHtmlCorreo(String token) {
        // Colores corporativos (basados en la imagen que pasaste)
        String colorPrimario = "#003057"; // Azul oscuro tipo banco
        String colorSecundario = "#009c3b"; // Verde tipo Interbank
        String colorFondo = "#f4f4f4";

        // URLs de imágenes (REEMPLÁZALAS CON TUS LINKS REALES DE CLOUDINARY O TU SERVIDOR)
        // Si no tienes imágenes aún, usa estas de ejemplo o déjalas vacías, pero se verá mejor con ellas.
        String urlLogo = "https://i.postimg.cc/L6g22RSc/GRUPO-UNION-Photoroom.png"; // Icono genérico de empresa
        String urlBanner = "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1632&q=80"; // Imagen de oficina

        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: " + colorFondo + "; margin: 0; padding: 0;'>" +
                "  <div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +

                // --- HEADER CON LOGO ---
                "    <div style='padding: 20px; text-align: left; border-bottom: 1px solid #eeeeee;'>" +
                "      <img src='" + urlLogo + "' alt='Grupo Unión' style='height: 40px; vertical-align: middle;'>" +
                "      <span style='font-size: 20px; font-weight: bold; color: " + colorPrimario + "; vertical-align: middle; margin-left: 10px;'>Grupo Unión</span>" +
                "    </div>" +

                // --- BANNER DE IMAGEN (Opcional, como en la foto) ---
                "    <div style='width: 100%; height: 150px; overflow: hidden;'>" +
                "      <img src='" + urlBanner + "' style='width: 100%; object-fit: cover;' alt='Seguridad'>" +
                "    </div>" +

                // --- CUERPO DEL MENSAJE ---
                "    <div style='padding: 40px 30px; color: #333333;'>" +
                "      <h2 style='color: " + colorPrimario + "; margin-top: 0;'>Hola, Usuario:</h2>" +
                "      <p style='font-size: 16px; line-height: 1.5;'>Para continuar con tu acceso al sistema, por favor ingresa el siguiente <strong style='color: " + colorSecundario + ";'>código de verificación</strong> en la web.</p>" +

                // --- CAJA DEL CÓDIGO (Similar a la imagen) ---
                "      <div style='margin: 30px 0; text-align: center;'>" +
                "        <div style='display: inline-block; background-color: #f8f9fa; border: 1px solid #e9ecef; border-radius: 8px; padding: 20px 40px;'>" +
                "          <span style='display: block; font-size: 14px; color: #6c757d; margin-bottom: 5px;'>Tu código de verificación es:</span>" +
                "          <span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #000000;'>" + token + "</span>" +
                "        </div>" +
                "      </div>" +

                "      <p style='font-size: 14px; color: #666666;'>Si es tu primera vez ingresando, el sistema te solicitará cambiar tu contraseña inmediatamente.</p>" +
                "    </div>" +

                // --- FOOTER VERDE (Como en la imagen) ---
                "    <div style='background-color: " + colorSecundario + "; padding: 15px; text-align: center; color: #ffffff;'>" +
                "      <p style='margin: 0; font-size: 14px; font-weight: bold;'>Seguridad y Confianza - Grupo Unión</p>" +
                "    </div>" +

                // --- DISCLAIMER ---
                "    <div style='padding: 20px; text-align: center; font-size: 12px; color: #999999;'>" +
                "      <p>Nunca te pediremos tus claves por correo. Si no solicitaste este código, ignora este mensaje.</p>" +
                "    </div>" +

                "  </div>" +
                "</body>" +
                "</html>";
    }
}