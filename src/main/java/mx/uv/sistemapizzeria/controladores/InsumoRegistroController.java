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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class InsumoRegistroController implements Initializable {


    @FXML
    private Label lbl_tituloFormulario;
    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_existencias;
    @FXML
    private TextField txt_fechaCaducidad;
    @FXML
    private StackPane spn_foto;
    @FXML
    private ImageView img_foto;
    @FXML
    private Rectangle rec_placeholderFoto;
    @FXML
    private Line lin_placeholderFoto1;
    @FXML
    private Line lin_placeholderFoto2;
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
    private void clicBorrar(ActionEvent event) {
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

}
