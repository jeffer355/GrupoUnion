package utp.edu.pe.GrupoUnion.entity.core;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.Banco;

import java.io.Serializable;

@Entity
@Table(name = "cuenta_bancaria")
public class CuentaBancaria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuenta;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_banco", nullable = false)
    private Banco banco;

    @Column(name = "nro_cuenta", nullable = false, length = 100)
    private String nroCuenta;

    @Column(name = "CCI", length = 100)
    private String cci;

    @Column(name = "activo")
    private Boolean activo;

    public CuentaBancaria() {}

    public CuentaBancaria(Integer idCuenta, Empleado empleado, Banco banco, String nroCuenta, String cci, Boolean activo) {
        this.idCuenta = idCuenta;
        this.empleado = empleado;
        this.banco = banco;
        this.nroCuenta = nroCuenta;
        this.cci = cci;
        this.activo = activo;
    }

    public Integer getIdCuenta() { return idCuenta; }
    public void setIdCuenta(Integer idCuenta) { this.idCuenta = idCuenta; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Banco getBanco() { return banco; }
    public void setBanco(Banco banco) { this.banco = banco; }

    public String getNroCuenta() { return nroCuenta; }
    public void setNroCuenta(String nroCuenta) { this.nroCuenta = nroCuenta; }

    public String getCci() { return cci; }
    public void setCci(String cci) { this.cci = cci; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}