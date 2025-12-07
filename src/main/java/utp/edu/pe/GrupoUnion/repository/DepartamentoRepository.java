package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
    // JpaRepository ya incluye findAll(), findById(), save(), deleteById()
    // No hace falta agregarlos manualmente.
}