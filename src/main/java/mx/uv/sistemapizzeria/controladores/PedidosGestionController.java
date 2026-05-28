package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.utilidades.ExportadorCSV;
import mx.uv.sistemapizzeria.utilidades.ExportadorPDF;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidosGestionController implements Initializable {

    // ── FXML ──────────────────────────────────────────────────────────────────
    @FXML private ImageView img_logo;
    @FXML private Accordion ac_menu;
    @FXML private TitledPane tp_administracion;
    @FXML private TitledPane tp_inventarios;
    @FXML private TitledPane tp_pedidos;
    @FXML private AnchorPane pnl_contenido;
    @FXML private HBox hbox_busqueda;
    @FXML private TextField txt_buscar;
    @FXML private TitledPane tp_filtroEstatus;
    @FXML private RadioButton rb_enProceso;
    @FXML private ToggleGroup tg_estatus;
    @FXML private RadioButton rb_entregado;
    @FXML private RadioButton rb_cancelado;

    @FXML private TableView<PedidoDTO> tbl_pedidos;
    @FXML private TableColumn<PedidoDTO, Integer> col_folio;
    @FXML private TableColumn<PedidoDTO, String>  col_cliente;
    @FXML private TableColumn<PedidoDTO, String>  col_fecha;
    @FXML private TableColumn<PedidoDTO, String>  col_total;
    @FXML private TableColumn<PedidoDTO, String>  col_estatus;
    @FXML private TableColumn<PedidoDTO, String>  col_atiende;

    @FXML private Button btn_buscar;
    @FXML private Button btn_menuUsuarios;
    @FXML private Button btn_menuProductos;
    @FXML private Button btn_menuProductosInventario;
    @FXML private Button btn_menuValidacionInventarios;
    @FXML private Button btn_menuPedidos;
    @FXML private Button btn_cerrarSesion;
    @FXML private Button btn_ayudaAcercaDe;
    @FXML private AnchorPane pnl_menuCajero;
    @FXML private ImageView img_logo1;
    @FXML private Accordion ac_menu1;
    @FXML private TitledPane tp_pedidos1;
    @FXML private Button btn_menuPedidos1;
    @FXML private Button btn_cerrarSesion1;
    @FXML private Button btn_ayudaAcercaDe1;
    @FXML private Button btn_exportarPDF;
    @FXML private AnchorPane pnl_menuAdmin;

    // ── Formato de fecha ──────────────────────────────────────────────────────
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── DAO ───────────────────────────────────────────────────────────────────
    private final PedidosDAO dao = new PedidosDAO();

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mostrar panel según rol
        EmpleadoDTO empleado = (EmpleadoDTO) SistemaPizzeria.getMetadatos("empleado");
        if ("Administrador".equals(empleado.getTipoEmpleado().toString())) {
            pnl_menuAdmin.setVisible(true);
            pnl_menuCajero.setVisible(false);
        } else {
            pnl_menuAdmin.setVisible(false);
            pnl_menuCajero.setVisible(true);
        }

        configurarColumnas();
        cargarTodos();
    }

    // ── Configuración de columnas ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void configurarColumnas() {
        // Folio — entero directo
        col_folio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIdPedido()));

        // Cliente — nombre completo derivado del ClienteDTO anidado
        col_cliente.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String nombre = (p.getCliente() != null)
                    ? p.getCliente().getNombreCompleto()
                    : "N/A";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        // Fecha — formateada
        col_fecha.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String fecha = (p.getFecha() != null) ? p.getFecha().format(FMT) : "-";
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        // Total — con signo de pesos y 2 decimales
        col_total.setCellValueFactory(data -> {
            String total = String.format("$%.2f", data.getValue().getTotalPagar());
            return new javafx.beans.property.SimpleStringProperty(total);
        });

        // Estatus — directo
        col_estatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEstatus() != null ? data.getValue().getEstatus() : "-"));

        // Atiende — teléfono del cliente como referencia (la vista no trae empleado)
        col_atiende.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String atiende = (p.getCliente() != null && p.getCliente().getTelefono() != null)
                    ? p.getCliente().getTelefono()
                    : "-";
            return new javafx.beans.property.SimpleStringProperty(atiende);
        });
    }

    // ── Carga de datos ────────────────────────────────────────────────────────
    private void cargarTodos() {
        try {
            List<PedidoDTO> pedidos = dao.mostrarTodos();
            tbl_pedidos.setItems(FXCollections.observableArrayList(pedidos));
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudieron cargar los pedidos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarConLista(List<PedidoDTO> pedidos) {
        tbl_pedidos.setItems(FXCollections.observableArrayList(pedidos));
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────────
    @FXML
    private void clicBuscar(ActionEvent event) {
        String termino = txt_buscar.getText().trim();

        try {
            List<PedidoDTO> resultado;

            if (termino.isEmpty()) {
                // Sin texto: filtrar sólo por estatus seleccionado
                resultado = obtenerPorEstatusSeleccionado();
            } else {
                // Con texto: buscar por nombre/apellido del cliente
                resultado = dao.buscarPorCliente(termino);
            }

            cargarConLista(resultado);

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudo realizar la búsqueda:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private List<PedidoDTO> obtenerPorEstatusSeleccionado() throws Exception {
        if (rb_enProceso.isSelected())  return dao.buscarPorEstatus("En proceso");
        if (rb_entregado.isSelected())  return dao.buscarPorEstatus("Entregado");
        if (rb_cancelado.isSelected())  return dao.buscarPorEstatus("Cancelado");
        return dao.mostrarTodos();
    }

    // ── Nuevo pedido ──────────────────────────────────────────────────────────
    @FXML
    private void clicNuevoPedido(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoCreacion");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Crear Pedido");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos(); // refrescar tabla al volver
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Editar ────────────────────────────────────────────────────────────────
    @FXML
    private void clicEditar(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoEdicion");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Editar Pedido");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    @FXML
    private void clicEliminar(ActionEvent event) {
        // TODO: confirmación y llamada a dao.eliminar(id)
    }

    // ── Exportar PDF ──────────────────────────────────────────────────────────
    @FXML
    private void clicExportarPDF(ActionEvent event) {
        List<PedidoDTO> pedidos = obtenerPedidosParaExportar();
        if (pedidos == null || pedidos.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin datos",
                    "No hay pedidos para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte de pedidos");
        fc.setInitialFileName("reporte_pedidos.pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File archivo = fc.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            ExportadorPDF.exportar(pedidos, archivo.getAbsolutePath());
            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Reporte guardado en:\n" + archivo.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al exportar",
                    "No fue posible generar el PDF:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Exportar CSV ──────────────────────────────────────────────────────────
    @FXML
    private void clicExportarCSV(ActionEvent event) {
        List<PedidoDTO> pedidos = obtenerPedidosParaExportar();
        if (pedidos == null || pedidos.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin datos",
                    "No hay pedidos para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte de pedidos CSV");
        fc.setInitialFileName("reporte_pedidos.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File archivo = fc.showSaveDialog(stage);
        if (archivo == null) return;

        try {
            ExportadorCSV.exportar(pedidos, archivo.getAbsolutePath());
            UtilidadesFX.mostrarAlertaSimple("Exportación exitosa",
                    "Reporte CSV guardado en:\n" + archivo.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al exportar",
                    "No fue posible generar el CSV:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Helper de exportación ─────────────────────────────────────────────────
    private List<PedidoDTO> obtenerPedidosParaExportar() {
        ObservableList<PedidoDTO> items = tbl_pedidos.getItems();
        if (items != null && !items.isEmpty()) {
            return items;
        }
        try {
            return dao.mostrarTodos();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron obtener los pedidos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
            return null;
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────
    @FXML private void clicUsuarios(ActionEvent event) {
        try { SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicProductos(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosGestion", "Productos"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicProductosInventario(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicPedidos(ActionEvent event) {
        try { SistemaPizzeria.setRoot("PedidosGestion", "Pedidos"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicValidacionInventarios(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try { SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clicAyudaAcercaDe(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("AcercaDe");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Acerca de");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
