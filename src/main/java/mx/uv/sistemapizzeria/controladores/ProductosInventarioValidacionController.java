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
public class ProductosInventarioValidacionController implements Initializable {

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
    private Button btn_generarReportePdf;
    @FXML
    private TableView<?> tbl_validacionInsumos;
    @FXML
    private TableColumn<?, ?> col_codigo;
    @FXML
    private TableColumn<?, ?> col_insumo;
    @FXML
    private TableColumn<?, ?> col_existencias;
    @FXML
    private TableColumn<?, ?> col_conteoFisicoReal;
    @FXML
    private TableColumn<?, ?> col_diferencia;
    @FXML
    private Button btn_guardarValidacion;

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
    private void clicGenerarReportePdf(ActionEvent event) {
    }

    @FXML
    private void clicGuardarValidacion(ActionEvent event) {
    }
    
}
