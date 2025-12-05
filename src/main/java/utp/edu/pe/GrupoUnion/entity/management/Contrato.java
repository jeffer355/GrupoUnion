package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.RegimenPensionario;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoContrato;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

@Entity
@Table(name = "contrato")
public class Contrato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_contrato", nullable = false)
    private TipoContrato tipoContrato;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "sueldo_basico", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBasico;

    @ManyToOne
    @JoinColumn(name = "id_regimen", nullable = false)
    private RegimenPensionario regimenPensionario;

    @Column(name = "activo")
    private Boolean activo;

    public Contrato() {}

    public Contrato(Integer idContrato, Empleado empleado, TipoContrato tipoContrato, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal sueldoBasico, RegimenPensionario regimenPensionario, Boolean activo) {
        this.idContrato = idContrato;
        this.empleado = empleado;
        this.tipoContrato = tipoContrato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.sueldoBasico = sueldoBasico;
        this.regimenPensionario = regimenPensionario;
        this.activo = activo;
    }

    public Integer getIdContrato() { return idContrato; }
    public void setIdContrato(Integer idContrato) { this.idContrato = idContrato; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public TipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(TipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getSueldoBasico() { return sueldoBasico; }
    public void setSueldoBasico(BigDecimal sueldoBasico) { this.sueldoBasico = sueldoBasico; }

    public RegimenPensionario getRegimenPensionario() { return regimenPensionario; }
    public void setRegimenPensionario(RegimenPensionario regimenPensionario) { this.regimenPensionario = regimenPensionario; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
