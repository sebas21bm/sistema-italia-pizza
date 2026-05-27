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

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class MenuAdministradorController implements Initializable {


    @FXML
    private Label lbl_nombreUsuario;
    @FXML
    private Label lbl_rolUsuario;
    @FXML
    private StackPane panelContenido;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void clicUsuarios(ActionEvent event) {
    }

    @FXML
    private void clicProductos(ActionEvent event) {
    }

    @FXML
    private void clicInsumos(ActionEvent event) {
    }

    @FXML
    private void clicValidacion(ActionEvent event) {
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
    }

    @FXML
    private void clicMenuCerrarSesion(ActionEvent event) {
    }

    @FXML
    private void clicMenuAyuda(ActionEvent event) {
    }

}
