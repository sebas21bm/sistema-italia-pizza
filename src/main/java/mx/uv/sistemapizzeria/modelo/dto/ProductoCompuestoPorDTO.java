package mx.uv.sistemapizzeria.modelo.dto;

public class ProductoCompuestoPorDTO {

    private String codigoInsumo;
    private String codigoMenu;
    private String nombreProductoInventario;
    private double cantidad;


    public ProductoCompuestoPorDTO() {}

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public void setCodigoInsumo(String codigoInsumo) {
        this.codigoInsumo = codigoInsumo;
    }

    public String getCodigoMenu() {
        return codigoMenu;
    }

    public void setCodigoMenu(String codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public String getNombreProductoInventario() {
        return nombreProductoInventario;
    }

    public void setNombreProductoInventario(String nombreProductoInventario) {
        this.nombreProductoInventario = nombreProductoInventario;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return codigoInsumo + " x" + cantidad + " → " + codigoMenu;
    }
}
