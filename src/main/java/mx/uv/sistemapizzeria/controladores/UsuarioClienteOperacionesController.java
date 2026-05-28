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

    private TextField txt_nombres;
    @FXML
    private TextField txt_nombres1;
    @FXML
    private TextField txt_apellidoPaterno1;
    @FXML
    private TextField txt_apellidoMaterno1;
    @FXML
    private TextField txt_telefono1;
    @FXML
    private TextField txt_correoElectronico1;
    @FXML
    private TextField txt_calle1;
    @FXML
    private TextField txt_codigoPostal1;
    @FXML
    private TextField txt_numero1;
    @FXML
    private TextField txt_ciudad1;
    @FXML
    private Button btn_agregarDireccion1;
    @FXML
    private Button btn_cancelar1;
    @FXML
    private Button btn_guardar1;

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
    private void clicAgregarDireccion(ActionEvent event) {
    }
    
}
