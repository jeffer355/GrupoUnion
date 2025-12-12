package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import utp.edu.pe.GrupoUnion.payload.EmpleadoResumenDTO; // 👈 Importa el nuevo DTO
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    Optional<Empleado> findByPersona(Persona persona);

    long countByDepartamentoIdDepartamento(Integer idDepartamento);

    List<Empleado> findByDepartamentoIdDepartamento(Integer idDepartamento);

    @Query("SELECT e FROM Empleado e WHERE MONTH(e.persona.fechaNac) = :mes AND e.estado = 'ACTIVO' ORDER BY DAY(e.persona.fechaNac) ASC")
    List<Empleado> findByBirthdayMonth(Integer mes);

    // ✅ MÉTODO OPTIMIZADO PARA CARGA RÁPIDA DE TABLA
    // Usa JOINs para obtener todos los datos de entidades relacionadas en una sola consulta
    @Query("SELECT new utp.edu.pe.GrupoUnion.payload.EmpleadoResumenDTO(" +
            "e.idEmpleado, p.nombres, p.nroDocumento, p.email, p.fotoUrl, d.nombre, c.nombre, e.estado, e.fechaIngreso) " +
            "FROM Empleado e " +
            "JOIN e.persona p " +
            "JOIN e.departamento d " +
            "JOIN e.cargo c")
    List<EmpleadoResumenDTO> findAllResumen();
}