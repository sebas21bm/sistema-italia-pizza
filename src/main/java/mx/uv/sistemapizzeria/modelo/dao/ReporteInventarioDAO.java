package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteInventarioDAO implements Operaciones<Integer, ReporteInventarioDTO> {

    // ── buscar(idInventario): ReporteInventario con detalles ───────────────
    @Override
    public ReporteInventarioDTO buscar(Integer idInventario) throws Exception {
        ReporteInventarioDTO reporte = null;

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            // Cabecera
            String sqlCab = "SELECT id_inventario, fecha FROM reporte_inventario WHERE id_inventario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCab)) {
                ps.setInt(1, idInventario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        reporte = new ReporteInventarioDTO();
                        reporte.setIdInventario(rs.getInt("id_inventario"));
                        Timestamp ts = rs.getTimestamp("fecha");
                        if (ts != null) reporte.setFecha(ts.toLocalDateTime());
                    }
                }
            }

            if (reporte == null) return null;

            // Detalles
            String sqlDet = "SELECT dr.id_inventario, dr.codigo, dr.diferencia, dr.justificacion, " +
                            "pi.nombre, pi.existencias, pi.estatus " +
                            "FROM detalle_reporte dr " +
                            "JOIN producto_inventario pi ON dr.codigo = pi.codigo " +
                            "WHERE dr.id_inventario = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlDet)) {
                ps.setInt(1, idInventario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) reporte.agregarDetalle(mapearDetalle(rs));
                }
            }
        }
        return reporte;
    }

    // ── editar(reporte): no aplica — los reportes son inmutables ──────────
    @Override
    public boolean editar(ReporteInventarioDTO reporte) throws Exception {
        throw new UnsupportedOperationException("Los reportes de inventario no son editables.");
    }

    // ── eliminar(idInventario): boolean ───────────────────────────────────
    @Override
    public boolean eliminar(Integer idInventario) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // Eliminar detalles primero (integridad referencial)
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM detalle_reporte WHERE id_inventario = ?")) {
                    ps.setInt(1, idInventario);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM reporte_inventario WHERE id_inventario = ?")) {
                    ps.setInt(1, idInventario);
                    int rows = ps.executeUpdate();
                    conn.commit();
                    return rows > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── mostrarTodos(): List<ReporteInventario> (sin detalles) ─────────────
    @Override
    public List<ReporteInventarioDTO> mostrarTodos() throws Exception {
        List<ReporteInventarioDTO> reportes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT id_inventario, fecha FROM reporte_inventario ORDER BY fecha DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteInventarioDTO r = new ReporteInventarioDTO();
                    r.setIdInventario(rs.getInt("id_inventario"));
                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) r.setFecha(ts.toLocalDateTime());
                    reportes.add(r);
                }
            }
        }
        return reportes;
    }

    // ── registrar(reporte): boolean — transacción completa ─────────────────
    @Override
    public boolean registrar(ReporteInventarioDTO reporte) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Insertar cabecera
                int idGenerado;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO reporte_inventario (fecha) VALUES (NOW())",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo ID del reporte.");
                        idGenerado = keys.getInt(1);
                        reporte.setIdInventario(idGenerado);
                    }
                }

                // 2. Insertar detalles y ajustar existencias en producto_inventario
                String sqlDet = "INSERT INTO detalle_reporte (id_inventario, codigo, diferencia, justificacion) " +
                                "VALUES (?, ?, ?, ?)";
                String sqlAjuste = "UPDATE producto_inventario SET existencias = existencias + ? WHERE codigo = ?";

                try (PreparedStatement psDet    = conn.prepareStatement(sqlDet);
                     PreparedStatement psAjuste = conn.prepareStatement(sqlAjuste)) {

                    for (DetalleReporteDTO det : reporte.getDetalles()) {
                        psDet.setInt(1, idGenerado);
                        psDet.setString(2, det.getCodigoInsumo());
                        psDet.setDouble(3, det.getDiferencia());
                        psDet.setString(4, det.getJustificacion());
                        psDet.addBatch();

                        if (det.hayDiferencia()) {
                            psAjuste.setDouble(1, det.getDiferencia());
                            psAjuste.setString(2, det.getCodigoInsumo());
                            psAjuste.addBatch();
                        }
                    }
                    psDet.executeBatch();
                    psAjuste.executeBatch();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private DetalleReporteDTO mapearDetalle(ResultSet rs) throws SQLException {
        DetalleReporteDTO det = new DetalleReporteDTO();
        det.setIdInventario(rs.getInt("id_inventario"));
        det.setCodigoInsumo(rs.getString("codigo"));
        det.setDiferencia(rs.getDouble("diferencia"));
        det.setJustificacion(rs.getString("justificacion"));

        ProductoInventarioDTO insumo = new ProductoInventarioDTO();
        insumo.setCodigo(rs.getString("codigo"));
        insumo.setNombre(rs.getString("nombre"));
        insumo.setExistencias(rs.getInt("existencias"));
        insumo.setEstatus(rs.getInt("estatus"));
        det.setInsumo(insumo);

        return det;
    }
}
