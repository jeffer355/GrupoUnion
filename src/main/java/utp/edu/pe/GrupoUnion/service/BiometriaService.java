package utp.edu.pe.GrupoUnion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import utp.edu.pe.GrupoUnion.entity.biometrics.AsistenciaBiometrica;
import utp.edu.pe.GrupoUnion.entity.biometrics.EmpleadoBiometria;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.Asistencia;
import utp.edu.pe.GrupoUnion.repository.*;
import utp.edu.pe.GrupoUnion.util.BiometriaUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BiometriaService {

    private final EmpleadoBiometriaRepository biometriaRepo; // Debes crear este repo si no existe
    private final AsistenciaBiometricaRepository asistenciaBioRepo; // Debes crear este repo
    private final AsistenciaRepository asistenciaRepo;
    private final EmpleadoRepository empleadoRepo;

    public BiometriaService(EmpleadoBiometriaRepository biometriaRepo, AsistenciaBiometricaRepository asistenciaBioRepo, AsistenciaRepository asistenciaRepo, EmpleadoRepository empleadoRepo) {
        this.biometriaRepo = biometriaRepo;
        this.asistenciaBioRepo = asistenciaBioRepo;
        this.asistenciaRepo = asistenciaRepo;
        this.empleadoRepo = empleadoRepo;
    }

    // Verifica si el empleado ya tiene cara registrada
    public boolean estaEnrolado(Integer idEmpleado) {
        return biometriaRepo.findByEmpleadoIdEmpleado(idEmpleado).isPresent();
    }

    // PASO 1: Enrolar (Guardar cara por primera vez)
    public void enrolarEmpleado(Integer idEmpleado, List<Float> embedding) throws Exception {
        Empleado emp = empleadoRepo.findById(idEmpleado).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        EmpleadoBiometria bio = biometriaRepo.findByEmpleadoIdEmpleado(idEmpleado).orElse(new EmpleadoBiometria());
        bio.setEmpleado(emp);
        bio.setEmbedding(new ObjectMapper().writeValueAsString(embedding));
        bio.setActivo(true);
        bio.setFechaActualizacion(LocalDateTime.now());

        biometriaRepo.save(bio);
    }

    // PASO 2: Marcar Asistencia
    public String procesarAsistencia(Integer idEmpleado, List<Float> embeddingNuevo, String tipo) {
        // 1. Obtener la cara base
        EmpleadoBiometria bioBase = biometriaRepo.findByEmpleadoIdEmpleado(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Usuario no enrolado. Debe registrar su rostro primero."));

        // 2. Comparar Matemáticamente
        double distancia = BiometriaUtils.calcularDistancia(bioBase.getEmbedding(), embeddingNuevo);

        // UMBRAL: Menor a 0.45 es la misma persona (ajustable según face-api.js)
        if (distancia < 0.45) {
            registrarAsistenciaEnBD(bioBase.getEmpleado(), tipo, distancia);
            return "MATCH";
        } else {
            return "NO_MATCH"; // No es la misma persona
        }
    }

    private void registrarAsistenciaEnBD(Empleado empleado, String tipo, double distancia) {
        LocalDate hoy = LocalDate.now();

        // Lógica Asistencia (Tabla simple)
        Asistencia asistencia = asistenciaRepo.findByEmpleadoAndFecha(empleado, hoy)
                .orElse(new Asistencia());

        asistencia.setEmpleado(empleado);
        asistencia.setFecha(hoy);

        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            if (asistencia.getHoraEntrada() == null) {
                asistencia.setHoraEntrada(LocalDateTime.now());
                asistencia.setOrigenEntrada("FACIAL");
                asistencia.setEstado("PUNTUAL"); // Logica simple, mejorar con horarios
            }
        } else {
            asistencia.setHoraSalida(LocalDateTime.now());
            asistencia.setOrigenSalida("FACIAL");
        }
        asistenciaRepo.save(asistencia);

        // Lógica Auditoría Biometrica
        AsistenciaBiometrica bioRecord = new AsistenciaBiometrica();
        bioRecord.setEmpleado(empleado);
        bioRecord.setFecha(hoy);
        bioRecord.setHora(LocalDateTime.now());
        bioRecord.setTipo(tipo);
        bioRecord.setConfianza(BigDecimal.valueOf(1.0 - distancia)); // Aprox
        bioRecord.setDistanciaMatch(distancia);
        asistenciaBioRepo.save(bioRecord);
    }
}