package utp.edu.pe.GrupoUnion.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhcqmb3rb",
                "api_key", "529543174157466",
                "api_secret", "YvqDf9rLzVh3MeFiMlCtHDicFdg" // Verifica que este sea exacto
        ));
    }
}