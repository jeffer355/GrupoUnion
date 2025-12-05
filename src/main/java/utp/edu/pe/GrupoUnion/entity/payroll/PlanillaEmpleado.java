package utp.edu.pe.GrupoUnion.entity.payroll;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.PeriodoPlanilla;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "planilla_empleado")
public class PlanillaEmpleado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilla_emp")
    private Integer idPlanillaEmp;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoPlanilla periodoPlanilla;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "dias_trabajados")
    private Integer diasTrabajados;

    @Column(name = "horas_extras")
    private Integer horasExtras;

    @Column(name = "neto_pagar", precision = 10, scale = 2)
    private BigDecimal netoPagar;

    @Column(name = "generado_en", insertable = false, updatable = false)
    private LocalDateTime generadoEn;

    @PrePersist
    protected void onCreate() {
        generadoEn = LocalDateTime.now();
    }

    public PlanillaEmpleado() {}

    public PlanillaEmpleado(Integer idPlanillaEmp, PeriodoPlanilla periodoPlanilla, Empleado empleado, Integer diasTrabajados, Integer horasExtras, BigDecimal netoPagar) {
        this.idPlanillaEmp = idPlanillaEmp;
        this.periodoPlanilla = periodoPlanilla;
        this.empleado = empleado;
        this.diasTrabajados = diasTrabajados;
        this.horasExtras = horasExtras;
        this.netoPagar = netoPagar;
    }

    public Integer getIdPlanillaEmp() { return idPlanillaEmp; }
    public void setIdPlanillaEmp(Integer idPlanillaEmp) { this.idPlanillaEmp = idPlanillaEmp; }

    public PeriodoPlanilla getPeriodoPlanilla() { return periodoPlanilla; }
    public void setPeriodoPlanilla(PeriodoPlanilla periodoPlanilla) { this.periodoPlanilla = periodoPlanilla; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Integer getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(Integer diasTrabajados) { this.diasTrabajados = diasTrabajados; }

    public Integer getHorasExtras() { return horasExtras; }
    public void setHorasExtras(Integer horasExtras) { this.horasExtras = horasExtras; }

    public BigDecimal getNetoPagar() { return netoPagar; }
    public void setNetoPagar(BigDecimal netoPagar) { this.netoPagar = netoPagar; }

    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }
}
