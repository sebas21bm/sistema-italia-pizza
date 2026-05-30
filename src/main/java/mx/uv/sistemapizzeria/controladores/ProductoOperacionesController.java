package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
import javafx.scene.control.ButtonType;
import java.util.Optional;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;

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
    private TableColumn<ProductoCompuestoPorDTO, String> col_insumo;
    @FXML
    private TableColumn<ProductoCompuestoPorDTO, Double> col_cantidad;
    @FXML
    private AnchorPane pnl_foto;
    @FXML
    private ImageView img_foto;
    @FXML
    private Button btn_subirFoto;
    @FXML
    private Button btn_borrarFoto;
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

    private ObservableList<ProductoCompuestoPorDTO> listaInsumosReceta = FXCollections.observableArrayList();
    private File archivoFotoSeleccionado = null;
    private Boolean registro;
    @FXML
    private DatePicker dp_caducidad;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.registro = (Boolean) SistemaPizzeria.getMetadatos("registrar-producto");

        if(registro){
            txt_operacion.setText("Registrar Producto Inventario");
        }else{
            txt_operacion.setText("Editar Producto Inventario");
            txt_codigo.setDisable(true);
        }
        
        configurarReceta();
        configurarTablaReceta();
        cargarInsumosDisponibles();
    }

    private void configurarReceta(){
        tg_requiereReceta.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (tg_requiereReceta.getSelectedToggle() != null) {
                RadioButton conReceta = (RadioButton) tg_requiereReceta.getSelectedToggle();
                if(conReceta == rb_requiereRecetaSi){
                    pnl_receta.setVisible(true);
                    pnl_sinReceta.setVisible(false);
                }else{
                    pnl_receta.setVisible(false);
                    pnl_sinReceta.setVisible(true);
                }
            }
        });
    }

    private void configurarTablaReceta() {
        tbl_insumos.setItems(listaInsumosReceta);

        col_insumo.setCellValueFactory(cellData -> {
            ProductoCompuestoPorDTO fila = cellData.getValue();
            if (fila != null && fila.getInsumo() != null) {
                return new javafx.beans.property.SimpleStringProperty(fila.getInsumo().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        col_cantidad.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantidad"));
    }

    private void cargarInsumosDisponibles() {
        try {
            ProductoInventarioDAO insumoDAO = new ProductoInventarioDAO();
            List<ProductoInventarioDTO> insumos = insumoDAO.mostrarTodos();
            ObservableList<ProductoInventarioDTO> listaObservable = FXCollections.observableArrayList(insumos);
            cb_insumo.setItems(listaObservable);

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error de Conexión", "No se pudieron cargar los insumos desde la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Método agregado por el equipo para recibir el producto a editar
    public void editarProductoInventario(ProductoVentaDTO producto){
        txt_codigo.setText(producto.getCodigoMenu());
    }

    @FXML
    private void clicAgregarInsumo(ActionEvent event) {
        ProductoInventarioDTO insumoSeleccionado = cb_insumo.getSelectionModel().getSelectedItem();
        String cantidadTexto = txt_cantidadInsumo.getText().trim();

        if (insumoSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Dato requerido", "Por favor, seleccione un insumo de la lista.", Alert.AlertType.WARNING);
            return;
        }

        if (cantidadTexto.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Dato requerido", "Por favor, introduzca la cantidad del insumo.", Alert.AlertType.WARNING);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTexto);
            if (cantidad <= 0) {
                UtilidadesFX.mostrarAlertaSimple("Formato inválido", "La cantidad del insumo debe ser estrictamente mayor a cero.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato inválido", "Por favor, ingrese un valor numérico válido para la cantidad.", Alert.AlertType.WARNING);
            return;
        }

        for (ProductoCompuestoPorDTO itemExistente : listaInsumosReceta) {
            if (itemExistente.getCodigoInsumo() != null && itemExistente.getCodigoInsumo().equals(insumoSeleccionado.getCodigo())) {
                UtilidadesFX.mostrarAlertaSimple(
                        "Insumo repetido",
                        "Este insumo ya está integrado en la receta actual. Si requiere cambiar su dosificación, elimine el renglón y vuelva a agregarlo.",
                        Alert.AlertType.WARNING
                );
                return;
            }
        }

        ProductoCompuestoPorDTO ingredienteReceta = new ProductoCompuestoPorDTO();
        ingredienteReceta.setInsumo(insumoSeleccionado);
        ingredienteReceta.setCantidad(cantidad);

        listaInsumosReceta.add(ingredienteReceta);

        cb_insumo.getSelectionModel().clearSelection();
        txt_cantidadInsumo.clear();
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Stage escenario = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenario.close();
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String codigo = txt_codigo.getText().trim().toUpperCase();
        String nombre = txt_nombre.getText().trim();
        String descripcion = txt_descripcion.getText().trim();
        String precioTxt = txt_precio.getText().trim();
        String limiteTxt = txt_limite.getText().trim();

        // Validación de formato del código del producto (P0000) P + cuatro números
        if (!codigo.matches("^P[0-9]{4}$")) {
            UtilidadesFX.mostrarAlertaSimple("Formato de código", "El código debe iniciar con 'P' seguido de 4 números exactos (Ej. P0001, P0120).", Alert.AlertType.WARNING);
            return;
        }

        if (nombre.isEmpty() || precioTxt.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Por favor, ingrese el Nombre y el Precio del producto.", Alert.AlertType.WARNING);
            return;
        }

        double precio = 0;
        int limite = 0;

        try {
            precio = Double.parseDouble(precioTxt);
            if (precio <= 0) {
                UtilidadesFX.mostrarAlertaSimple("Dato inválido", "El precio debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }
            if (!limiteTxt.isEmpty()) {
                limite = Integer.parseInt(limiteTxt);
                if (limite <= 0) {
                    UtilidadesFX.mostrarAlertaSimple("Dato inválido", "El límite debe ser mayor a 0.", Alert.AlertType.WARNING);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "El precio y el límite deben ser valores numéricos.", Alert.AlertType.WARNING);
            return;
        }

        boolean requiereReceta = rb_requiereRecetaSi.isSelected();
        int existencias = 0;
        LocalDate fechaCaducidad = null;

        // Validaciones específicas según el tipo de producto (con o sin receta)
        if (requiereReceta) {
            if (listaInsumosReceta.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Receta vacía", "Debe agregar al menos un insumo a la receta.", Alert.AlertType.WARNING);
                return;
            }
        } else {
            String existenciasTxt = txt_existencias.getText().trim();
            if (existenciasTxt.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Por favor, indique las existencias iniciales del producto.", Alert.AlertType.WARNING);
                return;
            }
            try {
                existencias = Integer.parseInt(existenciasTxt);
                if (existencias < 0) {
                    UtilidadesFX.mostrarAlertaSimple("Dato inválido", "Las existencias no pueden ser negativas.", Alert.AlertType.WARNING);
                    return;
                }
            } catch (NumberFormatException e) {
                UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "Las existencias deben ser un número entero.", Alert.AlertType.WARNING);
                return;
            }

            fechaCaducidad = dp_caducidad.getValue();
            if (fechaCaducidad == null) {
                UtilidadesFX.mostrarAlertaSimple("Dato requerido", "Por favor, indique la fecha de caducidad del producto.", Alert.AlertType.WARNING);
                return;
            }
        }

        try {
            // Información del producto
            ProductoVentaDTO productoVenta = new ProductoVentaDTO();
            productoVenta.setCodigoMenu(codigo);
            productoVenta.setNombre(nombre);
            productoVenta.setDescripcion(descripcion);
            productoVenta.setPrecio(precio);
            productoVenta.setLimite(limite);

            // Gestión de la fotografía
            String nombreFoto = "";
            if (archivoFotoSeleccionado != null) {
                nombreFoto = archivoFotoSeleccionado.getName();
                String rutaProyecto = System.getProperty("user.dir");
                Path destinoVenta = Paths.get(rutaProyecto, "source", "imagenes", "productos", nombreFoto);
                Files.createDirectories(destinoVenta.getParent());
                Files.copy(archivoFotoSeleccionado.toPath(), destinoVenta, StandardCopyOption.REPLACE_EXISTING);

                // Si no tiene receta, también se copia a inventario
                if (!requiereReceta) {
                    Path destinoInv = Paths.get(rutaProyecto, "source", "imagenes", "productosInventario", nombreFoto);
                    Files.createDirectories(destinoInv.getParent());
                    Files.copy(archivoFotoSeleccionado.toPath(), destinoInv, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            productoVenta.setFoto(nombreFoto);

            // Lógica del DAO
            ProductoDAO daoVenta = new ProductoDAO();
            if (requiereReceta) {
                daoVenta.registrarConReceta(productoVenta, listaInsumosReceta);
            } else {
                daoVenta.registrarSinReceta(productoVenta, existencias, fechaCaducidad);
            }

            UtilidadesFX.mostrarAlertaSimple("Éxito", "El producto ha sido registrado correctamente.", Alert.AlertType.INFORMATION);
            clicCancelar(event);

        } catch (SQLException ex) {
            // Este bloque atrapa los mensajes personalizados que envía la base de datos
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Operación rechazada", ex.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error Inesperado", "Ha ocurrido un fallo en el sistema al procesar el registro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicRequiereRecetaSi(ActionEvent event) {
    }

    @FXML
    private void clicRequiereRecetaNo(ActionEvent event) {
    }

    @FXML
    private void clicSubirFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Producto");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            archivoFotoSeleccionado = archivoSeleccionado;
            Image imagen = new Image(archivoSeleccionado.toURI().toString());
            img_foto.setImage(imagen);
        }
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
        archivoFotoSeleccionado = null; // Limpiar la memoria del archivo
        img_foto.setImage(null);       // Quitar la foto de la pantalla
    }
}
