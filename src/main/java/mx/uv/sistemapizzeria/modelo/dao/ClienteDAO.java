package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements Operaciones<Integer, ClienteDTO> {

    // ── buscar(identificador: int): Cliente ────────────────────────────────
    // Carga el cliente y su primera dirección asociada vía cliente_direccion
    @Override
    public ClienteDTO buscar(Integer noCliente) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT c.no_cliente, c.nombre, c.paterno, c.materno, c.telefono, " +
                         "c.email, c.estatus, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM cliente c " +
                         "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.id_cliente " +
                         "LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                         "WHERE c.no_cliente = ? " +
                         "LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearCliente(rs);
                }
            }
        }
        return null;
    }

    // ── editar(cliente: Cliente): boolean ──────────────────────────────────
    @Override
    public boolean editar(ClienteDTO cliente) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Actualizar dirección (si existe)
                if (cliente.getDireccion() != null && cliente.getDireccion().getIdDireccion() > 0) {
                    String sqlDir = "UPDATE direccion SET calle=?, numero=?, codigo_postal=?, ciudad=? " +
                                    "WHERE id_direccion=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDir)) {
                        DireccionDTO d = cliente.getDireccion();
                        ps.setString(1, d.getCalle());
                        ps.setString(2, d.getNumero());
                        ps.setString(3, d.getCodigoPostal());
                        ps.setString(4, d.getCiudad());
                        ps.setInt(5, d.getIdDireccion());
                        ps.executeUpdate();
                    }
                }

                // 2. Actualizar datos del cliente
                String sql = "UPDATE cliente SET nombre=?, paterno=?, materno=?, " +
                             "telefono=?, email=?, estatus=? WHERE no_cliente=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getPaterno());
                    ps.setString(3, cliente.getMaterno());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getEmail());
                    ps.setString(6, cliente.getEstatus());
                    ps.setInt(7, cliente.getNoCliente());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── eliminar(identificador: int): boolean ──────────────────────────────
    @Override
    public boolean eliminar(Integer noCliente) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE cliente SET estatus = 0 WHERE no_cliente = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noCliente);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<Cliente> ──────────────────────────────────────
    // Trae cada cliente con su primera dirección asociada
    @Override
    public List<ClienteDTO> mostrarTodos() throws Exception {
        List<ClienteDTO> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT c.no_cliente, c.nombre, c.paterno, c.materno, c.telefono, " +
                         "c.email, c.estatus, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM cliente c " +
                         "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.id_cliente " +
                         "LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                         "GROUP BY c.no_cliente " +
                         "ORDER BY c.paterno, c.nombre";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) clientes.add(mapearCliente(rs));
            }
        }
        return clientes;
    }

    // ── registrar(cliente: Cliente): boolean ───────────────────────────────
    // Inserta dirección, luego cliente, luego vincula en cliente_direccion
    @Override
    public boolean registrar(ClienteDTO cliente) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Insertar cliente
                int noCliente;
                String sqlCliente = "INSERT INTO cliente (nombre, paterno, materno, telefono, email, estatus) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getPaterno());
                    ps.setString(3, cliente.getMaterno());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getEmail());
                    ps.setString(6, cliente.getEstatus() != null ? cliente.getEstatus() : "Activo");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo ID del cliente.");
                        noCliente = keys.getInt(1);
                        cliente.setNoCliente(noCliente);
                    }
                }

                // 2. Insertar dirección si viene con el cliente
                if (cliente.getDireccion() != null) {
                    int idDireccion;
                    String sqlDir = "INSERT INTO direccion (calle, numero, codigo_postal, ciudad) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS)) {
                        DireccionDTO d = cliente.getDireccion();
                        ps.setString(1, d.getCalle());
                        ps.setString(2, d.getNumero());
                        ps.setString(3, d.getCodigoPostal());
                        ps.setString(4, d.getCiudad());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (!keys.next()) throw new SQLException("No se obtuvo ID de dirección.");
                            idDireccion = keys.getInt(1);
                            cliente.getDireccion().setIdDireccion(idDireccion);
                        }
                    }

                    // 3. Vincular cliente <-> dirección
                    String sqlVinculo = "INSERT INTO cliente_direccion (id_cliente, id_direccion) VALUES (?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlVinculo)) {
                        ps.setInt(1, noCliente);
                        ps.setInt(2, idDireccion);
                        ps.executeUpdate();
                    }
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
    private ClienteDTO mapearCliente(ResultSet rs) throws SQLException {
        ClienteDTO c = new ClienteDTO();
        c.setNoCliente(rs.getInt("no_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setPaterno(rs.getString("paterno"));
        c.setMaterno(rs.getString("materno"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setEstatus(rs.getString("estatus"));

        int idDir = rs.getInt("id_direccion");
        if (!rs.wasNull()) {
            DireccionDTO d = new DireccionDTO();
            d.setIdDireccion(idDir);
            d.setCalle(rs.getString("calle"));
            d.setNumero(rs.getString("numero"));
            d.setCodigoPostal(rs.getString("codigo_postal"));
            d.setCiudad(rs.getString("ciudad"));
            c.setDireccion(d);
        }
        return c;
    }
}
