package utp.edu.pe.GrupoUnion.entity.biometrics;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "empleado_biometria")
public class EmpleadoBiometria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_biometria")
    private Integer idBiometria;

    @OneToOne
    @JoinColumn(name = "id_empleado", nullable = false, unique = true)
    private Empleado empleado;

    @Lob
    @Column(name = "foto_base", columnDefinition = "LONGBLOB")
    private byte[] fotoBase;

    @Column(name = "embedding", nullable = false, columnDefinition = "json")
    private String embedding;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "activo")
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    public EmpleadoBiometria() {}

    public EmpleadoBiometria(Integer idBiometria, Empleado empleado, byte[] fotoBase, String embedding, LocalDateTime fechaActualizacion, Boolean activo) {
        this.idBiometria = idBiometria;
        this.empleado = empleado;
        this.fotoBase = fotoBase;
        this.embedding = embedding;
        this.fechaActualizacion = fechaActualizacion;
        this.activo = activo;
    }

    public Integer getIdBiometria() { return idBiometria; }
    public void setIdBiometria(Integer idBiometria) { this.idBiometria = idBiometria; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public byte[] getFotoBase() { return fotoBase; }
    public void setFotoBase(byte[] fotoBase) { this.fotoBase = fotoBase; }

    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
