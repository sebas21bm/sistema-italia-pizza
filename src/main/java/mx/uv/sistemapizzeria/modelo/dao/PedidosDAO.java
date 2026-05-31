package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidosDAO {

    private static final String COLS_VISTA =
            "nombre, paterno, materno, telefono, " +
                    "no_cliente, id_pedido, fecha, total_pagar, estatus, " +
                    "calle, numero, codigo_postal, ciudad";

    private static final String SQL_CREATE_TEMP =
            "CREATE TEMPORARY TABLE IF NOT EXISTS temp_detalles_pedido (" +
                    "  codigo_menu VARCHAR(5) NOT NULL, " +
                    "  cantidad    INT        NOT NULL, " +
                    "  costo       DOUBLE     NOT NULL" +
                    ")";

    public PedidoDTO buscar(Integer idPedido)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        PedidoDTO pedido = null;

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String consulta = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos WHERE id_pedido = ?";

            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pedido = mapearDesdeVista(rs);
            }

            if (pedido != null) {
                cargarDetalles(conn, pedido);
                cargarDireccion(conn, pedido);
            }
        }
        return pedido;
    }

    public boolean editar(PedidoDTO pedido)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            try (Statement st = conn.createStatement()) {
                st.execute(SQL_CREATE_TEMP);
                st.execute("DELETE FROM temp_detalles_pedido");
            }

            for (DetallePedidoDTO det : pedido.getDetalles()) {
                try (CallableStatement cs = conn.prepareCall("{CALL registrar_detalle_pedido(?, ?)}")) {
                    cs.setString(1, det.getCodigoMenu());
                    cs.setInt(2, det.getCantidad());
                    cs.execute();
                }
            }

            try (CallableStatement cs = conn.prepareCall("{CALL editar_pedido(?)}")) {
                cs.setInt(1, pedido.getIdPedido());
                cs.execute();
            }

            return true;
        }
    }

    public boolean cambiarEstatus(Integer idPedido, String nuevoEstatus)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "UPDATE pedido SET estatus = ? WHERE id_pedido = ?";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, nuevoEstatus);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        }
    }

    public List<PedidoDTO> mostrarTodos()
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT " + COLS_VISTA + " FROM vista_lista_pedidos ORDER BY fecha DESC";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearDesdeVista(rs));
            }
        }
        return pedidos;
    }

    public boolean registrar(PedidoDTO pedido)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            try (Statement st = conn.createStatement()) {
                st.execute(SQL_CREATE_TEMP);
                st.execute("DELETE FROM temp_detalles_pedido");
            }

            for (DetallePedidoDTO det : pedido.getDetalles()) {
                try (CallableStatement cs = conn.prepareCall("{CALL registrar_detalle_pedido(?, ?)}")) {
                    cs.setString(1, det.getCodigoMenu());
                    cs.setInt(2, det.getCantidad());
                    cs.execute();
                }
            }

            try (CallableStatement cs = conn.prepareCall("{CALL registrar_pedido(?, ?, ?, ?)}")) {
                cs.setTimestamp(1, Timestamp.valueOf(pedido.getFecha()));
                cs.setString(2, pedido.getEstatus() != null ? pedido.getEstatus() : "En proceso");
                cs.setInt(3, pedido.getNoCliente());
                cs.setInt(4, pedido.getDireccion().getIdDireccion());
                cs.execute();
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs.next()) pedido.setIdPedido(rs.getInt(1));
            }

            return true;
        }
    }

    public List<PedidoDTO> buscarPorEstatus(String estatus)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT " + COLS_VISTA +
                    " FROM vista_lista_pedidos WHERE estatus = ? ORDER BY fecha DESC";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, estatus);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearDesdeVista(rs));
            }

        }
        return pedidos;
    }

    public List<PedidoDTO> buscarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT " + COLS_VISTA + " FROM vista_lista_pedidos " + "WHERE fecha BETWEEN ? AND ?";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            ps.setTimestamp(2, Timestamp.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearDesdeVista(rs));
            }
        }
        return pedidos;
    }

    public List<PedidoDTO> buscarPorCliente(String termino)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<PedidoDTO> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT " + COLS_VISTA + " FROM vista_lista_pedidos" +
                    " WHERE nombre LIKE ? OR paterno LIKE ? ORDER BY fecha DESC";
            PreparedStatement ps = conn.prepareStatement(consulta);
            String like = "%" + termino + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                pedidos.add(mapearDesdeVista(rs));
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

        DireccionDTO dir = new DireccionDTO();
        dir.setCalle(rs.getString("calle"));
        dir.setNumero(rs.getString("numero"));
        dir.setCodigoPostal(rs.getString("codigo_postal"));
        dir.setCiudad(rs.getString("ciudad"));
        p.setDireccion(dir);

        return p;
    }

    private void cargarDetalles(Connection conn, PedidoDTO pedido) throws SQLException {
        String consulta = "SELECT codigo_menu, id_pedido, cantidad, costo, " +
                "total_producto, nombre, precio, foto " +
                "FROM vista_detalles_pedido WHERE id_pedido = ?";

        PreparedStatement ps = conn.prepareStatement(consulta);
        ps.setInt(1, pedido.getIdPedido());
        ResultSet rs = ps.executeQuery();
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

    private void cargarDireccion(Connection conn, PedidoDTO pedido) throws SQLException {
        String consulta = "SELECT d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                "FROM pedido p " +
                "JOIN direccion d ON p.id_direccion = d.id_direccion " +
                "WHERE p.id_pedido = ?";

        PreparedStatement ps = conn.prepareStatement(consulta);
        ps.setInt(1, pedido.getIdPedido());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            DireccionDTO dir = new DireccionDTO();
            dir.setIdDireccion(rs.getInt("id_direccion"));
            dir.setCalle(rs.getString("calle"));
            dir.setNumero(rs.getString("numero"));
            dir.setCodigoPostal(rs.getString("codigo_postal"));
            dir.setCiudad(rs.getString("ciudad"));
            pedido.setDireccion(dir);
        }
    }
}
