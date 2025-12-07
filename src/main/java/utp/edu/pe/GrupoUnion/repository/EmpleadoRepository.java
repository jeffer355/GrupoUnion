package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    // Método útil para buscar empleado por su DNI/Datos personales
    Optional<Empleado> findByPersona(Persona persona);

    // Los métodos crud (save, delete, findAll) ya vienen heredados
}