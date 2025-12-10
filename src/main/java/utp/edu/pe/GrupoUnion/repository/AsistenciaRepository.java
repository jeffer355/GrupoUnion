package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.Asistencia;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    Optional<Asistencia> findByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);

    // NUEVO: Contar las faltas (INASISTENCIA o FALTA en el estado)
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.empleado.idEmpleado = :idEmpleado AND MONTH(a.fecha) = :mes AND YEAR(a.fecha) = :anio AND (a.estado LIKE '%FALTA%' OR a.estado LIKE '%INASISTENCIA%')")
    long contarFaltasPorMes(Integer idEmpleado, Integer mes, Integer anio);
}