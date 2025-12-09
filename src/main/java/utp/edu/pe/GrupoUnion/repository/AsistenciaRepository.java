package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.Asistencia;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    // Método fundamental para el servicio de biometría:
    // Busca si ya existe un registro de asistencia para este empleado en la fecha de hoy.
    Optional<Asistencia> findByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);
}