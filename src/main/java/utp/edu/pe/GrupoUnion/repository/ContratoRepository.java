package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.management.Contrato;
import java.util.Optional;
import java.util.List;
import jakarta.transaction.Transactional; // Importante para Modifying

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    // Obtener el contrato vigente de un empleado
    Optional<Contrato> findByEmpleadoIdEmpleadoAndVigenteTrue(Integer idEmpleado);

    // Obtener todo el historial para el admin
    List<Contrato> findAllByOrderByFechaGeneracionDesc();

    // VITAL: Se añade @Transactional aquí y @Modifying
    @Modifying
    @Transactional
    @Query("UPDATE Contrato c SET c.vigente = false WHERE c.empleado.idEmpleado = :idEmpleado AND c.vigente = true")
    void desactivarContratosPrevios(Integer idEmpleado);
}