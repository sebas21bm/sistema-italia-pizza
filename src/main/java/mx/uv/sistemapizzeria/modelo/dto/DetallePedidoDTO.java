package mx.uv.sistemapizzeria.modelo.dto;

public class DetallePedidoDTO {

    private int idPedido;
    private String codigoMenu;
    private int cantidad;
    private double costo;

    private ProductoVentaDTO productoVenta;

    public DetallePedidoDTO() {}

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getCodigoMenu() {
        return codigoMenu;
    }

    public void setCodigoMenu(String codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
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

    public double getSubtotal() {
        return costo * cantidad;
    }

    @Override
    public String toString() {
        return codigoMenu + " x" + cantidad + " = $" + getSubtotal();
    }
}
