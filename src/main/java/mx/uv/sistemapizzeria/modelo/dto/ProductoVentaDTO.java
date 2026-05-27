package mx.uv.sistemapizzeria.modelo.dto;

/**
 * DTO que mapea la tabla: producto_venta
 * Campos BD: codigo_menu, nombre, estatus, precio, limite, descripcion, foto
 *
 * Representa los productos del menú (pizzas, bebidas, etc.) que se venden
 * en pedidos. Si requiere receta, estará compuesto por ProductoInsumoDTO.
 */
public class ProductoVentaDTO {

    private String codigoMenu;
    private String nombre;
    private int estatus;        // 1 = disponible, 0 = eliminado lógicamente
    private double precio;
    private int limite;         // cantidad máxima permitida (0 = sin límite)
    private String descripcion;
    private String foto;        // ruta/path de la imagen VARCHAR(255)

    public ProductoVentaDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public String getCodigoMenu() {
        return codigoMenu;
    }

    public void setCodigoMenu(String codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "[" + codigoMenu + "] " + nombre + " - $" + precio;
    }
}
