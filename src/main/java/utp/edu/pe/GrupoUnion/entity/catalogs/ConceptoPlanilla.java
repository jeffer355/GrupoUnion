package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "concepto_planilla")
public class ConceptoPlanilla implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concepto")
    private Integer idConcepto;

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula;

    public ConceptoPlanilla() {}

    public ConceptoPlanilla(Integer idConcepto, String codigo, String descripcion, String tipo, String formula) {
        this.idConcepto = idConcepto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.formula = formula;
    }

    public Integer getIdConcepto() { return idConcepto; }
    public void setIdConcepto(Integer idConcepto) { this.idConcepto = idConcepto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
}
