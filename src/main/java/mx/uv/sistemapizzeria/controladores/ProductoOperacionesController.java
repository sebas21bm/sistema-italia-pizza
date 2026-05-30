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

    // Método para recibir el producto seleccionado y mostrar sus datos en la ventana de edición
    public void editarProductoInventario(ProductoVentaDTO producto) {
        // 1. Llenamos los datos básicos de texto
        txt_codigo.setText(producto.getCodigoMenu());
        txt_nombre.setText(producto.getNombre());

        if (producto.getDescripcion() != null) {
            txt_descripcion.setText(producto.getDescripcion());
        }

        txt_precio.setText(String.valueOf(producto.getPrecio()));
        txt_limite.setText(String.valueOf(producto.getLimite()));

        // 2. Cargamos la fotografía física desde la carpeta local de productos
        if (producto.getFoto() != null && !producto.getFoto().isEmpty()) {
            try {
                String rutaProyecto = System.getProperty("user.dir");
                java.nio.file.Path rutaLocal = java.nio.file.Paths.get(rutaProyecto, "source", "imagenes", "productos", producto.getFoto());

                if (java.nio.file.Files.exists(rutaLocal)) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(rutaLocal.toUri().toString());
                    img_foto.setImage(img);

                    // Guardamos el archivo por si el usuario guarda sin cambiar la imagen
                    archivoFotoSeleccionado = rutaLocal.toFile();
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen en la edición: " + e.getMessage());
            }
        }

        // 3. Lógica para detectar automáticamente si es "Con Receta" o "Sin Receta"
        try {
            ProductoCompuestoPorDAO daoReceta = new ProductoCompuestoPorDAO();
            List<ProductoCompuestoPorDTO> recetaCompleta = daoReceta.obtenerReceta(producto.getCodigoMenu());

            // REGLA: Si la receta tiene más de un ingrediente o su cantidad no es 1.0, es un producto "Con Receta"
            if (recetaCompleta.size() > 1 || (recetaCompleta.size() == 1 && recetaCompleta.get(0).getCantidad() != 1.0)) {

                rb_requiereRecetaSi.setSelected(true);
                pnl_receta.setVisible(true);
                pnl_sinReceta.setVisible(false);
                listaInsumosReceta.setAll(recetaCompleta);

            } else if (recetaCompleta.size() == 1) {

                // REGLA: Si tiene un solo insumo en cantidad 1.0, es un producto "Sin Receta" (venta libre)
                rb_requiereRecetaNo.setSelected(true);
                pnl_receta.setVisible(false);
                pnl_sinReceta.setVisible(true);

                ProductoCompuestoPorDTO enlaceDirecto = recetaCompleta.get(0);

                ProductoInventarioDAO daoInventario = new ProductoInventarioDAO();
                ProductoInventarioDTO insumoInventario = daoInventario.buscar(enlaceDirecto.getCodigoInsumo());

                if (insumoInventario != null) {
                    txt_existencias.setText(String.valueOf(insumoInventario.getExistencias()));

                    if (insumoInventario.getFechaCaducidad() != null) {
                        dp_caducidad.setValue(insumoInventario.getFechaCaducidad());
                    }
                }
            }

            // --- NUEVA REGLA DE SEGURIDAD VISUAL ---
            // Deshabilitamos ambos botones para evitar que el usuario cambie la naturaleza del producto
            rb_requiereRecetaSi.setDisable(true);
            rb_requiereRecetaNo.setDisable(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error de carga", "No se pudieron recuperar los componentes del producto para su edición.", javafx.scene.control.Alert.AlertType.ERROR);
        }
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
        // 1. Validar que toda la interfaz esté correctamente llenada
        if (!datosSonValidos()) return;

        try {
            // 2. Extraer los datos básicos de la pantalla
            String codigo = txt_codigo.getText().trim().toUpperCase();
            String nombre = txt_nombre.getText().trim();
            String descripcion = txt_descripcion.getText().trim();
            double precio = Double.parseDouble(txt_precio.getText().trim());
            int limite = Integer.parseInt(txt_limite.getText().trim());

            // 3. Procesar y copiar la fotografía
            String nombreFoto = procesarFotografia(codigo);

            // 4. Delegar la construcción de objetos y la conexión a la BD
            if (rb_requiereRecetaSi.isSelected()) {
                guardarConReceta(codigo, nombre, descripcion, precio, limite, nombreFoto);
            } else {
                guardarSinReceta(codigo, nombre, descripcion, precio, limite, nombreFoto);
            }

            UtilidadesFX.mostrarAlertaSimple("Éxito", "Operación procesada correctamente.", Alert.AlertType.INFORMATION);
            clicCancelar(event);

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Operación rechazada", ex.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception ex) {
            ex.printStackTrace();
            UtilidadesFX.mostrarAlertaSimple("Error Inesperado", "Ha ocurrido un fallo en el sistema al guardar.", Alert.AlertType.ERROR);
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
            if (listaInsumosReceta.isEmpty()) {
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

    private String procesarFotografia(String codigo) throws Exception {
        String nombreFoto = "";
        if (archivoFotoSeleccionado != null) {
            nombreFoto = archivoFotoSeleccionado.getName();
            String rutaProyecto = System.getProperty("user.dir");

            java.nio.file.Path destinoVenta = java.nio.file.Paths.get(rutaProyecto, "source", "imagenes", "productos", nombreFoto);
            java.nio.file.Files.createDirectories(destinoVenta.getParent());
            java.nio.file.Files.copy(archivoFotoSeleccionado.toPath(), destinoVenta, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            if (rb_requiereRecetaNo.isSelected()) {
                java.nio.file.Path destinoInv = java.nio.file.Paths.get(rutaProyecto, "source", "imagenes", "productosInventario", nombreFoto);
                java.nio.file.Files.createDirectories(destinoInv.getParent());
                java.nio.file.Files.copy(archivoFotoSeleccionado.toPath(), destinoInv, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } else if (!registro) {
            // Recupera la foto anterior si estamos editando y no se subió una nueva
            ProductoVentaDTO prodActual = new ProductoDAO().buscar(codigo);
            if (prodActual != null) nombreFoto = prodActual.getFoto();
        }
        return nombreFoto;
    }

    private void guardarConReceta(String codigo, String nombre, String descripcion, double precio, int limite, String nombreFoto) throws Exception {
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setLimite(limite);
        producto.setEstatus(1);
        producto.setFoto(nombreFoto);

        ProductoDAO dao = new ProductoDAO();
        if (registro) {
            if (dao.buscar(codigo) != null) throw new Exception("Ya existe un producto con este código.");
            dao.registrarConReceta(producto, listaInsumosReceta);
        } else {
            dao.editarProductoCompleto(producto, listaInsumosReceta);
        }
    }

    private void guardarSinReceta(String codigo, String nombre, String descripcion, double precio, int limite, String nombreFoto) throws Exception {
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setLimite(limite);
        producto.setEstatus(1);
        producto.setFoto(nombreFoto);

        int existencias = Integer.parseInt(txt_existencias.getText().trim());
        java.time.LocalDate caducidad = dp_caducidad.getValue();
        ProductoDAO dao = new ProductoDAO();

        if (registro) {
            if (dao.buscar(codigo) != null) throw new Exception("Ya existe un producto con este código.");
            dao.registrarSinReceta(producto, existencias, caducidad);
        } else {
            String codigoInventario = "I" + codigo.substring(1);

            ProductoInventarioDAO daoInv = new ProductoInventarioDAO();
            ProductoInventarioDTO insumo = daoInv.buscar(codigoInventario);
            if (insumo == null) insumo = new ProductoInventarioDTO();
            insumo.setCodigo(codigoInventario);
            insumo.setNombre(nombre);
            insumo.setExistencias(existencias);
            insumo.setFechaCaducidad(caducidad);
            insumo.setFoto(nombreFoto);
            daoInv.editar(insumo);

            ProductoCompuestoPorDTO ingUnico = new ProductoCompuestoPorDTO();
            ingUnico.setCodigoInsumo(codigoInventario);
            ingUnico.setCantidad(1.0);

            dao.editarProductoCompleto(producto, java.util.Arrays.asList(ingUnico));
        }
    }

    @FXML
    private void clicRequiereRecetaSi(ActionEvent event) {
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
