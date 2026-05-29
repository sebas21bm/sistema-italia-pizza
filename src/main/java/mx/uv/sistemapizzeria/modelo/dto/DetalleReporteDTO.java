package mx.uv.sistemapizzeria.modelo.dto;

public class DetalleReporteDTO {

    private int idInventario;
    private String codigoInsumo;
    private double diferencia;
    private String justificacion;

    private ProductoInventarioDTO insumo;

    public DetalleReporteDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public void setCodigoInsumo(String codigoInsumo) {
        this.codigoInsumo = codigoInsumo;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public ProductoInventarioDTO getInsumo() {
        return insumo;
    }

    public void setInsumo(ProductoInventarioDTO insumo) {
        this.insumo = insumo;
        if (insumo != null) {
            this.codigoInsumo = insumo.getCodigo();
        }
    }

    public boolean hayDiferencia() {
        return diferencia != 0;
    }

    @Override
    public String toString() {
        return codigoInsumo + " | diff: " + diferencia;
    }
}
