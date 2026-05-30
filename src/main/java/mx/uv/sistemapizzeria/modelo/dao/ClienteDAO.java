package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClienteDAO implements Operaciones<Integer, ClienteDTO> {

    // Columnas base que se repiten en todas las queries
    private static final String COLS_CLIENTE =
            "c.no_cliente, c.nombre, c.paterno, c.materno, c.telefono, c.email, c.estatus, " +
                    "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad ";

    private static final String JOINS_DIRECCION =
            "FROM cliente c " +
                    "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.no_cliente " +
                    "LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion ";

    // ── buscar(identificador: int): ClienteDTO ─────────────────────────────
    @Override
    public ClienteDTO buscar(Integer noCliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_CLIENTE + JOINS_DIRECCION + "WHERE c.no_cliente = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    List<ClienteDTO> resultado = mapearClientes(rs);
                    return resultado.isEmpty() ? null : resultado.get(0);
                }
            }
        }
    }

    // ── editar(cliente: ClienteDTO): boolean ───────────────────────────────
    @Override
    public boolean editar(ClienteDTO cliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            // Crear tabla temporal para esta sesión (es por sesión, no persiste entre conexiones)
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_direcciones (" +
                        "calle VARCHAR(45) NOT NULL, " +
                        "numero VARCHAR(4) NOT NULL, " +
                        "codigo_postal VARCHAR(5) NOT NULL, " +
                        "ciudad VARCHAR(20) NOT NULL)");
                st.execute("DELETE FROM temp_direcciones");
            }

            // 1. Llenar tabla temporal con las direcciones actualizadas
            String sqlDir = "{CALL registrar_direcciones_temporales(?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sqlDir)) {
                for (DireccionDTO d : cliente.getDirecciones()) {
                    cs.setString(1, d.getCalle());
                    cs.setString(2, d.getNumero());
                    cs.setString(3, d.getCodigoPostal());
                    cs.setString(4, d.getCiudad());
                    cs.execute();
                }
            }

            // 2. Llamar al stored procedure principal
            String sql = "{CALL editar_cliente(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, cliente.getNoCliente());
                cs.setString(2, cliente.getNombre());
                cs.setString(3, cliente.getPaterno());
                cs.setString(4, cliente.getMaterno());
                cs.setString(5, cliente.getTelefono());
                cs.setString(6, cliente.getEmail());
                cs.execute();
            }
            return true;
        }
    }

    // ── eliminar(identificador: int): boolean ──────────────────────────────
    @Override
    public boolean eliminar(Integer noCliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "{CALL eliminar_cliente(?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, noCliente);
                cs.execute();
            }
            return true;
        }
    }

    // ── mostrarTodos(): List<ClienteDTO> ───────────────────────────────────
    @Override
    public List<ClienteDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_CLIENTE + JOINS_DIRECCION +
                    "ORDER BY c.paterno, c.nombre, d.id_direccion";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                return mapearClientes(rs);
            }
        }
    }

    // ── registrar(cliente: ClienteDTO): boolean ────────────────────────────
    @Override
    public boolean registrar(ClienteDTO cliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            // Crear tabla temporal para esta sesión (es por sesión, no persiste entre conexiones)
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_direcciones (" +
                        "calle VARCHAR(45) NOT NULL, " +
                        "numero VARCHAR(4) NOT NULL, " +
                        "codigo_postal VARCHAR(5) NOT NULL, " +
                        "ciudad VARCHAR(20) NOT NULL)");
                st.execute("DELETE FROM temp_direcciones");
            }

            // 1. Llenar tabla temporal con las direcciones
            String sqlDir = "{CALL registrar_direcciones_temporales(?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sqlDir)) {
                for (DireccionDTO d : cliente.getDirecciones()) {
                    cs.setString(1, d.getCalle());
                    cs.setString(2, d.getNumero());
                    cs.setString(3, d.getCodigoPostal());
                    cs.setString(4, d.getCiudad());
                    cs.execute();
                }
            }

            // 2. Llamar al stored procedure principal
            String sql = "{CALL registrar_cliente(?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, cliente.getNombre());
                cs.setString(2, cliente.getPaterno());
                cs.setString(3, cliente.getMaterno());
                cs.setString(4, cliente.getTelefono());
                cs.setString(5, cliente.getEmail());
                cs.execute();
            }
            return true;
        }
    }

    // ── buscarPorNombre ────────────────────────────────────────────────────
    public List<ClienteDTO> buscarPorNombre(String nombreBusqueda)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_CLIENTE + JOINS_DIRECCION +
                    "WHERE (c.nombre LIKE ? OR c.paterno LIKE ? OR c.materno LIKE ?) " +
                    "ORDER BY c.paterno, c.nombre, d.id_direccion";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String patron = "%" + nombreBusqueda + "%";
                ps.setString(1, patron);
                ps.setString(2, patron);
                ps.setString(3, patron);
                try (ResultSet rs = ps.executeQuery()) {
                    return mapearClientes(rs);
                }
            }
        }
    }

    // ── buscarPorTelefono ──────────────────────────────────────────────────
    public List<ClienteDTO> buscarPorTelefono(String telefono)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_CLIENTE + JOINS_DIRECCION +
                    "WHERE c.telefono LIKE ? " +
                    "ORDER BY c.paterno, c.nombre, d.id_direccion";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + telefono + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    return mapearClientes(rs);
                }
            }
        }
    }

    // ── buscarPorDireccion ─────────────────────────────────────────────────
    public List<ClienteDTO> buscarPorDireccion(String direccion)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT " + COLS_CLIENTE + JOINS_DIRECCION +
                    "WHERE (d.calle LIKE ? OR d.numero LIKE ? OR d.codigo_postal LIKE ? OR d.ciudad LIKE ?) " +
                    "ORDER BY c.paterno, c.nombre, d.id_direccion";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String patron = "%" + direccion + "%";
                ps.setString(1, patron);
                ps.setString(2, patron);
                ps.setString(3, patron);
                ps.setString(4, patron);
                try (ResultSet rs = ps.executeQuery()) {
                    return mapearClientes(rs);
                }
            }
        }
    }

    // ── Helper: mapea ResultSet → List<ClienteDTO> ─────────────────────────
    private List<ClienteDTO> mapearClientes(ResultSet rs) throws SQLException {
        Map<Integer, ClienteDTO> mapaClientes = new LinkedHashMap<>();

        while (rs.next()) {
            int noCliente = rs.getInt("no_cliente");

            ClienteDTO c = mapaClientes.get(noCliente);
            if (c == null) {
                c = new ClienteDTO();
                c.setNoCliente(noCliente);
                c.setNombre(rs.getString("nombre"));
                c.setPaterno(rs.getString("paterno"));
                c.setMaterno(rs.getString("materno"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setEstatus(rs.getBoolean("estatus"));
                c.setDirecciones(new ArrayList<>());
                mapaClientes.put(noCliente, c);
            }

            int idDir = rs.getInt("id_direccion");
            if (!rs.wasNull()) {
                DireccionDTO d = new DireccionDTO();
                d.setIdDireccion(idDir);
                d.setCalle(rs.getString("calle"));
                d.setNumero(rs.getString("numero"));
                d.setCodigoPostal(rs.getString("codigo_postal"));
                d.setCiudad(rs.getString("ciudad"));
                c.getDirecciones().add(d);
            }
        }

        return new ArrayList<>(mapaClientes.values());
    }
}