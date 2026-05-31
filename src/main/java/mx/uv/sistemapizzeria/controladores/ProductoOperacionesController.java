package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.excepciones.ProductoExistenteException;
import mx.uv.sistemapizzeria.modelo.dao.ProductoCompuestoPorDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;
import javafx.scene.control.Alert;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import mx.uv.sistemapizzeria.SistemaPizzeria;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

/**
 * FXML Controller class
 */
public class ProductoOperacionesController implements Initializable {

    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_descripcion;
    @FXML
    private TextField txt_precio;
    @FXML
    private ComboBox<ProductoInventarioDTO> cb_insumo;
    @FXML
    private TextField txt_cantidadInsumo;
    @FXML
    private TextField txt_limite;
    @FXML
    private RadioButton rb_requiereRecetaSi;
    @FXML
    private ToggleGroup tg_requiereReceta;
    @FXML
    private RadioButton rb_requiereRecetaNo;
    @FXML
    private AnchorPane pnl_receta;
    @FXML
    private Button btn_agregarInsumo;
    @FXML
    private TableView<ProductoCompuestoPorDTO> tbl_insumos;
    @FXML
    private TableColumn col_insumo;
    @FXML
    private TableColumn col_cantidad;
    @FXML
    private AnchorPane pnl_foto;
    @FXML
    private ImageView img_foto;
    @FXML
    private Button btn_subirFoto;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_guardar;
    @FXML
    private AnchorPane pnl_sinReceta;

    // Variables agregadas por el equipo
    @FXML
    private Label txt_operacion;
    @FXML
    private TextField txt_existencias;

    private final ObservableList<ProductoCompuestoPorDTO> listaInsumosReceta = FXCollections.observableArrayList();
    private Boolean registro;
    @FXML
    private DatePicker dp_caducidad;
    @FXML
    private VBox vb_receta;

    String rutaFotoActual;

    private ProductoInventarioDAO productoInventarioDAO = new ProductoInventarioDAO();
    private ProductoCompuestoPorDAO productoCompuestoPorDAO = new ProductoCompuestoPorDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private boolean tieneReceta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.registro = (Boolean) SistemaPizzeria.getMetadatos("registrar-producto");

        if(registro){
            txt_operacion.setText("Registrar Producto");
        }else{
            txt_operacion.setText("Editar Producto");
            txt_codigo.setDisable(true);
            vb_receta.setVisible(false);
        }
        configurarTablaReceta();
        cargarProductosInventarioDisponibles();
    }

    private void configurarTablaReceta() {
        col_insumo.setCellValueFactory(new PropertyValueFactory<>("nombreProductoInventario"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        tbl_insumos.setItems(listaInsumosReceta);
    }

    private void cargarProductosInventarioDisponibles() {
        try {
            List<ProductoInventarioDTO> productosInventarioCB = productoInventarioDAO.mostrarTodos();
            ObservableList<ProductoInventarioDTO> productosInventario = FXCollections.observableArrayList(productosInventarioCB);
            cb_insumo.setItems(productosInventario);
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al obtener productos de inventario disponibles",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos del inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }

    public void mostrarProductoVenta(ProductoVentaDTO producto){
        txt_codigo.setText(producto.getCodigoMenu());
        txt_nombre.setText(producto.getNombre());

        if (producto.getDescripcion() != null) {
            txt_descripcion.setText(producto.getDescripcion());
        }

        txt_precio.setText(String.valueOf(producto.getPrecio()));
        txt_limite.setText(String.valueOf(producto.getLimite()));
        rutaFotoActual = producto.getFoto();
        img_foto.setImage(cargarImagen(rutaFotoActual));
        cargarReceta(producto);
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

    private void cargarReceta(ProductoVentaDTO producto){
        try {
            List<ProductoCompuestoPorDTO> recetaCompleta = productoCompuestoPorDAO.obtenerReceta(producto.getCodigoMenu());

            if (recetaCompleta.size() > 1 || (recetaCompleta.size() == 1 && recetaCompleta.get(0).getCantidad() != 1.0)) {
                pnl_receta.setVisible(true);
                pnl_sinReceta.setVisible(false);
                listaInsumosReceta.setAll(recetaCompleta);
                tieneReceta = true;
            } else {
                pnl_receta.setVisible(false);
                pnl_sinReceta.setVisible(false);
                listaInsumosReceta.clear();
                tieneReceta = false;
            }
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al obtener receta",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos de la receta",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void clicAgregarInsumo(ActionEvent event) {
        ProductoInventarioDTO insumoSeleccionado = cb_insumo.getSelectionModel().getSelectedItem();
        String cantidadTexto = txt_cantidadInsumo.getText().trim();

        if (insumoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Dato requerido",
                    "Por favor, seleccione un insumo de la lista.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (cantidadTexto.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Dato requerido",
                    "Por favor, introduzca la cantidad del insumo.",
                    Alert.AlertType.WARNING);
            return;
        }

        double cantidad;

        try {
            cantidad = Double.parseDouble(cantidadTexto);

            if (cantidad <= 0) {
                UtilidadesFX.mostrarAlertaSimple("Formato inválido",
                        "La cantidad del insumo debe ser estrictamente mayor a cero.",
                        Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato inválido",
                    "Por favor, ingrese un valor numérico válido para la cantidad.",
                    Alert.AlertType.WARNING);
            return;
        }

        for (ProductoCompuestoPorDTO itemExistente : listaInsumosReceta) {
            if (itemExistente.getCodigoInsumo() != null
                    && itemExistente.getCodigoInsumo().equals(insumoSeleccionado.getCodigo())) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Insumo repetido",
                        "Este insumo ya está integrado en la receta actual",
                        Alert.AlertType.WARNING
                );
                return;
            }
        }

        ProductoCompuestoPorDTO ingredienteReceta = new ProductoCompuestoPorDTO();
        ingredienteReceta.setCodigoInsumo(insumoSeleccionado.getCodigo());
        ingredienteReceta.setNombreProductoInventario(insumoSeleccionado.getNombre());
        ingredienteReceta.setCantidad(cantidad);


        listaInsumosReceta.add(ingredienteReceta);

        cb_insumo.getSelectionModel().clearSelection();
        txt_cantidadInsumo.clear();
    }

    private ProductoVentaDTO recuperarDatosProducto(){
        ProductoVentaDTO producto = new ProductoVentaDTO();

        producto.setCodigoMenu(txt_codigo.getText().trim().toUpperCase());
        producto.setNombre(txt_nombre.getText().trim());
        producto.setDescripcion(txt_descripcion.getText().trim());
        producto.setPrecio(Double.parseDouble(txt_precio.getText().trim()));
        producto.setLimite(Integer.parseInt(txt_limite.getText().trim()));
        producto.setFoto(rutaFotoActual);

        return producto;
    }

    private List<ProductoCompuestoPorDTO> recuperarDatosReceta(){
        return List.copyOf(tbl_insumos.getItems());
    }


    public boolean editarProductoConReceta() {

        try {
            if (productoDAO.editarConReceta(recuperarDatosProducto(), recuperarDatosReceta())) {
                UtilidadesFX.mostrarAlertaSimple("Edición exitosa",
                        "Se ha editado el producto correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
            }else{
                UtilidadesFX.mostrarAlertaSimple("Falló la edición",
                        "La edición del producto no pudo realizarse, intente de nuevo",
                        Alert.AlertType.WARNING);
                return false;
            }
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al editar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos de la edición",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        return false;
    }

    public boolean editarProductoSinReceta() {
        try {
            if (productoDAO.editarSinReceta(recuperarDatosProducto())) {
                UtilidadesFX.mostrarAlertaSimple("Edición exitosa",
                        "Se ha editado el producto correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
            }else{
                UtilidadesFX.mostrarAlertaSimple("Falló la edición",
                        "La edición del producto no pudo realizarse, intente de nuevo",
                        Alert.AlertType.WARNING);
                return false;
            }
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al editar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos de la edición",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        return false;
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage)txt_cantidadInsumo.getScene().getWindow()).close();
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (!datosSonValidos()) {
            return;
        }

        boolean guardado = false;
        if (registro) {
            try {
                if (rb_requiereRecetaSi.isSelected()) {
                    guardado = guardarConReceta();
                } else {
                    guardado = guardarSinReceta();
                }
            } catch (ProductoExistenteException e) {
                UtilidadesFX.mostrarAlertaSimple("Código repetido",
                        e.getMessage(),
                        Alert.AlertType.WARNING);
            }
        } else {
            boolean confirmado = UtilidadesFX.mostrarAlertaConfirmacion(
                    "Confirmar eliminación",
                    "¿Estás seguro de que deseas modificar este producto?",
                    "El producto se actualizará con los datos que editaste");


            if (!confirmado) {
                return;
            }

            if (tieneReceta) {
                guardado = editarProductoConReceta();
            } else {
                guardado = editarProductoSinReceta();
            }
        }

        if (guardado) {
            ((Stage) txt_existencias.getScene().getWindow()).close();
        }
    }

    private boolean datosSonValidos() {
        String codigo = txt_codigo.getText().trim().toUpperCase();
        if (!codigo.matches("^P[0-9]{4}$")) {
            UtilidadesFX.mostrarAlertaSimple("Formato de código", "El código debe iniciar con 'P' seguido de 4 números exactos (Ej. P0001).", Alert.AlertType.WARNING);
            return false;
        }

        if (txt_nombre.getText().trim().isEmpty() || txt_precio.getText().trim().isEmpty() || txt_limite.getText().trim().isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Por favor, llene el Nombre, el Precio y el Límite del producto.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double precio = Double.parseDouble(txt_precio.getText().trim());
            int limite = Integer.parseInt(txt_limite.getText().trim());
            if (precio <= 0 || limite <= 0) {
                UtilidadesFX.mostrarAlertaSimple("Dato inválido", "El precio y el límite de venta deben ser estrictamente mayores a cero.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "El precio y el límite deben ser numéricos.", Alert.AlertType.WARNING);
            return false;
        }

        if (rb_requiereRecetaSi.isSelected()) {
            if (listaInsumosReceta.isEmpty() && tieneReceta) {
                UtilidadesFX.mostrarAlertaSimple("Receta vacía", "Debe agregar al menos un insumo a la receta.", Alert.AlertType.WARNING);
                return false;
            }
        } else {
            if (txt_existencias.getText().trim().isEmpty() || dp_caducidad.getValue() == null) {
                UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Indique las existencias y la fecha de caducidad.", Alert.AlertType.WARNING);
                return false;
            }
            try {
                if (Integer.parseInt(txt_existencias.getText().trim()) < 0) {
                    UtilidadesFX.mostrarAlertaSimple("Dato inválido", "Las existencias no pueden ser negativas.", Alert.AlertType.WARNING);
                    return false;
                }
            } catch (NumberFormatException e) {
                UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "Las existencias deben ser un número entero.", Alert.AlertType.WARNING);
                return false;
            }
        }
        return true;
    }



    private boolean guardarConReceta() throws ProductoExistenteException {
        ProductoVentaDTO producto = recuperarDatosProducto();

        try {
            if (productoDAO.buscar(producto.getCodigoMenu()) != null) {
                throw new ProductoExistenteException("Ya existe un producto con este código.");
            }

            if (productoDAO.registrarConReceta(producto, recuperarDatosReceta())) {
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Se ha registrado el producto correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
            }else {
                UtilidadesFX.mostrarAlertaSimple("Falló registro",
                        "El producto no pudo registrarse, intente de nuevo.",
                        Alert.AlertType.WARNING);
                return false;
            }
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al registrar producto",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error para el registro de producto",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        return false;
    }

    private boolean guardarSinReceta() throws ProductoExistenteException {
        ProductoVentaDTO producto = recuperarDatosProducto();

        int existencias = Integer.parseInt(txt_existencias.getText().trim());
        LocalDate caducidad = dp_caducidad.getValue();

        try {
            if (productoDAO.buscar(producto.getCodigoMenu()) != null) {
                throw new ProductoExistenteException("Ya existe un producto con este código.");
            }

            if (productoDAO.registrarSinReceta(producto, existencias, caducidad)) {
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Se ha registrado el producto correctamente",
                        Alert.AlertType.INFORMATION);
                return true;
            }

            UtilidadesFX.mostrarAlertaSimple("Falló registro",
                    "El producto no pudo registrarse, intente de nuevo.",
                    Alert.AlertType.WARNING);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al registrar producto",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error para el registro de producto",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        return false;
    }

    @FXML
    private void clicRequiereRecetaSi(ActionEvent event) {
        pnl_receta.setVisible(true);
        pnl_sinReceta.setVisible(false);
        txt_existencias.clear();
        dp_caducidad.setValue(null);
    }

    @FXML
    private void clicRequiereRecetaNo(ActionEvent event) {
        listaInsumosReceta.clear();
        cb_insumo.getSelectionModel().clearSelection();

        if (txt_cantidadInsumo != null) {
            txt_cantidadInsumo.clear();
        }

        pnl_receta.setVisible(false);
        pnl_sinReceta.setVisible(true);
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
        img_foto.setImage(new Image(archivoSeleccionado.toURI().toString()));
    }
}
