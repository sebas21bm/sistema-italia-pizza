/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductosGestionController implements Initializable {


    @FXML
    private TableView<ProductoVentaDTO> tbl_productos;
    @FXML
    private TableColumn<ProductoVentaDTO, String> col_codigo;
    @FXML
    private TableColumn<ProductoVentaDTO, String> col_fotografia;
    @FXML
    private TableColumn<ProductoVentaDTO, String> col_nombre;
    @FXML
    private TableColumn<ProductoVentaDTO, Double> col_precio;
    @FXML
    private TableColumn<ProductoVentaDTO, Integer> col_limite;
    @FXML
    private TableColumn<ProductoVentaDTO, String> col_descripcion;
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
    private Button btn_menuProductosInventario;
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
    private Button btn_nuevoProducto;
    @FXML
    private Button btn_editar;
    @FXML
    private Button btn_eliminar;
    @FXML
    private Button btn_buscar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatosTabla();
    }
    private void configurarColumnas() {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigoMenu"));
        col_fotografia.setCellValueFactory(new PropertyValueFactory<>("foto"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        col_limite.setCellValueFactory(new PropertyValueFactory<>("limite"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    private void cargarDatosTabla() {
        try {
            ProductoDAO dao = new ProductoDAO();
            // Se utiliza el método que extrae los productos con estatus = 1
            List<ProductoVentaDTO> listaProductos = dao.mostrarTodos();

            // ObservableList para actualizar la vista automáticamente
            ObservableList<ProductoVentaDTO> productosObservables = FXCollections.observableArrayList(listaProductos);
            tbl_productos.setItems(productosObservables);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error al cargar los productos");
        }
    }

    @FXML
    private void clicNuevoProducto(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoOperaciones");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("ProductoOperaciones");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            SistemaPizzeria.setRoot("ProductoOperaciones", "Producto");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        ProductoVentaDTO producto = tbl_productos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            UtilidadesFX.mostrarAlertaSimple("Atención", "Selecciona un producto de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        try {
            ProductoDAO dao = new ProductoDAO();

            // 1. Validación de la regla de negocio
            if (dao.tienePedidos(producto.getCodigoMenu())) {
                UtilidadesFX.mostrarAlertaSimple("Operación denegada",
                        "El producto ya está en el historial de un pedido y no puede eliminarse.", Alert.AlertType.WARNING);
                return;
            }

            // 2. Confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Deseas eliminar el producto: " + producto.getNombre() + "?");

            // 3. Ejecución si el usuario acepta
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if (dao.eliminar(producto.getCodigoMenu())) {
                    UtilidadesFX.mostrarAlertaSimple("Éxito", "Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
                    cargarDatosTabla(); // Refresca la vista
                } else {
                    UtilidadesFX.mostrarAlertaSimple("Error", "No se pudo eliminar el producto de la base de datos.", Alert.AlertType.ERROR);
                }
            }

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace(); // Solo para los desarrolladores en consola
            UtilidadesFX.mostrarAlertaSimple("Error de BD", "Problema al conectar con la base de datos. Contacte a soporte.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error", "Ocurrió un error inesperado en el sistema.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
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
