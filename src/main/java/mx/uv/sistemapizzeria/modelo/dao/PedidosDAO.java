package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.excepciones.LimiteInsumosException;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidosDAO implements Operaciones<Integer, PedidoDTO> {

    // Columnas reales de vista_lista_pedidos (CONSULTAS.sql):
    // nombre, paterno, materno, telefono, no_cliente, id_pedido, fecha, total_pagar, estatus
    private static final String COLS_VISTA =
            "nombre, paterno, materno, telefono, " +
                    "no_cliente, id_pedido, fecha, total_pagar, estatus";

    @Override
    public PedidoDTO buscar(Integer idPedido) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        PedidoDTO pedido = null;

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos WHERE id_pedido = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pedido = mapearDesdeVista(rs);
                }
            }

            if (pedido != null) cargarDetalles(conn, pedido);
        }
        return pedido;
    }

    @Override
    public boolean editar(PedidoDTO pedido) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM detalles_pedido WHERE id_pedido = ?")) {
                    ps.setInt(1, pedido.getIdPedido());
                    ps.executeUpdate();
                }

                for (DetallePedidoDTO det : pedido.getDetalles()) {
                    validarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                    insertarDetalle(conn, pedido.getIdPedido(), det);
                    descontarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE pedido SET total_pagar=?, estatus=? WHERE id_pedido=?")) {
                    ps.setDouble(1, pedido.getTotalPagar());
                    ps.setString(2, pedido.getEstatus());
                    ps.setInt(3, pedido.getIdPedido());
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException | LimiteInsumosException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean eliminar(Integer idPedido) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pedido SET estatus = 'Cancelado' WHERE id_pedido = ?")) {
                ps.setInt(1, idPedido);
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public List<PedidoDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos ORDER BY fecha DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pedidos.add(mapearDesdeVista(rs));
            }
        }
        return pedidos;
    }

    @Override
    public boolean registrar(PedidoDTO pedido) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {

            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            try (Statement st = conn.createStatement()) {
                st.execute(
                        "CREATE TEMPORARY TABLE IF NOT EXISTS temp_detalles_pedido (" +
                                "  codigo_menu VARCHAR(5) NOT NULL, " +
                                "  cantidad    INT        NOT NULL, " +
                                "  costo       DOUBLE     NOT NULL" +
                                ")"
                );
                st.execute("DELETE FROM temp_detalles_pedido");
            }

            for (DetallePedidoDTO det : pedido.getDetalles()) {
                try (CallableStatement cs = conn.prepareCall(
                        "{CALL registrar_detalle_pedido(?, ?)}")) {
                    cs.setString(1, det.getCodigoMenu());
                    cs.setInt(2, det.getCantidad());
                    cs.execute();
                }
            }

            try (CallableStatement cs = conn.prepareCall(
                    "{CALL registrar_pedido(?, ?, ?, ?)}")) {
                cs.setTimestamp(1, Timestamp.valueOf(pedido.getFecha()));
                cs.setString(2, pedido.getEstatus() != null
                        ? pedido.getEstatus() : "En proceso");
                cs.setInt(3, pedido.getNoCliente());
                cs.setInt(4, pedido.getDireccion().getIdDireccion());
                cs.execute();
            }

            // Recuperar el último ID generado
            String consulta = "SELECT id_pedido FROM pedido ORDER BY id_pedido DESC LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(consulta);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    pedido.setIdPedido(rs.getInt("id_pedido"));
                }
            }

            return true;
        }
    }

    public List<PedidoDTO> buscarPorEstatus(String estatus) throws Exception {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos WHERE estatus = ? ORDER BY fecha DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estatus);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) pedidos.add(mapearDesdeVista(rs));
                }
            }
        }
        return pedidos;
    }

    public List<PedidoDTO> buscarPorCliente(String termino) throws Exception {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos" +
                    " WHERE nombre LIKE ? OR paterno LIKE ?" +
                    " ORDER BY fecha DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String like = "%" + termino + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) pedidos.add(mapearDesdeVista(rs));
                }
            }
        }
        return pedidos;
    }

    private PedidoDTO mapearDesdeVista(ResultSet rs) throws SQLException {
        PedidoDTO p = new PedidoDTO();
        p.setIdPedido(rs.getInt("id_pedido"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) p.setFecha(ts.toLocalDateTime());
        p.setTotalPagar(rs.getDouble("total_pagar"));
        p.setEstatus(rs.getString("estatus"));
        p.setNoCliente(rs.getInt("no_cliente"));

        ClienteDTO c = new ClienteDTO();
        c.setNoCliente(rs.getInt("no_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setPaterno(rs.getString("paterno"));
        c.setMaterno(rs.getString("materno"));
        c.setTelefono(rs.getString("telefono"));
        p.setCliente(c);

        return p;
    }

    private void cargarDetalles(Connection conn, PedidoDTO pedido) throws SQLException {
        // Usa vista_detalles_pedido de CONSULTAS.sql
        String sql = "SELECT codigo_menu, id_pedido, cantidad, costo, " +
                "total_producto, nombre, precio, foto " +
                "FROM vista_detalles_pedido WHERE id_pedido = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedido.getIdPedido());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedidoDTO det = new DetallePedidoDTO();
                    det.setIdPedido(rs.getInt("id_pedido"));
                    det.setCodigoMenu(rs.getString("codigo_menu"));
                    det.setCantidad(rs.getInt("cantidad"));
                    det.setCosto(rs.getDouble("costo"));

                    ProductoVentaDTO pv = new ProductoVentaDTO();
                    pv.setCodigoMenu(rs.getString("codigo_menu"));
                    pv.setNombre(rs.getString("nombre"));
                    pv.setPrecio(rs.getDouble("precio"));
                    pv.setFoto(rs.getString("foto"));
                    det.setProductoVenta(pv);

                    pedido.agregarDetalle(det);
                }
            }
        }
    }

    private void validarInsumos(Connection conn, String codigoMenu, int cantidad)
            throws SQLException {
        String sql = "SELECT pi.nombre, pi.existencias, pc.cantidad AS por_unidad " +
                "FROM producto_compuesto_por pc " +
                "JOIN producto_inventario pi ON pc.codigo = pi.codigo " +
                "WHERE pc.codigo_menu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoMenu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double necesario = rs.getDouble("por_unidad") * cantidad;
                    if (rs.getInt("existencias") < necesario) {
                        throw new LimiteInsumosException(
                                "Stock insuficiente de \"" + rs.getString("nombre") + "\". " +
                                        "Disponible: " + rs.getInt("existencias") +
                                        ", requerido: " + (int) necesario);
                    }
                }
            }
        }
    }

    private void insertarDetalle(Connection conn, int idPedido, DetallePedidoDTO det)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO detalles_pedido (id_pedido, codigo_menu, cantidad, costo) " +
                        "VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idPedido);
            ps.setString(2, det.getCodigoMenu());
            ps.setInt(3, det.getCantidad());
            ps.setDouble(4, det.getCosto());
            ps.executeUpdate();
        }
    }

    private void descontarInsumos(Connection conn, String codigoMenu, int cantidad)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE producto_inventario pi " +
                        "JOIN producto_compuesto_por pc ON pi.codigo = pc.codigo " +
                        "SET pi.existencias = pi.existencias - (pc.cantidad * ?) " +
                        "WHERE pc.codigo_menu = ?")) {
            ps.setInt(1, cantidad);
            ps.setString(2, codigoMenu);
            ps.executeUpdate();
        }
    }
}
