package utp.edu.pe.GrupoUnion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class BiometriaUtils {

    // Calcula la "Distancia Euclidiana" entre dos vectores faciales
    public static double calcularDistancia(String embeddingRegistradoJson, List<Float> embeddingNuevo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Double> vectorBase = mapper.readValue(embeddingRegistradoJson, List.class);

            if (vectorBase.size() != embeddingNuevo.size()) return 10.0; // Error dimensional

            double sum = 0.0;
            for (int i = 0; i < vectorBase.size(); i++) {
                double diff = vectorBase.get(i).floatValue() - embeddingNuevo.get(i);
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        } catch (Exception e) {
            e.printStackTrace();
            return 10.0; // Retorna distancia alta (no match) si falla
        }
    }
}