package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.controladores.PedidoEdicionController;
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

    // ── Lista observable directa (sin FilteredList) ───────────────────────────
    private final ObservableList<PedidoDTO> listaTabla = FXCollections.observableArrayList();

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EmpleadoDTO empleado = (EmpleadoDTO) SistemaPizzeria.getMetadatos("empleado");
        if((empleado.getTipoEmpleado().toString()).equals("Administrador")){
            pnl_menuAdmin.setVisible(true);
            pnl_menuCajero.setVisible(false);
        }else if((empleado.getTipoEmpleado().toString()).equals("Cajero")){
            pnl_menuAdmin.setVisible(false);
            pnl_menuCajero.setVisible(true);
        }

        configurarColumnas();

        // Conectar la lista directamente a la tabla, una sola vez
        tbl_pedidos.setItems(listaTabla);

        // Carga inicial: todos los pedidos, sin filtros
        cargarTodos();
    }

    // ── Configuración de columnas ─────────────────────────────────────────────
    private void configurarColumnas() {
        col_folio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIdPedido()));

        col_cliente.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String nombre = (p.getCliente() != null) ? p.getCliente().getNombreCompleto() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        col_fecha.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String fecha = (p.getFecha() != null) ? p.getFecha().format(FMT) : "-";
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        col_total.setCellValueFactory(data -> {
            String total = String.format("$%.2f", data.getValue().getTotalPagar());
            return new javafx.beans.property.SimpleStringProperty(total);
        });

        col_estatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEstatus() != null ? data.getValue().getEstatus() : "-"));

        col_atiende.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String atiende = (p.getCliente() != null && p.getCliente().getTelefono() != null)
                    ? p.getCliente().getTelefono() : "-";
            return new javafx.beans.property.SimpleStringProperty(atiende);
        });
    }

    // ── Carga todos los pedidos sin filtro alguno ─────────────────────────────
    private void cargarTodos() {
        try {
            List<PedidoDTO> todos = dao.mostrarTodos();
            listaTabla.setAll(todos != null ? todos : new ArrayList<>());
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudieron cargar los pedidos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Búsqueda: solo se ejecuta al presionar el botón ───────────────────────
    @FXML
    private void clicBuscar(ActionEvent event) {
        try {
            // 1. Traer pedidos por estatus desde la BD
            List<PedidoDTO> porEstatus = obtenerPorEstatusSeleccionado();

            // 2. Filtrar por texto si el campo no está vacío
            String termino = txt_buscar.getText() == null
                    ? "" : txt_buscar.getText().trim().toLowerCase();

            if (termino.isEmpty()) {
                // Sin texto: mostrar lo que devolvió el filtro de estatus
                listaTabla.setAll(porEstatus != null ? porEstatus : new ArrayList<>());
            } else {
                // Con texto: filtrar la lista resultante manualmente
                List<PedidoDTO> filtrados = new ArrayList<>();
                if (porEstatus != null) {
                    for (PedidoDTO pedido : porEstatus) {
                        if (coincideConTermino(pedido, termino)) {
                            filtrados.add(pedido);
                        }
                    }
                }
                listaTabla.setAll(filtrados);
            }

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudo realizar la búsqueda:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Verifica si un pedido coincide con el término de búsqueda ─────────────
    private boolean coincideConTermino(PedidoDTO pedido, String termino) {
        if (String.valueOf(pedido.getIdPedido()).contains(termino)) return true;

        if (pedido.getCliente() != null) {
            String nombre = pedido.getCliente().getNombreCompleto();
            if (nombre != null && nombre.toLowerCase().contains(termino)) return true;
        }

        if (pedido.getEstatus() != null &&
                pedido.getEstatus().toLowerCase().contains(termino)) return true;

        return false;
    }

    // ── Obtiene pedidos de la BD según el radio button seleccionado ───────────
    private List<PedidoDTO> obtenerPorEstatusSeleccionado() throws Exception {
        if (rb_enProceso.isSelected()) return dao.buscarPorEstatus("En proceso");
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
            cargarTodos(); // refresca sin filtros al volver
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Editar ────────────────────────────────────────────────────────────────
    @FXML
    private void clicEditar(ActionEvent event) {
        PedidoDTO seleccionado = tbl_pedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un pedido de la tabla para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Solo se pueden editar pedidos en proceso
        if (!"En proceso".equals(seleccionado.getEstatus())) {
            UtilidadesFX.mostrarAlertaSimple("No editable",
                    "Solo se pueden editar pedidos con estatus 'En proceso'.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            // Cargar el pedido completo (con detalles y dirección) desde la BD
            PedidoDTO pedidoCompleto = dao.buscar(seleccionado.getIdPedido());

            // Si buscar() falla o retorna null, usamos el seleccionado.
            // El seleccionado ya tiene dirección porque mostrarTodos() ahora la carga.
            PedidoDTO pedidoAEditar = (pedidoCompleto != null) ? pedidoCompleto : seleccionado;

            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoEdicion");
            Parent vista = loader.load();

            // Inyectar el pedido en el controller de edición
            PedidoEdicionController controller = loader.getController();
            controller.setPedido(pedidoAEditar);

            Stage stage = new Stage();
            stage.setTitle("Editar Pedido #" + seleccionado.getIdPedido());
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos(); // refresca sin filtros al volver
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudo cargar el pedido:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    @FXML
    private void clicEliminar(ActionEvent event) {
        PedidoDTO seleccionado = tbl_pedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un pedido de la tabla para cancelarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        if ("Cancelado".equals(seleccionado.getEstatus())) {
            UtilidadesFX.mostrarAlertaSimple("Ya cancelado",
                    "El pedido #" + seleccionado.getIdPedido() + " ya está cancelado.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        // Confirmación antes de cancelar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas cancelar el pedido #" + seleccionado.getIdPedido()
                + " del cliente " + (seleccionado.getCliente() != null
                ? seleccionado.getCliente().getNombreCompleto() : "")
                + "?\nEsta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == javafx.scene.control.ButtonType.OK) {
                try {
                    boolean resultado = dao.eliminar(seleccionado.getIdPedido());
                    if (resultado) {
                        UtilidadesFX.mostrarAlertaSimple("Pedido cancelado",
                                "El pedido #" + seleccionado.getIdPedido() + " fue cancelado.",
                                Alert.AlertType.INFORMATION);
                        cargarTodos();
                    } else {
                        UtilidadesFX.mostrarAlertaSimple("Sin cambios",
                                "No se encontró el pedido o ya estaba cancelado.",
                                Alert.AlertType.WARNING);
                    }
                } catch (Exception e) {
                    UtilidadesFX.mostrarAlertaSimple("Error",
                            "No se pudo cancelar el pedido:\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    // ── Exportar PDF ──────────────────────────────────────────────────────────
    @FXML
    private void clicExportarPDF(ActionEvent event) {
        List<PedidoDTO> pedidos = new ArrayList<>(listaTabla);
        if (pedidos.isEmpty()) {
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
            // La listaTabla solo contiene datos de la vista (sin detalles de productos).
            // Cargamos el pedido completo —con detalles— para cada registro antes de exportar.
            List<PedidoDTO> pedidosCompletos = new ArrayList<>();
            for (PedidoDTO p : pedidos) {
                try {
                    PedidoDTO completo = dao.buscar(p.getIdPedido());
                    pedidosCompletos.add(completo != null ? completo : p);
                } catch (Exception ignored) {
                    pedidosCompletos.add(p); // Si falla uno, incluye el parcial
                }
            }

            ExportadorPDF.exportar(pedidosCompletos, archivo.getAbsolutePath());
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
        List<PedidoDTO> pedidos = new ArrayList<>(listaTabla);
        if (pedidos.isEmpty()) {
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
