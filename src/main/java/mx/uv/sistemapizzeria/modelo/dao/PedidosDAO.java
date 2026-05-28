package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.excepciones.LimiteInsumosException;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidosDAO implements Operaciones<Integer, PedidoDTO> {

    // ── buscar(identificador: int): Pedido ─────────────────────────────────
    @Override
    public PedidoDTO buscar(Integer idPedido) throws Exception {
        PedidoDTO pedido = null;

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT p.id_pedido, p.fecha, p.total_pagar, p.estatus, p.no_cliente, " +
                         "c.nombre, c.paterno, c.materno, c.telefono, c.email, c.estatus AS estatus_cliente, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM pedido p " +
                         "JOIN cliente c ON p.no_cliente = c.no_cliente " +
                         "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.id_cliente LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                         "WHERE p.id_pedido = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pedido = mapearPedido(rs);
                }
            }

            if (pedido != null) cargarDetalles(conn, pedido);
        }
        return pedido;
    }

    // ── editar(pedido: Pedido): boolean ────────────────────────────────────
    @Override
    public boolean editar(PedidoDTO pedido) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // Eliminar detalles anteriores
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM detalles_pedido WHERE id_pedido = ?")) {
                    ps.setInt(1, pedido.getIdPedido());
                    ps.executeUpdate();
                }

                // Validar existencias y reinsertar
                for (DetallePedidoDTO det : pedido.getDetalles()) {
                    validarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                    insertarDetalle(conn, pedido.getIdPedido(), det);
                    descontarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                }

                // Actualizar total y estatus
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

    // ── eliminar(identificador: int): boolean ──────────────────────────────
    @Override
    public boolean eliminar(Integer idPedido) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE pedido SET estatus = 'Cancelado' WHERE id_pedido = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<Pedido> ───────────────────────────────────────
    @Override
    public List<PedidoDTO> mostrarTodos() throws Exception {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT p.id_pedido, p.fecha, p.total_pagar, p.estatus, p.no_cliente, " +
                         "c.nombre, c.paterno, c.materno, c.telefono, c.email, c.estatus AS estatus_cliente, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM pedido p " +
                         "JOIN cliente c ON p.no_cliente = c.no_cliente " +
                         "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.id_cliente LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                         "ORDER BY p.fecha DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
        }
        return pedidos;
    }

    // ── registrar(pedido: Pedido): boolean ─────────────────────────────────
    @Override
    public boolean registrar(PedidoDTO pedido) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Validar insumos
                for (DetallePedidoDTO det : pedido.getDetalles()) {
                    validarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                }

                // 2. Insertar cabecera
                int idGenerado;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO pedido (fecha, total_pagar, estatus, no_cliente) VALUES (NOW(), ?, 'En proceso', ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDouble(1, pedido.getTotalPagar());
                    ps.setInt(2, pedido.getNoCliente());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo ID del pedido.");
                        idGenerado = keys.getInt(1);
                        pedido.setIdPedido(idGenerado);
                    }
                }

                // 3. Insertar detalles y descontar insumos
                for (DetallePedidoDTO det : pedido.getDetalles()) {
                    insertarDetalle(conn, idGenerado, det);
                    descontarInsumos(conn, det.getCodigoMenu(), det.getCantidad());
                }

                conn.commit();
                return true;
            } catch (SQLException | LimiteInsumosException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── Helpers privados ───────────────────────────────────────────────────

    private void validarInsumos(Connection conn, String codigoMenu, int cantidad) throws SQLException {
        String sql = "SELECT pi.nombre, pi.existencias, pc.cantidad AS por_unidad " +
                     "FROM producto_compuesto_por pc " +
                     "JOIN producto_inventario pi ON pc.codigo = pi.codigo " +
                     "WHERE pc.codigo_menu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoMenu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int existencias = rs.getInt("existencias");
                    double necesario = rs.getDouble("por_unidad") * cantidad;
                    if (existencias < necesario) {
                        throw new LimiteInsumosException(
                            "Stock insuficiente de \"" + rs.getString("nombre") + "\". " +
                            "Disponible: " + existencias + ", requerido: " + (int) necesario
                        );
                    }
                }
            }
        }
    }

    private void insertarDetalle(Connection conn, int idPedido, DetallePedidoDTO det) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO detalles_pedido (id_pedido, codigo_menu, cantidad, costo) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, idPedido);
            ps.setString(2, det.getCodigoMenu());
            ps.setInt(3, det.getCantidad());
            ps.setDouble(4, det.getCosto());
            ps.executeUpdate();
        }
    }

    private void descontarInsumos(Connection conn, String codigoMenu, int cantidad) throws SQLException {
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

    private void cargarDetalles(Connection conn, PedidoDTO pedido) throws SQLException {
        String sql = "SELECT dp.codigo_menu, dp.cantidad, dp.costo, " +
                     "pv.nombre, pv.precio, pv.limite, pv.descripcion, pv.foto, pv.estatus " +
                     "FROM detalles_pedido dp " +
                     "JOIN producto_venta pv ON dp.codigo_menu = pv.codigo_menu " +
                     "WHERE dp.id_pedido = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedido.getIdPedido());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedidoDTO det = new DetallePedidoDTO();
                    det.setIdPedido(pedido.getIdPedido());
                    det.setCodigoMenu(rs.getString("codigo_menu"));
                    det.setCantidad(rs.getInt("cantidad"));
                    det.setCosto(rs.getDouble("costo"));

                    ProductoVentaDTO pv = new ProductoVentaDTO();
                    pv.setCodigoMenu(rs.getString("codigo_menu"));
                    pv.setNombre(rs.getString("nombre"));
                    pv.setPrecio(rs.getDouble("precio"));
                    pv.setLimite(rs.getInt("limite"));
                    pv.setDescripcion(rs.getString("descripcion"));
                    pv.setFoto(rs.getString("foto"));
                    pv.setEstatus(rs.getInt("estatus"));
                    det.setProductoVenta(pv);

                    pedido.agregarDetalle(det);
                }
            }
        }
    }

    private PedidoDTO mapearPedido(ResultSet rs) throws SQLException {
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
        c.setEmail(rs.getString("email"));
        c.setEstatus(rs.getString("estatus_cliente"));

        DireccionDTO d = new DireccionDTO();
        d.setIdDireccion(rs.getInt("id_direccion"));
        d.setCalle(rs.getString("calle"));
        d.setNumero(rs.getString("numero"));
        d.setCodigoPostal(rs.getString("codigo_postal"));
        d.setCiudad(rs.getString("ciudad"));
        c.setDireccion(d);
        p.setCliente(c);

        return p;
    }
}
