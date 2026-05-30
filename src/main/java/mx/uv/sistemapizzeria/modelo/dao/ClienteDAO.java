package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClienteDAO implements Operaciones<Integer, ClienteDTO> {

    // ── buscar(identificador: int): ClienteDTO ─────────────────────────────
    // Carga el cliente con TODAS sus direcciones asociadas vía cliente_direccion
    @Override
    public ClienteDTO buscar(Integer noCliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT c.no_cliente, c.nombre, c.paterno, c.materno, c.telefono, " +
                    "c.email, c.estatus, " +
                    "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                    "FROM cliente c " +
                    "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.no_cliente " +
                    "LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                    "WHERE c.no_cliente = ?";

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
    // Actualiza los datos personales del cliente.
    // NOTA: la edición de direcciones individuales se maneja por separado
    // con el procedimiento almacenado cuando esté disponible su nombre.
    @Override
    public boolean editar(ClienteDTO cliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Actualizar datos personales del cliente
                String sqlCliente = "UPDATE cliente SET nombre=?, paterno=?, materno=?, " +
                        "telefono=?, email=?, estatus=? WHERE no_cliente=?";
                try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getPaterno());
                    ps.setString(3, cliente.getMaterno());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getEmail());
                    ps.setBoolean(6, cliente.getEstatus());
                    ps.setInt(7, cliente.getNoCliente());
                    ps.executeUpdate();
                }

                // 2. Actualizar cada dirección de la lista
                // TODO: reemplazar por CALL nombre_procedimiento_editar_direccion(?)
                //       cuando Sebas confirme el nombre del stored procedure.
                //       Por ahora se actualiza con UPDATE directo (igual que el DAO de producto).
                if (cliente.getDirecciones() != null) {
                    String sqlDir = "UPDATE direccion SET calle=?, numero=?, codigo_postal=?, ciudad=? " +
                            "WHERE id_direccion=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDir)) {
                        for (DireccionDTO d : cliente.getDirecciones()) {
                            if (d.getIdDireccion() > 0) {
                                ps.setString(1, d.getCalle());
                                ps.setString(2, d.getNumero());
                                ps.setString(3, d.getCodigoPostal());
                                ps.setString(4, d.getCiudad());
                                ps.setInt(5, d.getIdDireccion());
                                ps.executeUpdate();
                            }
                        }
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

    // ── eliminar(identificador: int): boolean ──────────────────────────────
    // Baja lógica: pone estatus = false (0)
    @Override
    public boolean eliminar(Integer noCliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE cliente SET estatus = 0 WHERE no_cliente = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, noCliente);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<ClienteDTO> ───────────────────────────────────
    // Trae todos los clientes, cada uno con su lista completa de direcciones
    @Override
    public List<ClienteDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT c.no_cliente, c.nombre, c.paterno, c.materno, c.telefono, " +
                    "c.email, c.estatus, " +
                    "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                    "FROM cliente c " +
                    "LEFT JOIN cliente_direccion cd ON c.no_cliente = cd.no_cliente " +
                    "LEFT JOIN direccion d ON cd.id_direccion = d.id_direccion " +
                    "ORDER BY c.paterno, c.nombre, d.id_direccion";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                return mapearClientes(rs);
            }
        }
    }

    // ── registrar(cliente: ClienteDTO): boolean ────────────────────────────
    // Usa el procedimiento almacenado para insertar cliente + direcciones en una transacción
    @Override
    public boolean registrar(ClienteDTO cliente) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Insertar cliente y obtener su ID generado
                int noCliente;
                String sqlCliente = "INSERT INTO cliente (nombre, paterno, materno, telefono, email, estatus) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getPaterno());
                    ps.setString(3, cliente.getMaterno());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getEmail());
                    ps.setBoolean(6, cliente.getEstatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo ID del cliente.");
                        noCliente = keys.getInt(1);
                        cliente.setNoCliente(noCliente);
                    }
                }

                // 2. Registrar cada dirección vinculándola al cliente
                // TODO: reemplazar el bloque interno por CALL nombre_procedimiento_registrar_direccion(?, ?, ?, ?, ?)
                //       cuando Sebas confirme el nombre del stored procedure.
                //       El procedimiento debería recibir: no_cliente, calle, numero, codigo_postal, ciudad
                //       y hacer el INSERT en direccion + INSERT en cliente_direccion internamente.
                if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
                    String sqlDir = "INSERT INTO direccion (calle, numero, codigo_postal, ciudad) VALUES (?, ?, ?, ?)";
                    String sqlVinculo = "INSERT INTO cliente_direccion (no_cliente, id_direccion) VALUES (?, ?)";

                    for (DireccionDTO d : cliente.getDirecciones()) {
                        int idDireccion;
                        try (PreparedStatement ps = conn.prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS)) {
                            ps.setString(1, d.getCalle());
                            ps.setString(2, d.getNumero());
                            ps.setString(3, d.getCodigoPostal());
                            ps.setString(4, d.getCiudad());
                            ps.executeUpdate();
                            try (ResultSet keys = ps.getGeneratedKeys()) {
                                if (!keys.next()) throw new SQLException("No se obtuvo ID de dirección.");
                                idDireccion = keys.getInt(1);
                                d.setIdDireccion(idDireccion);
                            }
                        }
                        try (PreparedStatement ps = conn.prepareStatement(sqlVinculo)) {
                            ps.setInt(1, noCliente);
                            ps.setInt(2, idDireccion);
                            ps.executeUpdate();
                        }
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

    // ── Helper: mapea un ResultSet a List<ClienteDTO> ──────────────────────
    // Un mismo cliente puede aparecer en varias filas (una por dirección).
    // Se usa LinkedHashMap para conservar el orden de llegada (ORDER BY del SQL).
    private List<ClienteDTO> mapearClientes(ResultSet rs) throws SQLException {
        Map<Integer, ClienteDTO> mapaClientes = new LinkedHashMap<>();

        while (rs.next()) {
            int noCliente = rs.getInt("no_cliente");

            // Si el cliente no está en el mapa aún, lo creamos
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

            // Agregar la dirección de esta fila (si existe — LEFT JOIN puede traer null)
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

    public List<ClienteDTO> buscarPorNombre(String nombreBusqueda)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<ClienteDTO> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT e.no_cliente, e.nombre, e.paterno, e.materno, e.telefono, e.email, e.estatus, " +
                    "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad FROM empleado e " +
                    "LEFT JOIN direccion d ON d.id_direccion = e.id_direccion " +
                    "JOIN cliente_direcciones cd ON c.no_cliente = cd.no_cliente " +
                    "WHERE e.nombre LIKE ? OR e.paterno LIKE ?" +
                    "OR e.materno LIKE ? AND e.estatus = 1";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, "%" + nombreBusqueda +"%");
            ps.setString(2, "%" + nombreBusqueda +"%");
            ps.setString(3, "%" + nombreBusqueda +"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }

        }
        return lista;
    }

    public List<ClienteDTO> buscarPorTelefono(String campoBusqueda) {
        // TODO implementacion
        return null;
    }

    public List<ClienteDTO> buscarPorDireccion(String campoBusqueda) {
        // TODO implementacion
        return null;
    }
}
