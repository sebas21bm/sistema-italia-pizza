package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

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
        SistemaPizzeria.setMetadatos("registrar-producto",true);
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoOperaciones");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto");
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
    private void clicEditar(ActionEvent event) {
        SistemaPizzeria.setMetadatos("registrar-producto",false);
        ProductoVentaDTO producto = tbl_productos.getSelectionModel().getSelectedItem();
        if(producto == null){
            UtilidadesFX.mostrarAlertaSimple("Sin Producto Inventario para editar",
                    "No se ha seleccionado ningún producto de inventario, " +
                            "selecciona uno para continuar",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoOperaciones");
            Parent vista = loader.load();
            ProductoOperacionesController controller = loader.getController();
            controller.editarProductoInventario(producto);
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto");
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

        // Obtener el producto seleccionado de la tabla
        ProductoVentaDTO productoSeleccionado = tbl_productos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida", "Por favor, seleccione antes un producto de la tabla para poder eliminarlo", Alert.AlertType.WARNING);
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar eliminación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que deseas eliminar: " + productoSeleccionado.getNombre() + "?");
        alertaConfirmacion.setContentText("El producto será dado de baja del sistema.");

        // Mostrar ventana emergente de confirmación
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();

        // Si el usuario acepto eliminar el producto entonces sucede la eliminación
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                ProductoDAO producto = new ProductoDAO();
                producto.eliminarProductoVenta(productoSeleccionado.getCodigoMenu());

                UtilidadesFX.mostrarAlertaSimple("Éxito", "El producto fue eliminado correctamente", Alert.AlertType.CONFIRMATION);

                // Refrescar la tabla se simula con este método:
                cargarDatosTabla();
            } catch (SQLException ex) {
                ex.printStackTrace();
                UtilidadesFX.mostrarAlertaSimple("No se puede eliminar", ex.getMessage(), Alert.AlertType.WARNING);
            } catch (Exception ex) {
                ex.printStackTrace();
                UtilidadesFX.mostrarAlertaSimple("Error Inesperado", "Ha ocurrido un fallo en el sistema al intentar eliminar el producto.", Alert.AlertType.ERROR);
            }
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