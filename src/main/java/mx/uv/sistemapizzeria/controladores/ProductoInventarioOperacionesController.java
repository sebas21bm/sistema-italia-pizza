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
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;

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
    private Button btn_borrarFoto;
    @FXML
    private DatePicker dp_fechaCaducidad;

    private Boolean registro;
    @FXML
    private Label txt_operaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.registro = (Boolean) SistemaPizzeria.getMetadatos("registrar-producto-inventario");

        if(registro){
            txt_operaciones.setText("Registrar Producto Inventario");
        }else{
            txt_operaciones.setText("Editar Producto Inventario");
            txt_codigo.setDisable(true);
        }
    }

    public void editarProductoInventario(ProductoInventarioDTO productoInventario){
        txt_codigo.setText(productoInventario.getCodigo());
    }

    public void registrarProductoInventario(){

    }

    @FXML
    private void clicSubirFoto(ActionEvent event) {
        //TODO
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
    }


    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage)txt_codigo.getScene().getWindow()).close();
    }
}
