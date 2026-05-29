package mx.uv.sistemapizzeria.modelo.dto;

/**
 * DTO que mapea la tabla: producto_compuesto_por
 * Campos BD: codigo (FK → producto_insumo), codigo_menu (FK → producto_venta), cantidad
 *
 * Representa la RECETA: qué insumos y en qué cantidad componen un producto del menú.
 * Se incluyen los objetos anidados para acceder directamente a los datos
 * del insumo y del producto sin consultas extra.
 */
public class ProductoCompuestoPorDTO {

    private String codigoInsumo;      // FK → producto_insumo.codigo
    private String codigoMenu;        // FK → producto_venta.codigo_menu
    private double cantidad;

    // Objetos anidados (consejo de Sebas)
    private ProductoInventarioDTO insumo;
    private ProductoVentaDTO productoVenta;

    public ProductoCompuestoPorDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

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
