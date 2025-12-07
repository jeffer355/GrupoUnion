package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.core.Persona;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    // Método útil para buscar por DNI (evitar duplicados al crear)
    Optional<Persona> findByNroDocumento(String nroDocumento);

    // Método útil para buscar por Email
    Optional<Persona> findByEmail(String email);

}