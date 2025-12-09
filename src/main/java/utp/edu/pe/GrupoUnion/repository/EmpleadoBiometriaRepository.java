package utp.edu.pe.GrupoUnion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.edu.pe.GrupoUnion.entity.biometrics.EmpleadoBiometria;

import java.util.Optional;

@Repository
public interface EmpleadoBiometriaRepository extends JpaRepository<EmpleadoBiometria, Integer> {

    // Método CRÍTICO usado por el BiometriaService para encontrar la cara registrada
    Optional<EmpleadoBiometria> findByEmpleadoIdEmpleado(Integer idEmpleado);
}