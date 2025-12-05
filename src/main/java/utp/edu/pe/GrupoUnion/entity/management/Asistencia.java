package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "asistencia")
public class Asistencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Integer idAsistencia;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Column(name = "origen_entrada", length = 50)
    private String origenEntrada;

    @Column(name = "origen_salida", length = 50)
    private String origenSalida;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    public Asistencia() {}

    public Asistencia(Integer idAsistencia, Empleado empleado, LocalDate fecha, LocalDateTime horaEntrada, LocalDateTime horaSalida, String origenEntrada, String origenSalida, String estado, String observacion) {
        this.idAsistencia = idAsistencia;
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.origenEntrada = origenEntrada;
        this.origenSalida = origenSalida;
        this.estado = estado;
        this.observacion = observacion;
    }

    public Integer getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Integer idAsistencia) { this.idAsistencia = idAsistencia; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public String getOrigenEntrada() { return origenEntrada; }
    public void setOrigenEntrada(String origenEntrada) { this.origenEntrada = origenEntrada; }

    public String getOrigenSalida() { return origenSalida; }
    public void setOrigenSalida(String origenSalida) { this.origenSalida = origenSalida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
