package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "estado_solicitud")
public class EstadoSolicitud implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    public EstadoSolicitud() {}

    public EstadoSolicitud(Integer idEstado, String nombre) {
        this.idEstado = idEstado;
        this.nombre = nombre;
    }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
