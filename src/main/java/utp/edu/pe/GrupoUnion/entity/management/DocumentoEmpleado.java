package utp.edu.pe.GrupoUnion.entity.management;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_empleado")
public class DocumentoEmpleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDocumento;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    private String nombre;
    private String tipo;
    private String urlArchivo;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @Column(name = "subido_por")
    private String subidoPor;

    // --- NUEVOS CAMPOS PARA APROBACIÓN ---
    @Column(name = "estado", length = 50)
    private String estado = "PENDIENTE"; // Valor por defecto

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }

    // Getters y Setters
    public Integer getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Integer idDocumento) { this.idDocumento = idDocumento; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrlArchivo() { return urlArchivo; }
    public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }
    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public String getSubidoPor() { return subidoPor; }
    public void setSubidoPor(String subidoPor) { this.subidoPor = subidoPor; }

    // Nuevos Getters/Setters
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}