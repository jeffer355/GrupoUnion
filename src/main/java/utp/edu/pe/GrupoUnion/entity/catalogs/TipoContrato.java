package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipo_contrato")
public class TipoContrato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_contrato")
    private Integer idTipoContrato;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    public TipoContrato() {}

    public TipoContrato(Integer idTipoContrato, String nombre) {
        this.idTipoContrato = idTipoContrato;
        this.nombre = nombre;
    }

    public Integer getIdTipoContrato() { return idTipoContrato; }
    public void setIdTipoContrato(Integer idTipoContrato) { this.idTipoContrato = idTipoContrato; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}