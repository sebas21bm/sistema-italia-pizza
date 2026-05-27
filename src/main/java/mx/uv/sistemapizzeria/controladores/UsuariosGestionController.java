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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
/**
 * FXML Controller class
 *
 * @author macol
 */
public class UsuariosGestionController implements Initializable {


    @FXML
    private AnchorPane pnl_menuLateral;
    @FXML
    private ImageView img_logo;
    @FXML
    private Accordion ac_menu;
    @FXML
    private TitledPane tp_administracion;
    @FXML
    private Label lbl_menuUsuarios;
    @FXML
    private TitledPane tp_inventarios;
    @FXML
    private Label lbl_menuProductos;
    @FXML
    private Label lbl_menuInsumos;
    @FXML
    private Label lbl_menuValidacionInventarios;
    @FXML
    private TitledPane tp_pedidos;
    @FXML
    private Label lbl_menuPedidos;
    @FXML
    private Label lbl_cerrarSesion;
    @FXML
    private Label lbl_ayudaAcercaDe;
    @FXML
    private AnchorPane pnl_contenido;
    @FXML
    private HBox hbox_busqueda;
    @FXML
    private TextField txt_buscar;
    @FXML
    private TableView<?> tbl_usuarios;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_telefono;
    @FXML
    private TableColumn<?, ?> col_email;
    @FXML
    private TableColumn<?, ?> col_direccion;
    @FXML
    private TableColumn<?, ?> col_estatus;
    @FXML
    private TableColumn<?, ?> col_tipo;
    @FXML
    private TitledPane tp_filtroEstatus;
    @FXML
    private RadioButton rb_estatusActivo;
    @FXML
    private ToggleGroup tg_estatus;
    @FXML
    private RadioButton rb_estatusInactivo;
    @FXML
    private TitledPane tp_filtroTipo;
    @FXML
    private RadioButton rb_tipoEmpleado;
    @FXML
    private ToggleGroup tg_tipo;
    @FXML
    private RadioButton rb_tipoCliente;
    @FXML
    private Button btn_nuevoUsuario;
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
    private void clicNuevoUsuario(ActionEvent event) {
    }

    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
    }

}
