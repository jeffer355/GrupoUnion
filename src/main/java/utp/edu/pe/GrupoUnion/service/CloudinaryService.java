package utp.edu.pe.GrupoUnion.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    public String uploadFile(MultipartFile file) throws IOException {
        // Tu configuración (NO TOCAR)
        Cloudinary cloudinaryManual = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhcqmb3rb",
                "api_key", "529543174157466",
                "api_secret", "YvqDf9rLzVh3MeFiMlCtHDicFdg"
        ));

        // --- CORRECCIÓN CRÍTICA AQUÍ ---
        // "resource_type", "auto" permite que PDFs y documentos se guarden correctamente.
        // Sin esto, los PDFs se rompen y dan Error 401.
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "grupo_union_documentos" // (Opcional) Para ordenarlos en una carpeta
        );

        Map uploadResult = cloudinaryManual.uploader().upload(file.getBytes(), params);

        // Retornamos la URL segura
        return (String) uploadResult.get("secure_url");
    }
}