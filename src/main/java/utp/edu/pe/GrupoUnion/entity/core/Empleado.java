package utp.edu.pe.GrupoUnion.entity.core;

import jakarta.persistence.*;
import utp.edu.pe.GrupoUnion.entity.catalogs.Cargo;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;

import java.time.LocalDate;
import java.io.Serializable;

@Entity
@Table(name = "empleado")
public class Empleado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @OneToOne
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "id_cargo", nullable = false)
    private Cargo cargo;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @Column(name = "estado", length = 50)
    private String estado;

    public Empleado() {}

    public Empleado(Integer idEmpleado, Persona persona, Departamento departamento, Cargo cargo, LocalDate fechaIngreso, LocalDate fechaCese, String estado) {
        this.idEmpleado = idEmpleado;
        this.persona = persona;
        this.departamento = departamento;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.fechaCese = fechaCese;
        this.estado = estado;
    }

    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaCese() { return fechaCese; }
    public void setFechaCese(LocalDate fechaCese) { this.fechaCese = fechaCese; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
