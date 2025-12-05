package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.EstadoSolicitud;

import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "solicitud_historial")
public class SolicitudHistorial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "creado", insertable = false, updatable = false)
    private LocalDateTime creado;

    @PrePersist
    protected void onCreate() {
        creado = LocalDateTime.now();
    }

    public SolicitudHistorial() {}

    public SolicitudHistorial(Integer idHistorial, Solicitud solicitud, Usuario usuario, EstadoSolicitud estadoSolicitud, String comentario) {
        this.idHistorial = idHistorial;
        this.solicitud = solicitud;
        this.usuario = usuario;
        this.estadoSolicitud = estadoSolicitud;
        this.comentario = comentario;
    }

    public Integer getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Integer idHistorial) { this.idHistorial = idHistorial; }

    public Solicitud getSolicitud() { return solicitud; }
    public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoSolicitud getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getCreado() { return creado; }
    public void setCreado(LocalDateTime creado) { this.creado = creado; }
}
