package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements Operaciones<String, EmpleadoDTO> {

    // ── buscar(identificador: String): Empleado ────────────────────────────
    @Override
    public EmpleadoDTO buscar(String identificador) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT e.no_empleado, e.usuario, e.nombre, e.paterno, e.materno, " +
                         "e.telefono, e.email, e.estatus, e.tipo_empleado, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM empleado e " +
                         "LEFT JOIN direccion d ON e.id_direccion = d.id_direccion " +
                         "WHERE e.no_empleado = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, identificador);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearEmpleado(rs);
                }
            }
        }
        return null;
    }

    // ── editar(empleado: Empleado): boolean ────────────────────────────────
    @Override
    public boolean editar(EmpleadoDTO empleado) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // Actualizar dirección
                String sqlDir = "UPDATE direccion SET calle=?, numero=?, codigo_postal=?, ciudad=? " +
                                "WHERE id_direccion=?";
                try (PreparedStatement ps = conn.prepareStatement(sqlDir)) {
                    DireccionDTO d = empleado.getDireccion();
                    ps.setString(1, d.getCalle());
                    ps.setString(2, d.getNumero());
                    ps.setString(3, d.getCodigoPostal());
                    ps.setString(4, d.getCiudad());
                    ps.setInt(5, d.getIdDireccion());
                    ps.executeUpdate();
                }

                // Actualizar empleado
                String sql = "UPDATE empleado SET nombre=?, paterno=?, materno=?, " +
                             "telefono=?, email=?, estatus=?, tipo_empleado=? " +
                             "WHERE no_empleado=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, empleado.getNombre());
                    ps.setString(2, empleado.getPaterno());
                    ps.setString(3, empleado.getMaterno());
                    ps.setString(4, empleado.getTelefono());
                    ps.setString(5, empleado.getEmail());
                    ps.setBoolean(6, empleado.isEstatus());
                    ps.setString(7, empleado.getTipoEmpleado().name());
                    ps.setString(8, empleado.getNoEmpleado());
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

    // ── eliminar(identificador: String): boolean ───────────────────────────
    @Override
    public boolean eliminar(String identificador) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE empleado SET estatus = false WHERE no_empleado = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, identificador);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ── mostrarTodos(): List<Empleado> ─────────────────────────────────────
    @Override
    public List<EmpleadoDTO> mostrarTodos() throws Exception {
        List<EmpleadoDTO> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT e.no_empleado, e.usuario, e.nombre, e.paterno, e.materno, " +
                         "e.telefono, e.email, e.estatus, e.tipo_empleado, " +
                         "d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad " +
                         "FROM empleado e " +
                         "LEFT JOIN direccion d ON e.id_direccion = d.id_direccion " +
                         "ORDER BY e.paterno, e.nombre";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) empleados.add(mapearEmpleado(rs));
            }
        }
        return empleados;
    }

    // ── registrar(empleado: Empleado): boolean ─────────────────────────────
    @Override
    public boolean registrar(EmpleadoDTO empleado) throws Exception {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            conn.setAutoCommit(false);
            try {
                // 1. Insertar dirección
                int idDireccion;
                String sqlDir = "INSERT INTO direccion (calle, numero, codigo_postal, ciudad) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS)) {
                    DireccionDTO d = empleado.getDireccion();
                    ps.setString(1, d.getCalle());
                    ps.setString(2, d.getNumero());
                    ps.setString(3, d.getCodigoPostal());
                    ps.setString(4, d.getCiudad());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo ID de dirección.");
                        idDireccion = keys.getInt(1);
                    }
                }

                // 2. Insertar empleado
                String sql = "INSERT INTO empleado (no_empleado, usuario, contrasenia, nombre, paterno, materno, " +
                             "telefono, email, estatus, tipo_empleado, id_rol, id_direccion) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, empleado.getNoEmpleado());
                    ps.setString(2, empleado.getUsuario());
                    ps.setBytes(3, empleado.getContrasenia());
                    ps.setString(4, empleado.getNombre());
                    ps.setString(5, empleado.getPaterno());
                    ps.setString(6, empleado.getMaterno());
                    ps.setString(7, empleado.getTelefono());
                    ps.setString(8, empleado.getEmail());
                    ps.setBoolean(9, empleado.isEstatus());
                    ps.setString(10, empleado.getTipoEmpleado().name());
                    ps.setInt(11, empleado.getTipoEmpleado().name().equals("Admnistrador") ? 1 : 2);
                    ps.setInt(12, idDireccion);
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

    // ── Helper ─────────────────────────────────────────────────────────────
    private EmpleadoDTO mapearEmpleado(ResultSet rs) throws SQLException {
        EmpleadoDTO e = new EmpleadoDTO();
        e.setNoEmpleado(rs.getString("no_empleado"));
        e.setUsuario(rs.getString("usuario"));
        e.setNombre(rs.getString("nombre"));
        e.setPaterno(rs.getString("paterno"));
        e.setMaterno(rs.getString("materno"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setEstatus(rs.getBoolean("estatus"));

        String tipo = rs.getString("tipo_empleado");
        if (tipo != null) {
            try {
                e.setTipoEmpleado(mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado.valueOf(tipo.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        DireccionDTO d = new DireccionDTO();
        d.setIdDireccion(rs.getInt("id_direccion"));
        d.setCalle(rs.getString("calle"));
        d.setNumero(rs.getString("numero"));
        d.setCodigoPostal(rs.getString("codigo_postal"));
        d.setCiudad(rs.getString("ciudad"));
        e.setDireccion(d);

        return e;
    }
}
