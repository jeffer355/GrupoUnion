package utp.edu.pe.GrupoUnion.entity.auth;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "hash_pass", nullable = false)
    private String hashPass;

    @Column(name = "activo")
    private Boolean activo;

    // --- NUEVOS CAMPOS PARA SEGURIDAD ---
    @Column(name = "requiere_cambio_pass")
    private Boolean requiereCambioPass = true; // True por defecto

    @Column(name = "token_2fa")
    private String token2fa;
    // ------------------------------------

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() { creadoEn = LocalDateTime.now(); }

    public Usuario() {}

    // Getters y Setters (Asegúrate de tener los de los nuevos campos)
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHashPass() { return hashPass; }
    public void setHashPass(String hashPass) { this.hashPass = hashPass; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

    // Getters/Setters Nuevos
    public Boolean getRequiereCambioPass() { return requiereCambioPass; }
    public void setRequiereCambioPass(Boolean requiereCambioPass) { this.requiereCambioPass = requiereCambioPass; }
    public String getToken2fa() { return token2fa; }
    public void setToken2fa(String token2fa) { this.token2fa = token2fa; }
}