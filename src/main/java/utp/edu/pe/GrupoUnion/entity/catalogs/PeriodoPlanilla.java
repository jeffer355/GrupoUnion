package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "periodo_planilla")
public class PeriodoPlanilla implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodo")
    private Integer idPeriodo;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "estado", length = 50)
    private String estado;

    public PeriodoPlanilla() {}

    public PeriodoPlanilla(Integer idPeriodo, Integer anio, Integer mes, String estado) {
        this.idPeriodo = idPeriodo;
        this.anio = anio;
        this.mes = mes;
        this.estado = estado;
    }

    public Integer getIdPeriodo() { return idPeriodo; }
    public void setIdPeriodo(Integer idPeriodo) { this.idPeriodo = idPeriodo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
