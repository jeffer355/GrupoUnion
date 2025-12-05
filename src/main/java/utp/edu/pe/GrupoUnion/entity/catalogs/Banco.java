package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "banco")
public class Banco implements Serializable{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id_banco")
    private Integer idBanco;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    public Banco(){

    }

    public Banco(Integer idBanco, String nombre){
        this.idBanco = idBanco;
        this.nombre = nombre;
    }

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
