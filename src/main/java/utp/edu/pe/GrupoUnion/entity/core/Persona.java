package utp.edu.pe.GrupoUnion.entity.core;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoDocumento;

import java.time.LocalDate;
import java.io.Serializable;

@Entity
@Table(name = "persona")
public class Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Integer idPersona;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @ManyToOne
    @JoinColumn(name = "id_tipo_doc", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nro_documento", nullable = false, length = 20, unique = true)
    private String nroDocumento;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "direccion")
    private String direccion;

    public Persona() {}

    public Persona(Integer idPersona, String nombres, TipoDocumento tipoDocumento, String nroDocumento, LocalDate fechaNac, String telefono, String email, String direccion) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.fechaNac = fechaNac;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public Integer getIdPersona() { return idPersona; }
    public void setIdPersona(Integer idPersona) { this.idPersona = idPersona; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }

    public LocalDate getFechaNac() { return fechaNac; }
    public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
