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

            DireccionDTO d = empleado.getDireccion();
            String sql = "{CALL editar_empleado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, empleado.getNoEmpleado());
                cs.setString(2, empleado.getNombre());
                cs.setString(3, empleado.getPaterno());
                cs.setString(4, empleado.getMaterno());
                cs.setString(5, empleado.getTelefono());
                cs.setString(6, empleado.getEmail());
                cs.setString(7, empleado.getTipoEmpleado().name());
                cs.setString(8, d.getCalle());
                cs.setString(9, d.getNumero());
                cs.setString(10, d.getCodigoPostal());
                cs.setString(11, d.getCiudad());
                cs.execute();
            }
            return true;
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

            DireccionDTO d = empleado.getDireccion();
            String sql = "{CALL registrar_empleado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, empleado.getNoEmpleado());
                cs.setString(2, empleado.getUsuario());
                cs.setBytes(3, empleado.getContrasenia());
                cs.setString(4, empleado.getNombre());
                cs.setString(5, empleado.getPaterno());
                cs.setString(6, empleado.getMaterno());
                cs.setString(7, empleado.getTelefono());
                cs.setString(8, empleado.getEmail());
                cs.setString(9, empleado.getTipoEmpleado().name());
                cs.setString(10, d.getCalle());
                cs.setString(11, d.getNumero());
                cs.setString(12, d.getCodigoPostal());
                cs.setString(13, d.getCiudad());
                cs.execute();
            }
            return true;
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