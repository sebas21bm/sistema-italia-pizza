package mx.uv.sistemapizzeria.modelo.dto;

public class DetalleReporteDTO {

    private int idInventario;
    private String codigo;
    private String descripcionProductoInventario;
    private Double existencias;
    private Double conteoFisico;
    private Double diferencia;
    private String justificacion;

    public DetalleReporteDTO() {
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcionProductoInventario() {
        return descripcionProductoInventario;
    }

    public void setDescripcionProductoInventario(String descripcionProductoInventario) {
        this.descripcionProductoInventario = descripcionProductoInventario;
    }

    public Double getConteoFisico() {
        return conteoFisico;
    }

    public void setConteoFisico(Double conteoFisico) {
        this.conteoFisico = conteoFisico;
    }
    
    public Double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(Double diferencia) {
        this.diferencia = diferencia;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public Double getExistencias() {
        return existencias;
    }

    public void setExistencias(Double existencias) {
        this.existencias = existencias;
    }
    
    public boolean hayDiferencia() {
        return diferencia != 0;
    }

    @Override
    public String toString() {
        return codigo + " | diff: " + diferencia;
    }
}
