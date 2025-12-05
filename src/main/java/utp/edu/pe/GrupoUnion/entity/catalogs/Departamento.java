package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "departamento")
public class Departamento implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Integer idDepartamento;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    public Departamento(){
    }

    public Departamento(Integer idDepartamento, String nombre){
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}


