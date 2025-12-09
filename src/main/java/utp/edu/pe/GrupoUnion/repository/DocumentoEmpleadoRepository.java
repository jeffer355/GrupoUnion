package utp.edu.pe.GrupoUnion.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import utp.edu.pe.GrupoUnion.entity.management.DocumentoEmpleado;
import java.util.List;

public interface DocumentoEmpleadoRepository extends JpaRepository<DocumentoEmpleado, Integer> {
    // Para el empleado
    List<DocumentoEmpleado> findByEmpleadoIdEmpleado(Integer idEmpleado);

    // NUEVO: Para el admin
    List<DocumentoEmpleado> findAllByOrderByFechaSubidaDesc();
}