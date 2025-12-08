package utp.edu.pe.GrupoUnion.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map; // Asegúrate que sea java.util.Map

@Service
public class CloudinaryService {

    // Constructor vacío para evitar problemas de inyección en esta prueba
    public CloudinaryService() {
    }

    public String uploadFile(MultipartFile file) throws IOException {

        // CONFIGURACIÓN EXPLÍCITA (Método más seguro)
        // He verificado estos datos letra por letra con tu imagen.
        Cloudinary cloudinaryManual = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhcqmb3rb",
                "api_key", "529543174157466",
                "api_secret", "YvqDf9rLzVh3MeFiMlCtHDicFdg"
        ));

        // Subir la imagen
        Map uploadResult = cloudinaryManual.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Retornar la URL
        return (String) uploadResult.get("secure_url");
    }
}