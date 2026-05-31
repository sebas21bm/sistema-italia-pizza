package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import jdk.jshell.execution.Util;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

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
    private TextField txt_buscar;
    @FXML
    private ComboBox<String> cb_filtro;

    private ObservableList<String> filtros = FXCollections.observableArrayList(
            "Por código", "Por nombre", "Ver todos");
    private String filtroBusqueda;
    private ObservableList<ProductoVentaDTO> productosVenta;
    ProductoDAO productoDAO = new ProductoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_filtro.setItems(filtros);
        configurarSeleccionFiltro();
        configurarTabla();
        cargarInformacionProductosVenta();
        filtroBusqueda = "";
    }

    private void configurarSeleccionFiltro(){
        cb_filtro.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null){
                    if(newValue.equals("Ver todos")){
                        txt_buscar.setText("");
                        filtroBusqueda = "";
                        cargarInformacionProductosVenta();
                    }else {
                        filtroBusqueda = newValue;
                    }
                }
            }
        });
    }

    private void configurarTabla() {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigoMenu"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        col_limite.setCellValueFactory(new PropertyValueFactory<>("limite"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        col_fotografia .setCellValueFactory(new PropertyValueFactory<>("foto"));

        col_fotografia.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                setGraphic(null);
                setText(null);
                imageView.setImage(null);

                if (empty || imagePath == null || imagePath.isBlank()) {
                    return;
                }

                try {
                    Image image = cargarImagen(imagePath);

                    if (image == null || image.isError()) {
                        return;
                    }

                    imageView.setImage(image);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);

                } catch (Exception e) {
                    setGraphic(null);
                    setText(null);
                    imageView.setImage(null);
                }
            }
        });
    }

    private Image cargarImagen(String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isBlank()) {
            return null;
        }

        rutaFoto = rutaFoto.trim().replace("\\", "/");

        try {
            if (rutaFoto.startsWith("/imagenes/") || rutaFoto.startsWith("imagenes/")) {
                String rutaRecurso = rutaFoto.startsWith("/") ? rutaFoto : "/" + rutaFoto;

                var recurso = getClass().getResourceAsStream(rutaRecurso);

                if (recurso == null) {
                    return null;
                }

                return new Image(recurso);
            }

            File archivo = new File(rutaFoto);

            if (archivo.exists()) {
                return new Image(archivo.toURI().toString());
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private void cargarInformacionProductosVenta(){
        try {
            productosVenta = FXCollections.observableArrayList();
            List<ProductoVentaDTO> productosVentaBD = productoDAO.mostrarTodos();
            productosVenta.addAll(productosVentaBD);
            tbl_productos.setItems(productosVenta);
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos para venta",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    private void actualizarInformacion(){
        if(filtroBusqueda.equals("")){
            cargarInformacionProductosVenta();
        }else {
            buscarPorFiltro();
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        buscarPorFiltro();
    }

    private void buscarPorFiltro(){
        String campoBuscar = txt_buscar.getText();
        productosVenta = FXCollections.observableArrayList();
        if(filtroBusqueda.equals("")) {
            UtilidadesFX.mostrarAlertaSimple("Sin filtro",
                    "Por favor selecciona un filtro para realizar la búsqueda",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            if (filtroBusqueda.equals("Por código")) {
                ProductoVentaDTO producto = productoDAO.buscar(campoBuscar);
                productosVenta.add(producto);
            } else {
                List<ProductoVentaDTO> productosBD = productoDAO.buscarPorNombre(campoBuscar);
                productosVenta.addAll(productosBD);
            }
            tbl_productos.setItems(productosVenta);
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos para venta",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        ProductoVentaDTO productoSeleccionado = tbl_productos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Sin Producto para eliminar",
                    "No se ha seleccionado ningún producto, " +
                            "selecciona uno para continuar",
                    Alert.AlertType.WARNING);
            return;
        }


        boolean confirmado = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Estás seguro de que deseas eliminar: " + productoSeleccionado.getNombre() + "?",
                "El producto será dado de baja del sistema.");


        if (!confirmado) {
            return;
        }

        try {
            if (productoDAO.eliminar(productoSeleccionado.getCodigoMenu())){
                UtilidadesFX.mostrarAlertaSimple("Eliminación exitosa",
                        "Se ha eliminado el producto correctamente",
                        Alert.AlertType.INFORMATION);
            } else {
                UtilidadesFX.mostrarAlertaSimple("Falló la eliminación",
                        "La eliminación del producto no pudo realizarse," +
                                "intente de nuevo",
                        Alert.AlertType.WARNING);
            }
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar producto a eliminar",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        actualizarInformacion();
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
            actualizarInformacion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        SistemaPizzeria.setMetadatos("registrar-producto",false);
        ProductoVentaDTO producto = tbl_productos.getSelectionModel().getSelectedItem();
        if(producto == null){
            UtilidadesFX.mostrarAlertaSimple("Sin Producto para editar",
                    "No se ha seleccionado ningún producto, " +
                            "selecciona uno para continuar",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("ProductoOperaciones");
            Parent vista = loader.load();
            ProductoOperacionesController controller = loader.getController();
            controller.mostrarProductoVenta(producto);
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            actualizarInformacion();
        } catch (IOException e) {
            e.printStackTrace();
        }
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