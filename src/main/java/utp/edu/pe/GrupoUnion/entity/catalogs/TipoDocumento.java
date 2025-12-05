package utp.edu.pe.GrupoUnion.entity.catalogs;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name= "tipo_documento")
public class TipoDocumento implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_doc")
    private Integer idTipoDoc;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    public TipoDocumento(){

    }

    public TipoDocumento(Integer idTipoDoc, String nombre){
      this.idTipoDoc = idTipoDoc;
      this.nombre = nombre;
    }

    public Integer getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(Integer idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
