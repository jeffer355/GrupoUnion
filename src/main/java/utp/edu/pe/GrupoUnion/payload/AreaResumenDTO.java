package utp.edu.pe.GrupoUnion.payload;

public class AreaResumenDTO {
    private Integer idDepartamento;
    private String nombre;
    private Long cantidadEmpleados;

    public AreaResumenDTO(Integer idDepartamento, String nombre, Long cantidadEmpleados) {
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
        this.cantidadEmpleados = cantidadEmpleados;
    }

    // Getters y Setters
    public Integer getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(Integer idDepartamento) { this.idDepartamento = idDepartamento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getCantidadEmpleados() { return cantidadEmpleados; }
    public void setCantidadEmpleados(Long cantidadEmpleados) { this.cantidadEmpleados = cantidadEmpleados; }
}