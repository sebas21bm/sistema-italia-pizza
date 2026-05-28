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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class PedidoEdicionController implements Initializable {

    @FXML
    private Button btn_cerrar;
    @FXML
    private TableView<?> tbl_cliente;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_numeroTelefono;
    @FXML
    private TableColumn<?, ?> col_calle;
    @FXML
    private TableColumn<?, ?> col_numeroCalle;
    @FXML
    private TableColumn<?, ?> col_codigoPostal;
    @FXML
    private TableColumn<?, ?> col_ciudad;
    @FXML
    private Button btn_disminuirUno;
    @FXML
    private Button btn_agregarUno;
    @FXML
    private Button btn_disminuirDos;
    @FXML
    private Button btn_agregarDos;
    @FXML
    private Button btn_disminuirTres;
    @FXML
    private Button btn_agregarTres;
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
    private void clicCerrar(ActionEvent event) {
    }

    @FXML
    private void clicDisminuirUno(ActionEvent event) {
    }

    @FXML
    private void clicAgregarUno(ActionEvent event) {
    }

    @FXML
    private void clicDisminuirDos(ActionEvent event) {
    }

    @FXML
    private void clicAgregarDos(ActionEvent event) {
    }

    @FXML
    private void clicDisminuirTres(ActionEvent event) {
    }

    @FXML
    private void clicAgregarTres(ActionEvent event) {
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }
    
}
