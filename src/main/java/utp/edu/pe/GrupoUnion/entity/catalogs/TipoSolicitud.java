package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_solicitud")
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_solicitud")
    private Integer idTipoSolicitud;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    public TipoSolicitud() {}

    public TipoSolicitud(Integer idTipoSolicitud, String nombre) {
        this.idTipoSolicitud = idTipoSolicitud;
        this.nombre = nombre;
    }

    public Integer getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    public void setIdTipoSolicitud(Integer idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
