package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método personalizado necesario para el Login (UserDetailsServiceImpl)
    Optional<Usuario> findByUsername(String username);

    // Los métodos crud (save, delete, findAll) ya vienen heredados
}