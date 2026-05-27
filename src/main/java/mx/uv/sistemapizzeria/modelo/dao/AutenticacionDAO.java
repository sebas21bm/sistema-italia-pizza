package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.excepciones.UsuarioInactivoException;
import mx.uv.sistemapizzeria.excepciones.UsuarioNoEncontradoException;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutenticacionDAO {

    public static EmpleadoDTO autenticarUsuario(String usuario, byte[] contrasenia) throws SQLException, IOException,
            ClassNotFoundException {

        EmpleadoDTO empleadoLogin = new EmpleadoDTO();

        try (Connection conn = ConnectionFactory.crearParaAutenticacion()){
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT no_empleado, usuario, contrasenia, nombre, paterno, materno, id_rol, " +
                    "estatus, tipo_empleado FROM empleado WHERE usuario = ? AND contrasenia = ? ";
            PreparedStatement ps = conn.prepareStatement(consulta);
            ps.setString(1, usuario);
            ps.setBytes(2, contrasenia);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new UsuarioNoEncontradoException("Usuario no encontrado. El usuario y/o contraseña no coinciden.");
            }

            if (!rs.getBoolean("estatus")) {
                throw new UsuarioInactivoException("Lo sentimos, no puedes iniciar sesión en el sistema");
            }

            empleadoLogin.setNombre(rs.getString("nombre"));
            empleadoLogin.setPaterno(rs.getString("paterno"));
            empleadoLogin.setMaterno(rs.getString("materno"));
            empleadoLogin.setNoEmpleado(rs.getString("no_empleado"));

            int idRol = rs.getInt("id_rol");
            switch (idRol) {
                case 1:
                    empleadoLogin.setTipoEmpleado(TipoEmpleado.ADMINISTRADOR);
                    break;
                case 2:
                    empleadoLogin.setTipoEmpleado(TipoEmpleado.CAJERO);
                    break;
                default:
                    throw new NullPointerException();
            }
        }

        return empleadoLogin;

    }
}
