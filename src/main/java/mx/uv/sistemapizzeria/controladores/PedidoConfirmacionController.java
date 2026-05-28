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
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class PedidoConfirmacionController implements Initializable {

    @FXML
    private Label lblSubtitulo;
    @FXML
    private Label lblFechaHora;
    @FXML
    private Label lblAtiende;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblTelefono;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnAtras;
    @FXML
    private Button btnConfirmar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clicAtras(ActionEvent event) {
    }

    @FXML
    private void clicConfirmar(ActionEvent event) {
    }
    
}
