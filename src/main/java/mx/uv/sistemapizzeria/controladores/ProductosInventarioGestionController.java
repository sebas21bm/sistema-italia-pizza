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
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductosInventarioGestionController implements Initializable {


    @FXML
    private TableView<?> tbl_insumos;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_existencias;
    @FXML
    private TableColumn<?, ?> col_estatus;
    @FXML
    private AnchorPane pnl_menuLateral;
    @FXML
    private ImageView img_logo;
    @FXML
    private Accordion ac_menu;
    @FXML
    private TitledPane tp_administracion;
    @FXML
    private Button btn_menuUsuarios;
    @FXML
    private TitledPane tp_inventarios;
    @FXML
    private Button btn_menuProductos;
    @FXML
    private Button btn_menuInsumos;
    @FXML
    private Button btn_menuValidacionInventarios;
    @FXML
    private TitledPane tp_pedidos;
    @FXML
    private Button btn_menuPedidos;
    @FXML
    private Button btn_cerrarSesion;
    @FXML
    private Button btn_ayudaAcercaDe;
    @FXML
    private AnchorPane pnl_contenido;
    @FXML
    private HBox hbox_busqueda;
    @FXML
    private TextField txt_buscar;
    @FXML
    private Button btn_buscar;
    @FXML
    private TableColumn<?, ?> col_fotografia;
    @FXML
    private TableColumn<?, ?> col_fechaCaducidad;
    @FXML
    private Button btn_nuevoInsumo;
    @FXML
    private Button btn_editar;
    @FXML
    private Button btn_eliminar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    

    @FXML
    private void clicNuevoInsumo(ActionEvent event) {
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
    private void clicValidacionInventarios(ActionEvent event) {
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
    }

    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
    }

}
