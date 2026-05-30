package mx.uv.sistemapizzeria.modelo.dto;

import java.time.LocalDate;

/**
 * DTO que mapea la tabla: producto_insumo
 * Campos BD: codigo, nombre, estatus, existencias, fecha_caducidad, foto
 *
 * Representa la materia prima / insumos del inventario físico
 * (harina, queso, tomate, refrescos en stock, etc.).
 */
public class ProductoInventarioDTO {

    private String codigo;
    private String nombre;
    private int estatus;            // 1 = activo, 0 = eliminado lógicamente
    private int existencias;
    private LocalDate fechaCaducidad;
    private String foto;            // ruta/path VARCHAR(255)

    public ProductoInventarioDTO() {

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - Existencias: " + existencias;
    }
}
