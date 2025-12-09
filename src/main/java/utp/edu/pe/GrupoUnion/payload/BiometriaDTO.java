package utp.edu.pe.GrupoUnion.payload;

import java.util.List;
import lombok.Data;

@Data
public class BiometriaDTO {
    private List<Float> embedding; // El vector numérico de la cara
    private String fotoBase64;     // La foto para evidencia (opcional)
    private String tipo;           // "ENTRADA" o "SALIDA"
}