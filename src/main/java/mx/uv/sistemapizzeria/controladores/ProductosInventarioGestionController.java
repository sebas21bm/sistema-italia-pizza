/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductosInventarioGestionController implements Initializable {


    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_existencias;
    @FXML
    private TableColumn<?, ?> col_estatus;
    @FXML
    private AnchorPane pnl_menuLateral;
    @FXML
    private TitledPane tp_administracion;
    @FXML
    private TitledPane tp_inventarios;
    @FXML
    private TitledPane tp_pedidos;
    @FXML
    private AnchorPane pnl_contenido;
    @FXML
    private HBox hbox_busqueda;
    @FXML
    private TextField txt_buscar;
    @FXML
    private TableColumn<?, ?> col_fotografia;
    @FXML
    private TableColumn<?, ?> col_fechaCaducidad;
    @FXML
    private TableView<ProductoInventarioDTO> tbl_productoInventario;
    @FXML
    private ComboBox<String> cb_filtro;

    private ObservableList<String> filtros = FXCollections.observableArrayList(
            "Por código", "Por nombre");
    private String filtroBusqueda;

    ProductoInventarioDAO productoInventarioDAO = new ProductoInventarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_filtro.setItems(filtros);
        configurarSeleccionFiltro();
    }

    private void configurarSeleccionFiltro(){
        cb_filtro.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null){
                    filtroBusqueda = newValue;
                }
            }
        });
    }



    @FXML
    private void clicNuevoProductoInventario(ActionEvent event) {
        SistemaPizzeria.setMetadatos("registrar-producto-inventario",true);
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoInventarioOperaciones");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto Inventario");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String campoBuscar = txt_buscar.getText();
        if(filtroBusqueda.equals("Por código")){
            productoInventarioDAO.buscarPorCodigo(campoBuscar);
        }else{
            productoInventarioDAO.buscarPorNombre(campoBuscar);
        }

    }

    @FXML
    private void clicEditar(ActionEvent event) {
        SistemaPizzeria.setMetadatos("registrar-producto-inventario",false);
        ProductoInventarioDTO productoInventario = tbl_productoInventario.getSelectionModel().getSelectedItem();
        if(productoInventario == null){
            UtilidadesFX.mostrarAlertaSimple("Sin Producto Inventario para editar",
                    "No se ha seleccionado ningún producto de inventario, " +
                            "selecciona uno para continuar",
                            Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoInventarioOperaciones");
            Parent vista = loader.load();
            ProductoInventarioOperacionesController controller = loader.getController();
            controller.editarProductoInventario(productoInventario);
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto Inventario");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        //TODO Implementación Eliminación
    }


    //NAVEGACION MENÚ
    @FXML
    private void clicUsuarios(ActionEvent event) {
        try{
            SistemaPizzeria.setRoot("UsuariosGestion","Usuarios");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion","Productos");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion","Productos de Inventario");
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    private void clicValidacionInventarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try {
            SistemaPizzeria.setRoot("InicioSesion","Sistema Pizzeria - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("AcercaDe");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("AcercaDe");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
