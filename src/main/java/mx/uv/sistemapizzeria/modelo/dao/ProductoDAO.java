package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements Operaciones<String, ProductoVentaDTO> {

    // ── buscar(identificador: String): Producto ────────────────────────────
    @Override
    public ProductoVentaDTO buscar(String codigoMenu) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo_menu, nombre, estatus, precio, limite, descripcion, foto " +
                         "FROM producto_venta WHERE codigo_menu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    // ── editar(elemento: Producto): boolean ────────────────────────────────
    @Override
    public boolean editar(ProductoVentaDTO producto) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE producto_venta SET nombre=?, precio=?, limite=?, descripcion=?, foto=? " +
                         "WHERE codigo_menu=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, producto.getNombre());
                ps.setDouble(2, producto.getPrecio());
                ps.setInt(3, producto.getLimite());
                ps.setString(4, producto.getDescripcion());
                ps.setString(5, producto.getFoto());
                ps.setString(6, producto.getCodigoMenu());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── eliminar(identificador: String): boolean ───────────────────────────
    @Override
    public boolean eliminar(String codigoMenu) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE producto_venta SET estatus = 0 WHERE codigo_menu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<Producto> ─────────────────────────────────────
    @Override
    public List<ProductoVentaDTO> mostrarTodos() throws Exception {
        List<ProductoVentaDTO> productos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo_menu, nombre, estatus, precio, limite, descripcion, foto " +
                         "FROM producto_venta WHERE estatus = 1 ORDER BY nombre";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }

    // ── registrar(elemento: Producto): boolean ─────────────────────────────
    @Override
    public boolean registrar(ProductoVentaDTO producto) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "INSERT INTO producto_venta (codigo_menu, nombre, estatus, precio, limite, descripcion, foto) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setInt(3, producto.getEstatus());
                ps.setDouble(4, producto.getPrecio());
                ps.setInt(5, producto.getLimite());
                ps.setString(6, producto.getDescripcion());
                ps.setString(7, producto.getFoto());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private ProductoVentaDTO mapearProducto(ResultSet rs) throws SQLException {
        ProductoVentaDTO p = new ProductoVentaDTO();
        p.setCodigoMenu(rs.getString("codigo_menu"));
        p.setNombre(rs.getString("nombre"));
        p.setEstatus(rs.getInt("estatus"));
        p.setPrecio(rs.getDouble("precio"));
        p.setLimite(rs.getInt("limite"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setFoto(rs.getString("foto"));
        return p;
    }
}
