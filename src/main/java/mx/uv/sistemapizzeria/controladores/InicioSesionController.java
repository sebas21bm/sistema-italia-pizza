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
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txt_usuario;
    @FXML
    private PasswordField txt_contrasenia;
    @FXML
    private Label lb_datosCompletosError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        String usuario = txt_usuario.getText();
        String contrasenia = txt_contrasenia.getText();

        lb_datosCompletosError.setText(""); // Limpiar error anterior

        if (!validarEntradaLogin(usuario, contrasenia)) {
            return;
        }

        try {
            EmpleadoDTO empleadoLogin = Autenticador.iniciarSesion(usuario, contrasenia);

            // 1. Guardar sesión PRIMERO antes de cargar cualquier escena
            SistemaPizzeria.setMetadatos("empleado", empleadoLogin);

            // 2. Determinar ruta según rol
            String rutaMenu = CargadorEscenas.cargarEscenaSegunRol(empleadoLogin.getTipoEmpleado());

            if (rutaMenu == null) {
                UtilidadesFX.mostrarAlertaSimple("Error",
                        "No se encontró una pantalla para el rol: " + empleadoLogin.getTipoEmpleado(),
                        Alert.AlertType.ERROR);
                return;
            }

            // 3. Mostrar bienvenida
            UtilidadesFX.mostrarAlertaSimple("Bienvenido(a)",
                    "Bienvenido al sistema: " + empleadoLogin.getNombreCompleto(),
                    Alert.AlertType.INFORMATION);

            // 4. Cambiar de escena
            SistemaPizzeria.setRoot(rutaMenu, "Menú principal");

        } catch (NoSuchAlgorithmException | SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "Ocurrió un error al intentar iniciar sesión. Causa: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (UsuarioNoEncontradoException e) {
            UtilidadesFX.mostrarAlertaSimple("Usuario no encontrado", e.getMessage(), Alert.AlertType.WARNING);
        } catch (UsuarioInactivoException e) {
            UtilidadesFX.mostrarAlertaSimple("Usuario inactivo", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private boolean validarEntradaLogin(String usuario, String contrasenia) {
        if (usuario == null || usuario.isEmpty() ||
                contrasenia == null || contrasenia.isEmpty()) {
            lb_datosCompletosError.setText("Falta información. Debes introducir tu usuario y contraseña.");
            return false;
        }
        return true;
    }
}
