package utp.edu.pe.GrupoUnion.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import utp.edu.pe.GrupoUnion.entity.management.BoletaPago;
import java.util.List;

public interface BoletaPagoRepository extends JpaRepository<BoletaPago, Integer> {
    // Para el empleado (sus boletas)
    List<BoletaPago> findByEmpleadoIdEmpleadoOrderByAnioDescMesDesc(Integer idEmpleado);

    // NUEVO: Para el admin (historial completo)
    List<BoletaPago> findAllByOrderByFechaSubidaDesc();
}