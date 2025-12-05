package utp.edu.pe.GrupoUnion.entity.biometrics;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "asistencia_biometrica")
public class AsistenciaBiometrica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia_bio")
    private Integer idAsistenciaBio;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalDateTime hora;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "confianza", nullable = false, precision = 5, scale = 4)
    private BigDecimal confianza;

    @Column(name = "distancia_match")
    private Double distanciaMatch;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "hostname", length = 100)
    private String hostname;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Lob
    @Column(name = "foto_capturada", columnDefinition = "LONGBLOB")
    private byte[] fotoCapturada;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    public AsistenciaBiometrica() {}

    public AsistenciaBiometrica(Integer idAsistenciaBio, Empleado empleado, LocalDate fecha, LocalDateTime hora, String tipo, BigDecimal confianza, Double distanciaMatch, String ipAddress, String hostname, String userAgent, byte[] fotoCapturada) {
        this.idAsistenciaBio = idAsistenciaBio;
        this.empleado = empleado;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.confianza = confianza;
        this.distanciaMatch = distanciaMatch;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.userAgent = userAgent;
        this.fotoCapturada = fotoCapturada;
    }

    public Integer getIdAsistenciaBio() { return idAsistenciaBio; }
    public void setIdAsistenciaBio(Integer idAsistenciaBio) { this.idAsistenciaBio = idAsistenciaBio; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getHora() { return hora; }
    public void setHora(LocalDateTime hora) { this.hora = hora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getConfianza() { return confianza; }
    public void setConfianza(BigDecimal confianza) { this.confianza = confianza; }

    public Double getDistanciaMatch() { return distanciaMatch; }
    public void setDistanciaMatch(Double distanciaMatch) { this.distanciaMatch = distanciaMatch; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public byte[] getFotoCapturada() { return fotoCapturada; }
    public void setFotoCapturada(byte[] fotoCapturada) { this.fotoCapturada = fotoCapturada; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
