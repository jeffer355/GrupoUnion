package utp.edu.pe.GrupoUnion.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import utp.edu.pe.GrupoUnion.entity.management.Solicitud;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByEmpleadoIdEmpleadoOrderByCreadoEnDesc(Integer idEmpleado);
    // Para admin: ver todas ordenadas
    List<Solicitud> findAllByOrderByCreadoEnDesc();
}