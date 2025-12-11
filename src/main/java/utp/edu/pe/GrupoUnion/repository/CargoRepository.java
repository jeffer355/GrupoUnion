package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.catalogs.Cargo;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {
    // JpaRepository ya incluye automáticamente el método findAll() que usamos en el controlador.
}