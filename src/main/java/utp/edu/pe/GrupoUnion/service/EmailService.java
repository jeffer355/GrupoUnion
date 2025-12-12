package utp.edu.pe.GrupoUnion.service; // <-- Manteniendo tu paquete original

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;


    @Value("${spring.mail.username:no-responder@grupounion.pe}")
    private String remitente;

    // URL Fija de la API de Brevo para enviar correos transaccionales
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";


    // Constructor eliminado: Ya no necesitamos inyectar JavaMailSender

    public void enviarToken(String destinatario, String token) {
        try {
            // 1. Construir el contenido HTML profesional (APARIENCIA SIN CAMBIOS)
            String htmlContent = construirHtmlCorreo(token);

            // 2. Crear el objeto JSON para el cuerpo de la petición API
            JSONObject payload = new JSONObject();

            // Remitente (De: Grupo Unión)
            JSONObject sender = new JSONObject();
            sender.put("email", remitente);
            sender.put("name", "Grupo Unión");
            payload.put("sender", sender);

            // Destinatario (Para: el usuario)
            JSONArray toArray = new JSONArray();
            JSONObject to = new JSONObject();
            to.put("email", destinatario);
            toArray.put(to);
            payload.put("to", toArray);

            // Asunto y Contenido
            payload.put("subject", "Código de Verificación - Grupo Unión");
            payload.put("htmlContent", htmlContent);

            // 3. Ejecutar la petición POST a la API de Brevo usando Unirest
            HttpResponse<String> response = Unirest.post(BREVO_API_URL)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    // Autenticación crucial
                    .header("api-key", brevoApiKey)
                    .body(payload.toString())
                    .asString();

            // 4. Revisar la respuesta
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                System.out.println(">>> ✅ Correo HTML enviado via Brevo API a: " + destinatario);
            } else {
                System.err.println(">>> ❌ Error enviando correo HTML via Brevo. Status: " + response.getStatus() + " Body: " + response.getBody());
                // Lanzar un error para que el flujo de negocio lo maneje
                throw new RuntimeException("Fallo en el envío de correo con Brevo: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println(">>> ❌ Error fatal en la comunicación API/Brevo: " + e.getMessage());
            e.printStackTrace();
            // Esto asegura que el error se propague al controlador/servicio de login
            throw new RuntimeException("Fallo al intentar enviar el token de seguridad.", e);
        }
    }

    // Método de construcción de HTML se mantiene IDÉNTICO
    private String construirHtmlCorreo(String token) {
        // Colores corporativos (basados en la imagen que pasaste)
        String colorPrimario = "#003057"; // Azul oscuro tipo banco
        String colorSecundario = "#009c3b"; // Verde tipo Interbank
        String colorFondo = "#f4f4f4";

        // URLs de imágenes (REEMPLÁZALAS CON TUS LINKS REALES DE CLOUDINARY O TU SERVIDOR)
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