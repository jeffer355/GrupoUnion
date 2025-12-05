package utp.edu.pe.GrupoUnion.entity.payroll;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.ConceptoPlanilla;

import java.math.BigDecimal;
import java.io.Serializable;

@Entity
@Table(name = "planilla_detalle")
public class PlanillaDetalle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_planilla_emp", nullable = false)
    private PlanillaEmpleado planillaEmpleado;

    @ManyToOne
    @JoinColumn(name = "id_concepto", nullable = false)
    private ConceptoPlanilla conceptoPlanilla;

    @Column(name = "cantidad", precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "monto_unitario", precision = 10, scale = 2)
    private BigDecimal montoUnitario;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    public PlanillaDetalle() {}

    public PlanillaDetalle(Integer idDetalle, PlanillaEmpleado planillaEmpleado, ConceptoPlanilla conceptoPlanilla, BigDecimal cantidad, BigDecimal montoUnitario, BigDecimal montoTotal) {
        this.idDetalle = idDetalle;
        this.planillaEmpleado = planillaEmpleado;
        this.conceptoPlanilla = conceptoPlanilla;
        this.cantidad = cantidad;
        this.montoUnitario = montoUnitario;
        this.montoTotal = montoTotal;
    }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public PlanillaEmpleado getPlanillaEmpleado() { return planillaEmpleado; }
    public void setPlanillaEmpleado(PlanillaEmpleado planillaEmpleado) { this.planillaEmpleado = planillaEmpleado; }

    public ConceptoPlanilla getConceptoPlanilla() { return conceptoPlanilla; }
    public void setConceptoPlanilla(ConceptoPlanilla conceptoPlanilla) { this.conceptoPlanilla = conceptoPlanilla; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getMontoUnitario() { return montoUnitario; }
    public void setMontoUnitario(BigDecimal montoUnitario) { this.montoUnitario = montoUnitario; }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
}
