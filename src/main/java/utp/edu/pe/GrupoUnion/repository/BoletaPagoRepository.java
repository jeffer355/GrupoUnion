package utp.edu.pe.GrupoUnion.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import utp.edu.pe.GrupoUnion.entity.management.BoletaPago;
import java.util.List;

public interface BoletaPagoRepository extends JpaRepository<BoletaPago, Integer> {
    List<BoletaPago> findByEmpleadoIdEmpleadoOrderByAnioDescMesDesc(Integer idEmpleado);
}