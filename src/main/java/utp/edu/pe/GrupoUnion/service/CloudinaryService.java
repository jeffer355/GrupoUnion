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

    // Inyección por constructor (Best Practice)
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Método para subir archivos desde el Frontend
    public String uploadFile(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "grupo_union_documentos"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    // Método para subir bytes (PDFs generados en Backend)
    public String uploadBytes(byte[] bytes, String fileName) throws IOException {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "grupo_union_boletas",
                "public_id", fileName
        );
        Map uploadResult = cloudinary.uploader().upload(bytes, params);
        return (String) uploadResult.get("secure_url");
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}