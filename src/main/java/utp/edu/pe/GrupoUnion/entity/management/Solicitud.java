package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.EstadoSolicitud;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoSolicitud;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "solicitud")
public class Solicitud implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_solicitud", nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "asunto", nullable = false)
    private String asunto;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "fecha_ini")
    private LocalDate fechaIni;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    public Solicitud() {}

    public Solicitud(Integer idSolicitud, Empleado empleado, TipoSolicitud tipoSolicitud, String asunto, String detalle, LocalDate fechaIni, LocalDate fechaFin, EstadoSolicitud estadoSolicitud) {
        this.idSolicitud = idSolicitud;
        this.empleado = empleado;
        this.tipoSolicitud = tipoSolicitud;
        this.asunto = asunto;
        this.detalle = detalle;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.estadoSolicitud = estadoSolicitud;
    }

    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDate getFechaIni() { return fechaIni; }
    public void setFechaIni(LocalDate fechaIni) { this.fechaIni = fechaIni; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public EstadoSolicitud getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }
}
