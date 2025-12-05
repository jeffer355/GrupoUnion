package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "regimen_pensionario")
public class RegimenPensionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regimen")
    private Integer idRegimen;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    public RegimenPensionario() {}

    public RegimenPensionario(Integer idRegimen, String nombre) {
        this.idRegimen = idRegimen;
        this.nombre = nombre;
    }

    public Integer getIdRegimen() { return idRegimen; }
    public void setIdRegimen(Integer idRegimen) { this.idRegimen = idRegimen; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
