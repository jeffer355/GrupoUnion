package utp.edu.pe.GrupoUnion.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // Importación necesaria

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoResumenDTO {

    // Campos del Empleado
    private Integer idEmpleado;
    private String estado;
    private LocalDate fechaIngreso;

    // Campos de Persona
    private String nombres;
    private String nroDocumento;
    private String email;
    private String fotoUrl; // Incluimos la foto por si se usa en la tabla/avatar

    // Campos de Entidades Relacionadas (Solo el nombre)
    private String departamentoNombre;
    private String cargoNombre;

    // Constructor que coincide exactamente con el SELECT de la consulta JPQL
    public EmpleadoResumenDTO(Integer idEmpleado, String nombres, String nroDocumento, String email, String fotoUrl, String departamentoNombre, String cargoNombre, String estado, LocalDate fechaIngreso) {
        this.idEmpleado = idEmpleado;
        this.nombres = nombres;
        this.nroDocumento = nroDocumento;
        this.email = email;
        this.fotoUrl = fotoUrl;
        this.departamentoNombre = departamentoNombre;
        this.cargoNombre = cargoNombre;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }
}