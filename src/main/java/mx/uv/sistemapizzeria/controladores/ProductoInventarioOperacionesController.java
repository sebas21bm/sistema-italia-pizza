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
import javafx.scene.control.DatePicker;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductoInventarioOperacionesController implements Initializable {


    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_existencias;
    @FXML
    private ImageView img_foto;
    @FXML
    private AnchorPane pnl_foto;
    @FXML
    private Button btn_subirFoto;
    @FXML
    private Button btn_borrarFoto;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_guardar;
    @FXML
    private DatePicker dp_fechaCaducidad;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void clicSubirFoto(ActionEvent event) {
    }


    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
    }

}
