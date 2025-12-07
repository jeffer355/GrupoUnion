package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByPersona(Persona persona);

    // Para validar antes de borrar un área
    long countByDepartamentoIdDepartamento(Integer idDepartamento);

    // Para ver detalles del área (listar sus empleados)
    List<Empleado> findByDepartamentoIdDepartamento(Integer idDepartamento);
}