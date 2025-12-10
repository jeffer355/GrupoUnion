package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.catalogs.EstadoSolicitud;

@Repository
public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Integer> {
    // No es necesario agregar métodos extra, JpaRepository ya trae findById
}