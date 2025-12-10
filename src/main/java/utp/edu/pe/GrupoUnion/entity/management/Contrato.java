package utp.edu.pe.GrupoUnion.entity.management;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoContrato;
import utp.edu.pe.GrupoUnion.entity.catalogs.RegimenPensionario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_contrato", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TipoContrato tipoContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regimen", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RegimenPensionario regimenPensionario;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "tipo_regimen", nullable = false, length = 20)
    private String tipoRegimen;

    @Column(name = "nombre_afp", length = 50)
    private String nombreAfp;

    @Column(name = "sueldo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Column(name = "vigente", nullable = false)
    private Boolean vigente;

    @Column(name = "generado_por", nullable = false)
    private String generadoPor;

    @Column(name = "fecha_generacion", updatable = false)
    private LocalDateTime fechaGeneracion;

    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
        if (vigente == null) vigente = true;
    }

    public Contrato() {}

    // Getters y Setters
    public Integer getIdContrato() { return idContrato; }
    public void setIdContrato(Integer idContrato) { this.idContrato = idContrato; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public TipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(TipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }
    public RegimenPensionario getRegimenPensionario() { return regimenPensionario; }
    public void setRegimenPensionario(RegimenPensionario regimenPensionario) { this.regimenPensionario = regimenPensionario; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getTipoRegimen() { return tipoRegimen; }
    public void setTipoRegimen(String tipoRegimen) { this.tipoRegimen = tipoRegimen; }
    public String getNombreAfp() { return nombreAfp; }
    public void setNombreAfp(String nombreAfp) { this.nombreAfp = nombreAfp; }
    public BigDecimal getSueldoBase() { return sueldoBase; }
    public void setSueldoBase(BigDecimal sueldoBase) { this.sueldoBase = sueldoBase; }
    public Boolean getVigente() { return vigente; }
    public void setVigente(Boolean vigente) { this.vigente = vigente; }
    public String getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(String generadoPor) { this.generadoPor = generadoPor; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
}