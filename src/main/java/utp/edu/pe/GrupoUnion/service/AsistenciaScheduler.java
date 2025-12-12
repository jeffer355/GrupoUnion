package utp.edu.pe.GrupoUnion.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.Asistencia;
import utp.edu.pe.GrupoUnion.repository.AsistenciaRepository;
import utp.edu.pe.GrupoUnion.repository.EmpleadoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaScheduler {

    private final EmpleadoRepository empleadoRepository;
    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaScheduler(EmpleadoRepository empleadoRepository, AsistenciaRepository asistenciaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    // Se ejecuta de Lunes a Viernes a las 23:55 PM (Hora del servidor)
    // cron: segundos minutos horas dia mes dia_semana
    @Scheduled(cron = "0 55 23 * * MON-FRI")
    @Transactional
    public void registrarInasistenciasAutomaticas() {
        System.out.println(">>> ⏰ EJECUTANDO REVISIÓN NOCTURNA DE ASISTENCIA...");

        LocalDate hoy = LocalDate.now();
        // Obtenemos solo empleados activos para no llenar la BD de ex-empleados
        List<Empleado> empleados = empleadoRepository.findAll().stream()
                .filter(e -> "ACTIVO".equals(e.getEstado()))
                .toList();

        int procesados = 0;

        for (Empleado emp : empleados) {
            // Buscamos si tiene registro hoy
            Optional<Asistencia> asistenciaOpt = asistenciaRepository.findByEmpleadoAndFecha(emp, hoy);

            if (asistenciaOpt.isEmpty()) {
                // REGLA: Si no marca en todo el día -> FALTA
                Asistencia falta = new Asistencia();
                falta.setEmpleado(emp);
                falta.setFecha(hoy);
                falta.setEstado("FALTA");
                falta.setObservacion("Inasistencia injustificada (Automático)");

                asistenciaRepository.save(falta);
                procesados++;
            } else {
                // REGLA OPCIONAL: Si marcó entrada pero se fue sin marcar salida
                Asistencia a = asistenciaOpt.get();
                if (a.getHoraEntrada() != null && a.getHoraSalida() == null) {
                    // Mantenemos el estado de entrada (ej: TARDANZA) pero agregamos nota
                    String obs = (a.getObservacion() != null ? a.getObservacion() + " | " : "") + "No marcó salida";
                    a.setObservacion(obs);
                    // Si prefieres que esto también sea FALTA, descomenta la siguiente línea:
                    // a.setEstado("FALTA");
                    asistenciaRepository.save(a);
                }
            }
        }
        System.out.println(">>> ✅ REVISIÓN COMPLETADA. Faltas generadas: " + procesados);
    }
}