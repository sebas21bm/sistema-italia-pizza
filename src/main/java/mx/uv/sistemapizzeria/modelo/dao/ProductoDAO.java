package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements Operaciones<String, ProductoVentaDTO> {

    // ── buscar(identificador: String): Producto ────────────────────────────
    @Override
    public ProductoVentaDTO buscar(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
    public boolean editar(ProductoVentaDTO producto) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
    public boolean eliminar(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
    public List<ProductoVentaDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
    public boolean registrar(ProductoVentaDTO producto) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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

    // ── Validar si el producto está en un pedido ───────────────────────────
    public boolean tienePedidos(String codigoMenu) throws SQLException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException("Error: Sin conexión a la base de datos.");

            String sql = "SELECT 1 FROM detalles_pedido WHERE codigo_menu = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (java.io.IOException | ClassNotFoundException ex) {
            throw new SQLException("Fallo al cargar la configuración o el driver de BD: " + ex.getMessage(), ex);
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

    // ── Registrar producto con una receta ─────────────────────────────────────────────────────────────
    public void registrarConReceta(ProductoVentaDTO producto, List<ProductoCompuestoPorDTO> receta) throws SQLException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            // Construir la tabla temporal solo para esta conexión y nada más
            String sqlCrearTemp = "CREATE TEMPORARY TABLE IF NOT EXISTS temp_receta(" +
                    "codigo VARCHAR(5) NOT NULL, " +
                    "cantidad DOUBLE NOT NULL)";
            try (PreparedStatement psCrear = conn.prepareStatement(sqlCrearTemp)) {
                psCrear.executeUpdate();
            }

            // Limpiar la tabla temporal
            try (PreparedStatement psLimpiar = conn.prepareStatement("DELETE FROM temp_receta")) {
                psLimpiar.executeUpdate();
            }

            // Llenar la tabla temporal
            String sqlReceta = "{CALL registrar_receta(?, ?)}";
            try (PreparedStatement psReceta = conn.prepareCall(sqlReceta)) {
                for (ProductoCompuestoPorDTO item : receta) {
                    psReceta.setString(1, item.getInsumo().getCodigo());
                    psReceta.setDouble(2, item.getCantidad());
                    psReceta.executeUpdate();
                }
            }

            // Llamar al procedimiento maestro para que absorba los datos temporales
            String sqlPrincipal = "{CALL registrar_producto_con_receta(?, ?, ?, ?, ?, ?)}";
            try (PreparedStatement ps = conn.prepareCall(sqlPrincipal)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setString(3, producto.getDescripcion());
                ps.setString(4, producto.getFoto());
                ps.setDouble(5, producto.getPrecio());
                ps.setInt(6, producto.getLimite());
                ps.executeUpdate();
            }
        } catch (IOException | ClassNotFoundException ex) {
            String msjErrorCargaDatos = Constantes.MSJ_ERROR_CARGA_DATOS;
            System.out.printf(msjErrorCargaDatos);
        }
    }

    // ── Registrar producto sin una receta ─────────────────────────────────────────────────────────────
    public void registrarSinReceta(ProductoVentaDTO producto, int existencias, LocalDate fechaCaducidad) throws SQLException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "{CALL registrar_producto_sin_receta(?, ?, ?, ?, ?, ?, ?, ?)}";
            try (PreparedStatement ps = conn.prepareCall(sql)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setString(3, producto.getDescripcion());
                ps.setString(4, producto.getFoto());
                ps.setDouble(5, producto.getPrecio());
                ps.setInt(6, producto.getLimite());
                ps.setInt(7, existencias);
                ps.setDate(8, java.sql.Date.valueOf(fechaCaducidad));
                ps.executeUpdate();
            }
        } catch (IOException | ClassNotFoundException ex) {
            String msjErrorCargaDatos = Constantes.MSJ_ERROR_CARGA_DATOS;
            System.out.printf(msjErrorCargaDatos);
        }
    }

    // ── Eliminar un producto de venta ─────────────────────────────────────────────────────────────
    public void eliminarProductoVenta(String codigoMenu) throws SQLException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "{CALL eliminar_producto_venta(?)}";
            try (PreparedStatement ps = conn.prepareCall(sql)) {
                ps.setString(1, codigoMenu);
                ps.executeUpdate();
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new SQLException(Constantes.MSJ_SIN_CONEXION);
        }
    }

    // ── Editar un producto de venta ───────────────────────────
    public void editarProductoCompleto(ProductoVentaDTO producto, List<ProductoCompuestoPorDTO> receta) throws SQLException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            // Construir la tabla temporal solo para esta conexión y nada más
            String sqlCrearTemp = "CREATE TEMPORARY TABLE IF NOT EXISTS temp_receta(" +
                    "codigo VARCHAR(5) NOT NULL, " +
                    "cantidad DOUBLE NOT NULL)";
            try (PreparedStatement psCrear = conn.prepareStatement(sqlCrearTemp)) {
                psCrear.executeUpdate();
            }

            try (PreparedStatement psLimpiar = conn.prepareStatement("DELETE FROM temp_receta")) {
                psLimpiar.executeUpdate();
            }

            // Llenar la tabla temporal
            String sqlReceta = "{CALL registrar_receta(?, ?)}";
            try (PreparedStatement psReceta = conn.prepareCall(sqlReceta)) {
                for (ProductoCompuestoPorDTO item : receta) {
                    psReceta.setString(1, item.getCodigoInsumo());
                    psReceta.setDouble(2, item.getCantidad());
                    psReceta.executeUpdate();
                }
            }

            // Llamar al procedimiento maestro para que absorba los datos temporales
            String sqlPrincipal = "{CALL editar_producto_venta(?, ?, ?, ?, ?, ?)}";
            try (PreparedStatement ps = conn.prepareCall(sqlPrincipal)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setDouble(3, producto.getPrecio());
                ps.setInt(4, producto.getLimite());
                ps.setString(5, producto.getDescripcion());
                ps.setString(6, producto.getFoto());
                ps.executeUpdate();
            }
        } catch (java.io.IOException | ClassNotFoundException ex) {
            throw new SQLException(Constantes.MSJ_SIN_CONEXION);
        }
    }
}
