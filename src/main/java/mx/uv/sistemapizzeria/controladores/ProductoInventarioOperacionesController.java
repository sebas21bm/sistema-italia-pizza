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
import mx.uv.sistemapizzeria.utilidades.Validador;

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
    private Button btn_borrarFoto;
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
        productoInventario.setCodigo(txt_codigo.getText().trim().toUpperCase());
        productoInventario.setNombre(txt_nombre.getText().trim());
        productoInventario.setExistencias(Integer.parseInt(txt_existencias.getText().trim()));
        productoInventario.setFechaCaducidad(dp_fechaCaducidad.getValue());
        productoInventario.setFoto(rutaFotoActual);
        return productoInventario;
    }

    private boolean registrarProductoInventario(){
        try {
            String codigoIngresado = txt_codigo.getText().trim().toUpperCase();
            if (productoInventarioDAO.buscar(codigoIngresado) != null) {
                UtilidadesFX.mostrarAlertaSimple("Código duplicado",
                        "Ya existe un producto en el inventario registrado con el código " + codigoIngresado + ".",
                        Alert.AlertType.WARNING);
                return false;
            }
            if (productoInventarioDAO.registrar(recuperarDatos())) {
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Se ha registrado el producto de inventario correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
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
        return false;
    }

    public boolean editarProductoInventario(){
        try {
            if (productoInventarioDAO.editar(recuperarDatos())) {
                UtilidadesFX.mostrarAlertaSimple("Edición exitosa",
                        "Se ha editado el producto de inventario correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
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
        return false;
    }

    private boolean datosSonValidos() {
        String codigo = txt_codigo.getText().trim();
        String nombre = txt_nombre.getText().trim();
        String existenciasTxt = txt_existencias.getText().trim();

        // Impedir guardar cuando los campos están vacios
        if (codigo.isEmpty() || nombre.isEmpty() || existenciasTxt.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos vacíos", "Por favor, complete todos los campos obligatorios.", Alert.AlertType.WARNING);
            return false;
        }

        // Manejo de letras en el campo de existencias
        int existencias;
        try {
            existencias = Integer.parseInt(existenciasTxt);
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "El campo de existencias debe contener un número entero válido.", Alert.AlertType.WARNING);
            return false;
        }

        // En caso de que no se haya elegido una fecha mostrar advertencia
        if (dp_fechaCaducidad.getValue() == null) {
            UtilidadesFX.mostrarAlertaSimple("Dato requerido", "Por favor, seleccione la fecha de caducidad del producto.", Alert.AlertType.WARNING);
            return false;
        }

        // Cumplimiento de las reglas de la base de datos (Ejemplo: Código = I0000, existencias >= 0)
        ProductoInventarioDTO tempInsumo = new ProductoInventarioDTO();
        tempInsumo.setCodigo(codigo);
        tempInsumo.setNombre(nombre);
        tempInsumo.setExistencias(existencias);

        List<String> errores = Validador.validarProductoInsumo(tempInsumo);

        if (!errores.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Datos inválidos", Validador.formatearErrores(errores), Alert.AlertType.WARNING);
            return false; // Frena la ejecución
        }

        return true; // Todo salío bien
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
        if (!datosSonValidos()) return;

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
