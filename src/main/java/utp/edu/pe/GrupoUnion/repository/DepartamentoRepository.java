package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;
import utp.edu.pe.GrupoUnion.payload.AreaResumenDTO;
import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {

    // BUENA PRÁCTICA: Usamos JPQL para contar empleados directamente en la BD
    @Query("SELECT new utp.edu.pe.GrupoUnion.payload.AreaResumenDTO(d.idDepartamento, d.nombre, COUNT(e)) " +
            "FROM Departamento d LEFT JOIN Empleado e ON e.departamento.idDepartamento = d.idDepartamento " +
            "GROUP BY d.idDepartamento, d.nombre")
    List<AreaResumenDTO> obtenerAreasConConteo();
}