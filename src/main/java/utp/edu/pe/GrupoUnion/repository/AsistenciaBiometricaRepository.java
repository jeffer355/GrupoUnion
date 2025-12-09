package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.biometrics.AsistenciaBiometrica;

import java.util.List;

@Repository
public interface AsistenciaBiometricaRepository extends JpaRepository<AsistenciaBiometrica, Integer> {

    // Útil para los reportes: obtener todo el historial de intentos biométricos de un empleado
    List<AsistenciaBiometrica> findByEmpleadoIdEmpleado(Integer idEmpleado);
}