package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInsumoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla producto_compuesto_por (recetas).
 * El identificador compuesto es codigo_menu + codigo_insumo,
 * pero por la interfaz usamos codigo_menu como identificador principal
 * para obtener toda la receta de un producto.
 */
public class ProductoCompuestoPorDAO implements Operaciones<String, ProductoCompuestoPorDTO> {

    // ── buscar(codigoMenu): receta completa del producto ───────────────────
    // Devuelve el primer insumo que coincida; para la receta completa usa obtenerReceta()
    @Override
    public ProductoCompuestoPorDTO buscar(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoCompuestoPorDTO> receta = obtenerReceta(codigoMenu);
        return receta.isEmpty() ? null : receta.get(0);
    }

    /** Obtiene todos los insumos que componen un producto del menú. */
    public List<ProductoCompuestoPorDTO> obtenerReceta(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT pc.codigo, pc.codigo_menu, pc.cantidad, " +
                         "pi.nombre AS nombre_insumo, pi.existencias, pi.estatus AS estatus_insumo " +
                         "FROM producto_compuesto_por pc " +
                         "JOIN producto_inventario pi ON pc.codigo = pi.codigo " +
                         "WHERE pc.codigo_menu = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) receta.add(mapearCompuesto(rs));
                }
            }
        }
        return receta;
    }

    // ── editar(item): boolean ──────────────────────────────────────────────
    @Override
    public boolean editar(ProductoCompuestoPorDTO item) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE producto_compuesto_por SET cantidad=? " +
                         "WHERE codigo=? AND codigo_menu=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, item.getCantidad());
                ps.setString(2, item.getCodigoInsumo());
                ps.setString(3, item.getCodigoMenu());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── eliminar(codigoMenu): elimina toda la receta de ese producto ───────
    @Override
    public boolean eliminar(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "DELETE FROM producto_compuesto_por WHERE codigo_menu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): todas las recetas del sistema ──────────────────────
    @Override
    public List<ProductoCompuestoPorDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoCompuestoPorDTO> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT pc.codigo, pc.codigo_menu, pc.cantidad, " +
                         "pi.nombre AS nombre_insumo, pi.existencias, pi.estatus AS estatus_insumo " +
                         "FROM producto_compuesto_por pc " +
                         "JOIN producto_inventario pi ON pc.codigo = pi.codigo " +
                         "ORDER BY pc.codigo_menu, pi.nombre";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCompuesto(rs));
            }
        }
        return lista;
    }

    // ── registrar(item): boolean ───────────────────────────────────────────
    @Override
    public boolean registrar(ProductoCompuestoPorDTO item) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "INSERT INTO producto_compuesto_por (codigo, codigo_menu, cantidad) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, item.getCodigoInsumo());
                ps.setString(2, item.getCodigoMenu());
                ps.setDouble(3, item.getCantidad());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private ProductoCompuestoPorDTO mapearCompuesto(ResultSet rs) throws SQLException {
        ProductoCompuestoPorDTO pc = new ProductoCompuestoPorDTO();
        pc.setCodigoInsumo(rs.getString("codigo"));
        pc.setCodigoMenu(rs.getString("codigo_menu"));
        pc.setCantidad(rs.getDouble("cantidad"));

        ProductoInsumoDTO insumo = new ProductoInsumoDTO();
        insumo.setCodigo(rs.getString("codigo"));
        insumo.setNombre(rs.getString("nombre_insumo"));
        insumo.setExistencias(rs.getInt("existencias"));
        insumo.setEstatus(rs.getInt("estatus_insumo"));
        pc.setInsumo(insumo);

        return pc;
    }
}
