/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import jdk.jshell.execution.Util;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.excepciones.UsuarioInactivoException;
import mx.uv.sistemapizzeria.excepciones.UsuarioNoEncontradoException;
import mx.uv.sistemapizzeria.logica.Autenticador;
import mx.uv.sistemapizzeria.logica.CargadorEscenas;
import mx.uv.sistemapizzeria.modelo.dao.AutenticacionDAO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class InicioSesionController implements Initializable {


    @FXML
    private TextField txt_usuario;
    @FXML
    private PasswordField txt_contrasenia;
    @FXML
    private Label lb_datosCompletosError;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        String usuario = txt_usuario.getText();
        String contrasenia = txt_contrasenia.getText();

        if (!validarEntradaLogin(usuario, contrasenia)) {
            return;
        }

        try {
            EmpleadoDTO empleadoLogin = Autenticador.iniciarSesion(usuario, contrasenia);

            UtilidadesFX.mostrarAlertaSimple("Bienvenido(a)", "Bienvendio al sistema: " +
                    empleadoLogin.getNombreCompleto(), Alert.AlertType.INFORMATION);

            String rutaMenu = CargadorEscenas.cargarEscenarSegunRol(empleadoLogin.getTipoEmpleado());

            SistemaPizzeria.setMetadatos("empleado",  empleadoLogin);

            cargarEscena(rutaMenu);
        } catch (NoSuchAlgorithmException | SQLException | IOException | ClassNotFoundException e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "Ocurrió un error al intentar iniciar sesión. " +
                    "Causa: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (UsuarioNoEncontradoException e) {
            UtilidadesFX.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.WARNING);
        } catch (UsuarioInactivoException e) {
            UtilidadesFX.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.WARNING);
        }

    }

    private boolean validarEntradaLogin(String usuario, String contrasenia){
        boolean esValida = true;
        if (usuario == null || usuario.isEmpty() ||
            contrasenia == null || contrasenia.isEmpty()) {
            lb_datosCompletosError.setText("Falta información. Debes introduir tu usuario y contraseña");
            esValida = false;
        }
        return esValida;
    }

    private void cargarEscena(String rutaMenu) {
        try {
            SistemaPizzeria.setRoot(rutaMenu, "Menu principal", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
