/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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

/**
 * FXML Controller class
 *
 * @author macol
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

    private ObservableList<ProductoCompuestoPorDTO> listaInsumosReceta = FXCollections.observableArrayList();
    private String rutaFotoLocal = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
            // Suponiendo que tu DAO implementa mostrarTodos() de la interfaz Operaciones
            List<ProductoInsumoDTO> insumos = insumoDAO.mostrarTodos();

            // Convertimos la lista normal de Java a una ObservableList de JavaFX
            ObservableList<ProductoInsumoDTO> listaObservable = FXCollections.observableArrayList(insumos);

            // Llenamos el ComboBox
            cb_insumo.setItems(listaObservable);

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error de Conexión", "No se pudieron cargar los insumos desde la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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

        // Evitar duplicar el mismo insumo en la receta
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
        // 1. Recopilar datos básicos de la vista
        String codigo = txt_codigo.getText().trim();
        String nombre = txt_nombre.getText().trim();
        String descripcion = txt_descripcion.getText().trim();
        String precioTxt = txt_precio.getText().trim();
        String limiteTxt = txt_limite.getText().trim();

        // 2. Validaciones iniciales
        if (codigo.isEmpty() || nombre.isEmpty() || precioTxt.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Por favor, llene los campos obligatorios (Código, Nombre y Precio).", Alert.AlertType.WARNING);
            return;
        }

        double precio = 0;
        int limite = 0;
        try {
            precio = Double.parseDouble(precioTxt);
            if (!limiteTxt.isEmpty()) {
                limite = Integer.parseInt(limiteTxt);
            }
        } catch (NumberFormatException e) {
            UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "El precio y el límite deben ser valores numéricos.", Alert.AlertType.WARNING);
            return;
        }

        boolean requiereReceta = rb_requiereRecetaSi.isSelected();
        int existencias = 0;

        // 3. Validaciones específicas según el tipo (Con receta o Sin receta)
        if (requiereReceta) {
            if (listaInsumosReceta.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Receta vacía", "Debe agregar al menos un insumo a la receta.", Alert.AlertType.WARNING);
                return;
            }
        } else {
            String existenciasTxt = txt_cantidadInsumo1.getText().trim();
            if (existenciasTxt.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Campos incompletos", "Por favor, indique las existencias iniciales del producto.", Alert.AlertType.WARNING);
                return;
            }
            try {
                existencias = Integer.parseInt(existenciasTxt);
            } catch (NumberFormatException e) {
                UtilidadesFX.mostrarAlertaSimple("Formato incorrecto", "Las existencias deben ser un número entero.", Alert.AlertType.WARNING);
                return;
            }
        }

        try {
            ProductoDAO daoVenta = new ProductoDAO();

            // Comprobar que no exista un producto con el mismo código previamente
            if (daoVenta.buscar(codigo) != null) {
                UtilidadesFX.mostrarAlertaSimple("Código duplicado", "Ya existe un producto registrado con este código.", Alert.AlertType.WARNING);
                return;
            }

            // 4. Crear y registrar el Producto para el Menú
            ProductoVentaDTO productoVenta = new ProductoVentaDTO();
            productoVenta.setCodigoMenu(codigo);
            productoVenta.setNombre(nombre);
            productoVenta.setDescripcion(descripcion);
            productoVenta.setPrecio(precio);
            productoVenta.setLimite(limite);
            productoVenta.setEstatus(1); // 1 = Activo
            productoVenta.setFoto(""); // La fotografía se puede gestionar como funcionalidad aparte

            daoVenta.registrar(productoVenta);

            ProductoCompuestoPorDAO daoReceta = new ProductoCompuestoPorDAO();

            // 5. Registrar dependencias (Receta o Insumo directo)
            if (requiereReceta) {
                for (ProductoCompuestoPorDTO item : listaInsumosReceta) {
                    item.setCodigoMenu(codigo); // Se vincula la receta al código del producto principal

                    // Aseguramos setear el código del insumo para el DAO
                    if(item.getInsumo() != null) {
                        item.setCodigoInsumo(item.getInsumo().getCodigo());
                    }
                    daoReceta.registrar(item);
                }
            } else {
                // Producto de venta directa (Ej. Bebida embotellada)
                // Lo damos de alta en el inventario físico
                ProductoInsumoDAO daoInsumo = new ProductoInsumoDAO();
                ProductoInsumoDTO insumoDirecto = new ProductoInsumoDTO();
                insumoDirecto.setCodigo(codigo);
                insumoDirecto.setNombre(nombre);
                insumoDirecto.setExistencias(existencias);
                insumoDirecto.setEstatus(1);

                daoInsumo.registrar(insumoDirecto);

                // Vinculamos su venta a su propio inventario con cantidad = 1
                ProductoCompuestoPorDTO recetaDirecta = new ProductoCompuestoPorDTO();
                recetaDirecta.setCodigoMenu(codigo);
                recetaDirecta.setCodigoInsumo(codigo);
                recetaDirecta.setCantidad(1.0);

                daoReceta.registrar(recetaDirecta);
            }

            UtilidadesFX.mostrarAlertaSimple("Éxito", "El producto ha sido registrado correctamente en el sistema.", Alert.AlertType.INFORMATION);
            clicCancelar(event); // Reutilizamos tu método cancelar para cerrar la ventana modal

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error de Base de Datos", "Ocurrió un problema al guardar la información: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error Inesperado", "Ha ocurrido un fallo en el sistema al intentar registrar el producto.", Alert.AlertType.ERROR);
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
            rutaFotoLocal = archivoSeleccionado.getAbsolutePath();

            Image imagen = new Image(archivoSeleccionado.toURI().toString());
            img_foto.setImage(imagen);
        }
    }

    @FXML
    private void clicBorrarFoto(ActionEvent event) {
    }

}
