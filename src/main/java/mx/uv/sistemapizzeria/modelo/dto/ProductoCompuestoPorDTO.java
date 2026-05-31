package mx.uv.sistemapizzeria.modelo.dto;

public class ProductoCompuestoPorDTO {

    private String codigoInsumo;
    private String codigoMenu;
    private String nombreProductoInventario;
    private double cantidad;

    private ProductoInventarioDTO insumo;
    private ProductoVentaDTO productoVenta;

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

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombreProductoInventario() {
        return nombreProductoInventario;
    }

    public void setNombreProductoInventario(String nombreProductoInventario) {
        this.nombreProductoInventario = nombreProductoInventario;
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

    public ProductoVentaDTO getProductoVenta() {
        return productoVenta;
    }

    public void setProductoVenta(ProductoVentaDTO productoVenta) {
        this.productoVenta = productoVenta;
        if (productoVenta != null) {
            this.codigoMenu = productoVenta.getCodigoMenu();
        }
    }

    @Override
    public String toString() {
        return codigoInsumo + " x" + cantidad + " → " + codigoMenu;
    }
}
