/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class UsuarioEmpleadoOperacionesController implements Initializable {


    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_calle;
    @FXML
    private TextField txt_numero;
    @FXML
    private TextField txt_codigoPostal;
    @FXML
    private Button btn_cerrar;
    @FXML
    private TextField txt_nombres;
    @FXML
    private TextField txt_apellidoPaterno;
    @FXML
    private TextField txt_apellidoMaterno;
    @FXML
    private TextField txt_correoElectronico;
    @FXML
    private TextField txt_ciudad;
    @FXML
    private PasswordField psw_contrasena;
    @FXML
    private ComboBox<?> cmb_tipoEmpleado;
    @FXML
    private Button btn_mostrarContrasena;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_guardar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
    }

    @FXML
    private void clicMostrarContrasena(ActionEvent event) {
    }

}
