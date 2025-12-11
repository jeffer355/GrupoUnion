package utp.edu.pe.GrupoUnion.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        // Configuración directa
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhcqmb3rb",
                "api_key", "529543174157466",
                "api_secret", "YvqDf9rLzVh3MeFiMlCtHDicFdg"
        ));
    }

    // Método para subir archivos desde el Frontend (MultipartFile)
    public String uploadFile(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "grupo_union_documentos"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    // --- ESTE ES EL MÉTODO QUE NECESITAS PARA LAS BOLETAS ---
    // Método público para subir bytes (PDFs generados en Backend)
    public String uploadBytes(byte[] bytes, String fileName) throws IOException {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "grupo_union_boletas",
                "public_id", fileName
        );
        // Accedemos a 'uploader()' aquí dentro, donde sí tenemos permiso
        Map uploadResult = cloudinary.uploader().upload(bytes, params);
        return (String) uploadResult.get("secure_url");
    }

    // Getter opcional por si necesitaras acceso directo (aunque es mejor usar uploadBytes)
    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}