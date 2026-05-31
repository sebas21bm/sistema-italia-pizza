package mx.uv.sistemapizzeria.logica;

import mx.uv.sistemapizzeria.excepciones.UsuarioNoEncontradoException;
import mx.uv.sistemapizzeria.modelo.dao.AutenticacionDAO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Autenticador {

    public static EmpleadoDTO iniciarSesion(String usuario, String password)
            throws NoSuchAlgorithmException, SQLException, IOException, ClassNotFoundException {

        byte[] passwordHashed = hashearContrasenia(password);
        return AutenticacionDAO.autenticarUsuario(usuario, passwordHashed);
    }

    public static byte[] hashearContrasenia(String contrasenia) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(contrasenia.getBytes(StandardCharsets.UTF_8));
    }
}
