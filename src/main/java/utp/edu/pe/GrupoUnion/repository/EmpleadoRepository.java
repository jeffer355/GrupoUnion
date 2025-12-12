package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByPersona(Persona persona);

    long countByDepartamentoIdDepartamento(Integer idDepartamento);

    List<Empleado> findByDepartamentoIdDepartamento(Integer idDepartamento);

    @Query("SELECT e FROM Empleado e WHERE MONTH(e.persona.fechaNac) = :mes AND e.estado = 'ACTIVO' ORDER BY DAY(e.persona.fechaNac) ASC")
    List<Empleado> findByBirthdayMonth(Integer mes);
}