/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.File;
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

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class ProductosInventarioGestionController implements Initializable {

    @FXML
    private
    TableColumn<ProductoInventarioDTO, String> col_nombre;
    @FXML
    private
    TableColumn<ProductoInventarioDTO, Integer> col_existencias;
    @FXML
    private
    TextField txt_buscar;
    @FXML
    private
    TableColumn<ProductoInventarioDTO,String> col_fotografia;
    @FXML
    private
    TableColumn<ProductoInventarioDTO, String> col_fechaCaducidad;
    @FXML
    private
    TableView<ProductoInventarioDTO> tbl_productoInventario;
    @FXML
    private ComboBox<String> cb_filtro;

    private ObservableList<String> filtros = FXCollections.observableArrayList(
            "Por código", "Por nombre", "Ver todos");
    private String filtroBusqueda;
    private ObservableList<ProductoInventarioDTO> productosInventario;
    ProductoInventarioDAO productoInventarioDAO = new ProductoInventarioDAO();

    @FXML
    private TableColumn<ProductoInventarioDTO, String> col_codigo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_filtro.setItems(filtros);
        configurarTabla();
        cargarInformacionProductosInventario();
        configurarSeleccionFiltro();
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
                        cargarInformacionProductosInventario();
                    }else {
                        filtroBusqueda = newValue;
                    }
                }
            }
        });
    }

    private void configurarTabla(){
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        col_fechaCaducidad.setCellValueFactory(new PropertyValueFactory<>("fechaCaducidad"));
        col_fotografia.setCellValueFactory(new PropertyValueFactory<>("foto"));

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

        if (!rutaFoto.contains("/")) {
            rutaFoto = "/imagenes/" + rutaFoto;
        }

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

    private void cargarInformacionProductosInventario(){
        try {
            productosInventario = FXCollections.observableArrayList();
            List<ProductoInventarioDTO> productosInventarioBD = productoInventarioDAO.mostrarTodos();
            productosInventario.addAll(productosInventarioBD);
            tbl_productoInventario.setItems(productosInventario);
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos del inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    private void actualizarInformacion(){
        if(filtroBusqueda.equals("")){
            cargarInformacionProductosInventario();
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
        productosInventario = FXCollections.observableArrayList();
        if(filtroBusqueda.equals("")) {
            UtilidadesFX.mostrarAlertaSimple("Sin filtro",
                    "Por favor selecciona un filtro para realizar la búsqueda",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            if (filtroBusqueda.equals("Por código")) {
                ProductoInventarioDTO productoInventario = productoInventarioDAO.buscar(campoBuscar);
                productosInventario.add(productoInventario);
            } else {
                List<ProductoInventarioDTO> productoInventarioBD = productoInventarioDAO.buscarPorNombre(campoBuscar);
                productosInventario.addAll(productoInventarioBD);
            }
            tbl_productoInventario.setItems(productosInventario);
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos del inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        ProductoInventarioDTO productoInventario = tbl_productoInventario.getSelectionModel().getSelectedItem();
        if(productoInventario == null){
            UtilidadesFX.mostrarAlertaSimple("Sin Producto Inventario para eliminar",
                    "No se ha seleccionado ningún producto de inventario, " +
                            "selecciona uno para continuar",
                    Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Estás seguro de que deseas eliminar: " + productoInventario.getNombre() + "?",
                "El producto de inventario será dado de baja del sistema.");


        if (!confirmado) {
            return;
        }

        try {
            if (productoInventarioDAO.eliminar(productoInventario.getCodigo())){
                UtilidadesFX.mostrarAlertaSimple("Eliminación exitosa",
                        "Se ha eliminado el producto de inventario correctamente",
                        Alert.AlertType.INFORMATION);
            } else {
                UtilidadesFX.mostrarAlertaSimple("Falló la edición",
                        "La eliminación del producto de inventario no pudo realizarse," +
                                "intente de nuevo",
                        Alert.AlertType.WARNING);
            }
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al eliminar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar producto inventario a eliminar",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        actualizarInformacion();
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
            actualizarInformacion();
        } catch (IOException e) {
            e.printStackTrace();
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
            controller.mostrarProductoInventario(productoInventario);
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Producto Inventario");
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
