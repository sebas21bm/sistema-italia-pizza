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
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class UsuarioClienteOperacionesController implements Initializable {

    @FXML
    private TextField txt_nombres;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_correoElectronico;
    @FXML
    private TextField txt_calle;
    @FXML
    private TextField txt_numero;
    @FXML
    private TextField txt_codigoPostal;
    @FXML
    private TextField txt_ciudad;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_cerrar;
    @FXML
    private TextField txt_apellidoPaterno;
    @FXML
    private TextField txt_apellidoMaterno;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_nombres.setDisable(false);
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
    
}
