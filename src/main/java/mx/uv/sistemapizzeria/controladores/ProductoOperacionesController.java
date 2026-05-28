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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductoOperacionesController implements Initializable {


    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_descripcion;
    @FXML
    private TextField txt_precio;
    @FXML
    private ComboBox<?> cb_insumo;
    @FXML
    private TextField txt_cantidadInsumo;
    @FXML
    private TextField txt_limite;
    @FXML
    private RadioButton rb_requiereRecetaSi;
    @FXML
    private ToggleGroup tg_requiereReceta;
    @FXML
    private RadioButton rb_requiereRecetaNo;
    @FXML
    private AnchorPane pnl_receta;
    @FXML
    private Button btn_agregarInsumo;
    @FXML
    private TableView<?> tbl_insumos;
    @FXML
    private TableColumn<?, ?> col_insumo;
    @FXML
    private TableColumn<?, ?> col_cantidad;
    @FXML
    private AnchorPane pnl_foto;
    @FXML
    private ImageView img_foto;
    @FXML
    private Button btn_subirFoto;
    @FXML
    private Button btn_borrarFoto;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_guardar;
    @FXML
    private AnchorPane pnl_receta1;
    @FXML
    private TextField txt_cantidadInsumo1;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    

    @FXML
    private void clicAgregarInsumo(ActionEvent event) {
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

    @FXML
    private void clicRequiereRecetaSi(ActionEvent event) {
    }

    @FXML
    private void clicRequiereRecetaNo(ActionEvent event) {
    }

    @FXML
    private void clicSubirFoto(ActionEvent event) {
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
    }

}
