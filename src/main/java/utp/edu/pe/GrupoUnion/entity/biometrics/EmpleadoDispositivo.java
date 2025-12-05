package utp.edu.pe.GrupoUnion.entity.biometrics;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "empleado_dispositivo")
public class EmpleadoDispositivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo")
    private Integer idDispositivo;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "ip_permitida", length = 50)
    private String ipPermitida;

    @Column(name = "hostname_permitido", length = 100)
    private String hostnamePermitido;

    @Column(name = "registrado_en", insertable = false, updatable = false)
    private LocalDateTime registradoEn;

    @Column(name = "activo")
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        registradoEn = LocalDateTime.now();
    }

    public EmpleadoDispositivo() {}

    public EmpleadoDispositivo(Integer idDispositivo, Empleado empleado, String ipPermitida, String hostnamePermitido, Boolean activo) {
        this.idDispositivo = idDispositivo;
        this.empleado = empleado;
        this.ipPermitida = ipPermitida;
        this.hostnamePermitido = hostnamePermitido;
        this.activo = activo;
    }

    public Integer getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(Integer idDispositivo) { this.idDispositivo = idDispositivo; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public String getIpPermitida() { return ipPermitida; }
    public void setIpPermitida(String ipPermitida) { this.ipPermitida = ipPermitida; }

    public String getHostnamePermitido() { return hostnamePermitido; }
    public void setHostnamePermitido(String hostnamePermitido) { this.hostnamePermitido = hostnamePermitido; }

    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(LocalDateTime registradoEn) { this.registradoEn = registradoEn; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
