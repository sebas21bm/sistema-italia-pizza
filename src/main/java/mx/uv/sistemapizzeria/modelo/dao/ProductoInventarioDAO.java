package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoInventarioDAO implements Operaciones<String, ProductoInventarioDTO> {
    // ── buscar(identificador: String): ProductoInsumo ──────────────────────
    @Override
    public ProductoInventarioDTO buscarPorCodigo(String codigo) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto " +
                         "FROM producto_inventario WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearInsumo(rs);
                }
            }
        }
        return null;
    }

    public ProductoInventarioDTO buscarPorNombre(String nombre) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto " +
                    "FROM producto_inventario WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearInsumo(rs);
                }
            }
        }
        return null;
    }


    // ── editar(insumo: ProductoInsumo): boolean ────────────────────────────
    @Override
    public boolean editar(ProductoInventarioDTO insumo) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE producto_inventario SET nombre=?, existencias=?, fecha_caducidad=?, foto=? " +
                         "WHERE codigo=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, insumo.getNombre());
                ps.setInt(2, insumo.getExistencias());
                ps.setDate(3, insumo.getFechaCaducidad() != null
                        ? Date.valueOf(insumo.getFechaCaducidad()) : null);
                ps.setString(4, insumo.getFoto());
                ps.setString(5, insumo.getCodigo());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── eliminar(identificador: String): boolean ───────────────────────────
    @Override
    public boolean eliminar(String codigo) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE producto_inventario SET estatus = 0 WHERE codigo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<ProductoInsumo> ───────────────────────────────
    @Override
    public List<ProductoInventarioDTO> mostrarTodos() throws Exception {
        List<ProductoInventarioDTO> insumos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto " +
                         "FROM producto_inventario WHERE estatus = 1 ORDER BY nombre";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) insumos.add(mapearInsumo(rs));
            }
        }
        return insumos;
    }

    // ── registrar(insumo: ProductoInsumo): boolean ─────────────────────────
    @Override
    public boolean registrar(ProductoInventarioDTO insumo) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "INSERT INTO producto_inventario (codigo, nombre, estatus, existencias, fecha_caducidad, foto) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, insumo.getCodigo());
                ps.setString(2, insumo.getNombre());
                ps.setInt(3, insumo.getEstatus());
                ps.setInt(4, insumo.getExistencias());
                ps.setDate(5, insumo.getFechaCaducidad() != null
                        ? Date.valueOf(insumo.getFechaCaducidad()) : null);
                ps.setString(6, insumo.getFoto());
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private ProductoInventarioDTO mapearInsumo(ResultSet rs) throws SQLException {
        ProductoInventarioDTO i = new ProductoInventarioDTO();
        i.setCodigo(rs.getString("codigo"));
        i.setNombre(rs.getString("nombre"));
        i.setEstatus(rs.getInt("estatus"));
        i.setExistencias(rs.getInt("existencias"));
        Date fecha = rs.getDate("fecha_caducidad");
        if (fecha != null) i.setFechaCaducidad(fecha.toLocalDate());
        i.setFoto(rs.getString("foto"));
        return i;
    }
}
