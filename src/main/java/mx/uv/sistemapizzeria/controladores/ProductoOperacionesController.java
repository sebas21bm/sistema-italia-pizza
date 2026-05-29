package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
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
import mx.uv.sistemapizzeria.modelo.dao.ProductoInsumoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInsumoDTO;
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
    private ComboBox<ProductoInsumoDTO> cb_insumo;
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
    private TextField txt_cantidadInsumo1;
    @FXML
    private AnchorPane pnl_sinReceta;
    
    // Variables agregadas por el equipo
    @FXML
    private Label txt_operacion;
    @FXML
    private TextField txt_existencias;

    private ObservableList<ProductoCompuestoPorDTO> listaInsumosReceta = FXCollections.observableArrayList();
    private String rutaFotoLocal = "";
    private Boolean registro;

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
            ProductoInsumoDAO insumoDAO = new ProductoInsumoDAO();
            List<ProductoInsumoDTO> insumos = insumoDAO.mostrarTodos();
            ObservableList<ProductoInsumoDTO> listaObservable = FXCollections.observableArrayList(insumos);
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
        ProductoInsumoDTO insumoSeleccionado = cb_insumo.getSelectionModel().getSelectedItem();
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
        // Se ha dejado en blanco intencionalmente para implementar los Procedimientos Almacenados
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
            rutaFotoLocal = archivoSeleccionado.getAbsolutePath();

            Image imagen = new Image(archivoSeleccionado.toURI().toString());
            img_foto.setImage(imagen);
        }
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
    }
}