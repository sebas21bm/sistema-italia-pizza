/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

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
    private DatePicker dp_fechaCaducidad;

    private Boolean registro;
    @FXML
    private Label txt_operaciones;
    @FXML
    private ImageView img_foto;

    String rutaFotoActual;
    ProductoInventarioDAO productoInventarioDAO = new ProductoInventarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.registro = (Boolean) SistemaPizzeria.getMetadatos("registrar-producto-inventario");

        if(registro){
            txt_operaciones.setText("Registrar Producto Inventario");
        }else{
            txt_operaciones.setText("Editar Producto Inventario");
            txt_codigo.setDisable(true);
            txt_nombre.setDisable(true);
        }
    }

    public void mostrarProductoInventario(ProductoInventarioDTO productoInventario){
        txt_codigo.setText(productoInventario.getCodigo());
        txt_nombre.setText(productoInventario.getNombre());
        txt_existencias.setText("" + productoInventario.getExistencias());
        dp_fechaCaducidad.setValue(productoInventario.getFechaCaducidad());
        rutaFotoActual = productoInventario.getFoto();
        img_foto.setImage(cargarImagen(rutaFotoActual));
    }

    private Image cargarImagen(String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isBlank()) {
            return null;
        }

        try {
            if (rutaFoto.startsWith("/") || rutaFoto.startsWith("imagenes/")) {
                String ruta = rutaFoto.startsWith("/") ? rutaFoto : "/" + rutaFoto;
                return new Image(getClass().getResourceAsStream(ruta));
            }

            File archivo = new File(rutaFoto);
            if (archivo.exists()) {
                return new Image(archivo.toURI().toString());
            }

        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    private ProductoInventarioDTO recuperarDatos(){
        ProductoInventarioDTO productoInventario = new ProductoInventarioDTO();
        productoInventario.setCodigo(txt_codigo.getText());
        productoInventario.setNombre(txt_nombre.getText());
        productoInventario.setExistencias(Integer.parseInt(txt_existencias.getText()));
        productoInventario.setFechaCaducidad(dp_fechaCaducidad.getValue());
        productoInventario.setFoto(rutaFotoActual);
        return productoInventario;
    }

    private void registrarProductoInventario(){
        try {
            if (productoInventarioDAO.registrar(recuperarDatos())) {
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Se ha registrado el producto de inventario correctamente",
                        Alert.AlertType.INFORMATION);
            } else {
                UtilidadesFX.mostrarAlertaSimple("Falló registro",
                        "El registro del producto de inventario no pudo realizarse," +
                                "intente de nuevo",
                        Alert.AlertType.WARNING);
            }
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al registrar",
                e.getMessage(),
                Alert.AlertType.ERROR);
         }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos del registro",
                MSJ_ERROR_CARGA_DATOS,
                Alert.AlertType.ERROR);
        }
    }

    public void editarProductoInventario(){
        try {
            if (productoInventarioDAO.editar(recuperarDatos())) {
                UtilidadesFX.mostrarAlertaSimple("Edición exitosa",
                        "Se ha editado el producto de inventario correctamente",
                        Alert.AlertType.INFORMATION);
            } else {
                UtilidadesFX.mostrarAlertaSimple("Falló la edición",
                        "La edición del producto de inventario no pudo realizarse," +
                                "intente de nuevo",
                        Alert.AlertType.WARNING);
            }
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al editar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos de la edición",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicSubirFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) img_foto.getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if (archivoSeleccionado == null) {
            return;
        }

        rutaFotoActual = archivoSeleccionado.getAbsolutePath();
        Image imagen = new Image(archivoSeleccionado.toURI().toString());
        img_foto.setImage(imagen);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(registro){
            registrarProductoInventario();
            ((Stage)txt_existencias.getScene().getWindow()).close();
        }else{
            editarProductoInventario();
            ((Stage)txt_existencias.getScene().getWindow()).close();
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage)txt_codigo.getScene().getWindow()).close();
    }
}
