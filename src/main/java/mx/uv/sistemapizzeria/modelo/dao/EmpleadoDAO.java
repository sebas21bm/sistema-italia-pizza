package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements Operaciones<String, EmpleadoDTO> {

    @Override
    public EmpleadoDTO buscar(String identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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

    @Override
    public boolean editar(EmpleadoDTO empleado) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
                    ps.setBoolean(6, empleado.getEstatus());
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
    public boolean eliminar(String identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "UPDATE empleado SET estatus = false WHERE no_empleado = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, identificador);
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public List<EmpleadoDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<EmpleadoDTO> empleados = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT e.no_empleado, e.nombre, e.paterno, e.materno, e.telefono, e.email, e.tipo_empleado, " +
                    "e.estatus, d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad FROM empleado e " +
                    "LEFT JOIN direccion d ON d.id_direccion = e.id_direccion";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                empleados.add(mapearEmpleado(rs));
            }
        }
        return empleados;
    }

    // ── registrar(empleado: Empleado): boolean ─────────────────────────────
    @Override
    public boolean registrar(EmpleadoDTO empleado) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
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
                    ps.setBoolean(9, empleado.getEstatus());
                    ps.setString(10, empleado.getTipoEmpleado().name());
                    ps.setInt(11, empleado.getTipoEmpleado().name().equals("Administrador") ? 1 : 2);
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


    public List<EmpleadoDTO> buscarPorNombre(String nombreBusqueda)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<EmpleadoDTO> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT e.no_empleado, e.nombre, e.paterno, e.materno, e.telefono, e.email, e.tipo_empleado, " +
                    "e.estatus, d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad FROM empleado e " +
                    "LEFT JOIN direccion d ON d.id_direccion = e.id_direccion WHERE e.nombre LIKE ? OR e.paterno LIKE ?" +
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

    public List<EmpleadoDTO> buscarPorTelefono(String telefono)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<EmpleadoDTO> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT e.no_empleado, e.nombre, e.paterno, e.materno, e.telefono, e.email, e.tipo_empleado, " +
                    "e.estatus, d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad FROM empleado e " +
                    "LEFT JOIN direccion d ON d.id_direccion = e.id_direccion WHERE e.telefono LIKE ? AND e.estatus = 1";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, "%" + telefono +"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }

        }
        return lista;
    }

    public List<EmpleadoDTO> buscarPorDireccion(String direccion)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException{
        List<EmpleadoDTO> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT e.no_empleado, e.nombre, e.paterno, e.materno, e.telefono, e.email, e.tipo_empleado, " +
                    "e.estatus, d.id_direccion, d.calle, d.numero, d.codigo_postal, d.ciudad FROM empleado e " +
                    "LEFT JOIN direccion d ON d.id_direccion = e.id_direccion WHERE d.calle LIKE ? OR d.numero LIKE ?" +
                    "OR d.codigo_postal LIKE ? OR d.ciudad LIKE ? AND e.estatus = 1";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, "%" + direccion +"%");
            ps.setString(2, "%" + direccion +"%");
            ps.setString(3, "%" + direccion +"%");
            ps.setString(4, "%" + direccion +"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }

        }
        return lista;
    }

    private EmpleadoDTO mapearEmpleado(ResultSet rs) throws SQLException {
        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado(rs.getString("no_empleado"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setPaterno(rs.getString("paterno"));
        empleado.setMaterno(rs.getString("materno"));
        empleado.setTelefono(rs.getString("telefono"));
        empleado.setEmail(rs.getString("email"));
        empleado.setEstatus(rs.getBoolean("estatus"));

        String tipo = rs.getString("tipo_empleado");
        empleado.setTipoEmpleado(TipoEmpleado.valueOf(tipo));

        DireccionDTO d = new DireccionDTO();
        d.setIdDireccion(rs.getInt("id_direccion"));
        d.setCalle(rs.getString("calle"));
        d.setNumero(rs.getString("numero"));
        d.setCodigoPostal(rs.getString("codigo_postal"));
        d.setCiudad(rs.getString("ciudad"));
        empleado.setDireccion(d);

        return empleado;
    }
}
