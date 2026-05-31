package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.excepciones.UsuarioInactivoException;
import mx.uv.sistemapizzeria.excepciones.UsuarioNoEncontradoException;
import mx.uv.sistemapizzeria.logica.Autenticador;
import mx.uv.sistemapizzeria.logica.CargadorEscenas;
import mx.uv.sistemapizzeria.modelo.dao.AutenticacionDAO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txt_usuario;
    @FXML
    private PasswordField txt_contrasenia;
    @FXML
    private Label lb_datosCompletosError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        String usuario = txt_usuario.getText();
        String contrasenia = txt_contrasenia.getText();

        if (!validarEntradaLogin(usuario, contrasenia)) {
            return;
        }

        try {
            EmpleadoDTO empleadoLogin = Autenticador.iniciarSesion(usuario, contrasenia);

            Sesion.empleadoSesion = empleadoLogin;
            SistemaPizzeria.setMetadatos("empleado", empleadoLogin);

            UtilidadesFX.mostrarAlertaSimple("Bienvenido(a)",
                    "Bienvenido al sistema: " + empleadoLogin.getNombreCompleto(),
                    Alert.AlertType.INFORMATION);

            String rutaMenu = CargadorEscenas.cargarEscenaSegunRol(empleadoLogin.getTipoEmpleado());
            cargarEscena(rutaMenu);

        } catch (NoSuchAlgorithmException | SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "Ocurrió un error al intentar iniciar sesión. Causa: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (UsuarioNoEncontradoException e) {
            UtilidadesFX.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.WARNING);
        } catch (UsuarioInactivoException e) {
            UtilidadesFX.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private boolean validarEntradaLogin(String usuario, String contrasenia) {
        if (usuario == null || usuario.isEmpty() ||
                contrasenia == null || contrasenia.isEmpty()) {
            lb_datosCompletosError.setText(
                    "Falta información. Debes introducir tu usuario y contraseña.");
            return false;
        }
        return true;
    }

    private void cargarEscena(String rutaMenu) {
        try {
            SistemaPizzeria.setRoot(rutaMenu, "Menu principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}