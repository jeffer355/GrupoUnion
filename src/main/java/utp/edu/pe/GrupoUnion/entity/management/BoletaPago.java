package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import java.time.LocalDateTime;
import java.math.BigDecimal; // Importación necesaria

@Entity
@Table(name = "boleta_pago")
public class BoletaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBoleta;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    private Integer mes;
    private Integer anio;

    @Column(name = "url_archivo")
    private String urlArchivo;

    private String estado;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @Column(name = "subido_por")
    private String subidoPor;

    // --- CAMPOS DE CÁLCULO DE NÓMINA (NUEVOS) ---
    @Column(name = "sueldo_basico_calculado", precision = 10, scale = 2)
    private BigDecimal sueldoBasicoCalculado;

    @Column(name = "descuento_faltas", precision = 10, scale = 2)
    private BigDecimal descuentoFaltas;

    @Column(name = "descuento_pension", precision = 10, scale = 2)
    private BigDecimal descuentoPension; // AFP o ONP

    @Column(name = "aporte_essalud", precision = 10, scale = 2)
    private BigDecimal aporteEsSalud; // 9% Empleador

    @Column(name = "neto_pagar", precision = 10, scale = 2)
    private BigDecimal netoPagar;

    @Column(name = "dias_faltados")
    private Integer diasFaltados;
    // ----------------------------------------------

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }

    public BoletaPago() {}

    // Getters y Setters Existentes
    public Integer getIdBoleta() { return idBoleta; }
    public void setIdBoleta(Integer idBoleta) { this.idBoleta = idBoleta; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public String getUrlArchivo() { return urlArchivo; }
    public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public String getSubidoPor() { return subidoPor; }
    public void setSubidoPor(String subidoPor) { this.subidoPor = subidoPor; }

    // Getters y Setters NUEVOS
    public BigDecimal getSueldoBasicoCalculado() { return sueldoBasicoCalculado; }
    public void setSueldoBasicoCalculado(BigDecimal sueldoBasicoCalculado) { this.sueldoBasicoCalculado = sueldoBasicoCalculado; }
    public BigDecimal getDescuentoFaltas() { return descuentoFaltas; }
    public void setDescuentoFaltas(BigDecimal descuentoFaltas) { this.descuentoFaltas = descuentoFaltas; }
    public BigDecimal getDescuentoPension() { return descuentoPension; }
    public void setDescuentoPension(BigDecimal descuentoPension) { this.descuentoPension = descuentoPension; }
    public BigDecimal getAporteEsSalud() { return aporteEsSalud; }
    public void setAporteEsSalud(BigDecimal aporteEsSalud) { this.aporteEsSalud = aporteEsSalud; }
    public BigDecimal getNetoPagar() { return netoPagar; }
    public void setNetoPagar(BigDecimal netoPagar) { this.netoPagar = netoPagar; }
    public Integer getDiasFaltados() { return diasFaltados; }
    public void setDiasFaltados(Integer diasFaltados) { this.diasFaltados = diasFaltados; }
}