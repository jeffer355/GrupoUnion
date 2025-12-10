package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método para login
    Optional<Usuario> findByUsername(String username);

    // Spring Boot implementará automáticamente la consulta "SELECT COUNT(*) > 0 ..."
    boolean existsByUsername(String username);
}