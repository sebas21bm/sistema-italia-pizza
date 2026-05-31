package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.utilidades.ExportadorCSV;
import mx.uv.sistemapizzeria.utilidades.ExportadorPDFGeneral;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class PedidosGestionController implements Initializable {

    @FXML
    private DatePicker dp_fechaInicio;
    @FXML
    private DatePicker dp_fechaFinal;
    @FXML
    private TextField txt_buscar;
    @FXML
    private ComboBox<String> cb_estatusFiltro;

    @FXML
    private TableView<PedidoDTO> tbl_pedidos;
    @FXML
    private TableColumn<PedidoDTO, Integer> col_folio;
    @FXML
    private TableColumn<PedidoDTO, ClienteDTO> col_cliente;
    @FXML
    private TableColumn<PedidoDTO, LocalDateTime> col_fecha;
    @FXML
    private TableColumn<PedidoDTO, Double> col_total;
    @FXML
    private TableColumn<PedidoDTO, String> col_estatus;

    @FXML
    private AnchorPane pnl_menuCajero;
    @FXML
    private AnchorPane pnl_menuAdmin;
    @FXML
    private ComboBox<String> cb_estatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidosDAO pedidosDAO = new PedidosDAO();

    private ObservableList<PedidoDTO> pedidos;
    private final ObservableList<String> estatusFiltro = FXCollections.observableArrayList(
            "En proceso", "Entregado", "Cancelado", "Ver todos");
    private final ObservableList<String> estatus = FXCollections.observableArrayList(
            "Entregado", "Cancelado");

    private String filtroEstatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EmpleadoDTO empleado = (EmpleadoDTO) SistemaPizzeria.getMetadatos("empleado");
        if ((empleado.getTipoEmpleado().toString()).equals("Administrador")) {
            pnl_menuAdmin.setVisible(true);
            pnl_menuCajero.setVisible(false);
        } else if ((empleado.getTipoEmpleado().toString()).equals("Cajero")) {
            pnl_menuAdmin.setVisible(false);
            pnl_menuCajero.setVisible(true);
        }

        cb_estatusFiltro.setItems(estatusFiltro);
        cb_estatus.setItems(estatus);
        dp_fechaFinal.setDisable(true);

        configurarColumnas();
        configurarSeleccionFiltroEstatus();
        configurarSeleccionFecha();
        cargarTodos();
    }

    private void configurarColumnas() {
        col_folio.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        col_cliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        col_cliente.setCellFactory(col -> new  TableCell<PedidoDTO, ClienteDTO>() {
            @Override
            protected void updateItem(ClienteDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty ||  item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreCompleto());
                }
            }
        });
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_fecha.setCellFactory(col -> new TableCell<PedidoDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty ||  item == null) {
                    setText(null);
                } else {
                    setText(FMT.format(item));
                }
            }
        });
        col_total.setCellValueFactory(new PropertyValueFactory<>("totalPagar"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
    }

    private void configurarSeleccionFecha() {
        dp_fechaInicio.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate newValue) {


                if (newValue != null) {
                    dp_fechaFinal.setDisable(false);
                } else  {
                    dp_fechaFinal.setDisable(true);
                    dp_fechaFinal.setValue(null);
                    return;
                }

                if (dp_fechaFinal.getValue() != null && dp_fechaFinal.getValue().isBefore(newValue)) {
                    dp_fechaFinal.setValue(null);
                }

                dp_fechaFinal.setDayCellFactory(dp -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        setDisable(empty || item.isBefore(newValue));
                    }
                });

                if (dp_fechaFinal.getValue() != null && !dp_fechaFinal.getValue().isBefore(newValue)) {
                    cargarPedidosPorFecha();
                }
            }
        });
        dp_fechaFinal.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                cargarPedidosPorFecha();
            }
        });
    }

    private void configurarSeleccionFiltroEstatus(){
        cb_estatusFiltro.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                txt_buscar.setText("");
                dp_fechaInicio.setValue(null);
                dp_fechaFinal.setValue(null);
                filtroEstatus = newValue;
                actualizarInformacion();
            }
        });
    }

    private void actualizarInformacion() {
        if (filtroEstatus.equals("Ver todos")) {
            cargarTodos();
        } else {
            cargarPedidosPorEstatus(filtroEstatus);
        }
    }

    private void cargarPedidosPorFecha() {
        if (dp_fechaInicio.getValue() == null || dp_fechaFinal.getValue() == null) {
            return;
        }
        try {
            LocalDate inicio = dp_fechaInicio.getValue();
            LocalDate fin = dp_fechaFinal.getValue();

            LocalDateTime fechaInicio = inicio.atStartOfDay();
            LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

            pedidos = FXCollections.observableArrayList();
            List<PedidoDTO> pedidosBD = pedidosDAO.buscarPorFecha(fechaInicio, fechaFin);
            pedidos.addAll(pedidosBD);
            tbl_pedidos.setItems(pedidos);
            cb_estatusFiltro.setValue(estatusFiltro.get(3));
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar pedidos", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }

    }

    private void cargarPedidosPorEstatus(String filtroEstatus) {
        try {
            pedidos = FXCollections.observableArrayList();
            List<PedidoDTO> pedidosBD = pedidosDAO.buscarPorEstatus(filtroEstatus);
            pedidos.addAll(pedidosBD);
            tbl_pedidos.setItems(pedidos);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar pedidos", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    private void cargarTodos() {
        try {
            pedidos = FXCollections.observableArrayList();
            List<PedidoDTO> pedidosBD = pedidosDAO.mostrarTodos();
            pedidos.addAll(pedidosBD);
            tbl_pedidos.setItems(pedidos);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar pedidos", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String campoBusqueda = txt_buscar.getText();
        if (campoBusqueda == null || campoBusqueda.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin nombre",
                    "Por favor ingresa un nombre de cliente para buscar.",
                    Alert.AlertType.WARNING);
            return;
        }

        cb_estatusFiltro.setValue(estatusFiltro.get(3));
        dp_fechaFinal.setValue(null);
        dp_fechaInicio.setValue(null);

        cargarPedidosPorNombre(campoBusqueda);
    }

    private void cargarPedidosPorNombre(String nombre) {
        try {
            pedidos = FXCollections.observableArrayList();
            List<PedidoDTO> pedidosBD = pedidosDAO.buscarPorCliente(nombre);
            pedidos.addAll(pedidosBD);
            tbl_pedidos.setItems(pedidos);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar pedidos", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }

    }

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
            cargarTodos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        PedidoDTO seleccionado = tbl_pedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un pedido de la tabla para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (!"En proceso".equals(seleccionado.getEstatus())) {
            UtilidadesFX.mostrarAlertaSimple("No editable",
                    "Solo se pueden editar pedidos con estatus 'En proceso'.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            PedidoDTO pedidoCompleto = pedidosDAO.buscar(seleccionado.getIdPedido());
            PedidoDTO pedidoAEditar = (pedidoCompleto != null) ? pedidoCompleto : seleccionado;

            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoEdicion");
            Parent vista = loader.load();

            PedidoEdicionController controller = loader.getController();
            controller.setPedido(pedidoAEditar);

            Stage stage = new Stage();
            stage.setTitle("Editar Pedido #" + seleccionado.getIdPedido());
            stage.setResizable(false);
            stage.setScene(new Scene(vista));

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos();
        } catch (IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudo cargar el pedido:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch(SQLException |  ClassNotFoundException | NullPointerException e ){
            UtilidadesFX.mostrarAlertaSimple("No se puedo editar el pedido",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCambiarEstatus(ActionEvent event) {
        String nuevoEstatus = cb_estatus.getValue();

        if (nuevoEstatus == null) return;

        PedidoDTO seleccionado = tbl_pedidos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Primero selecciona un pedido para poder cambiar su estatus.",
                    Alert.AlertType.WARNING);

            Platform.runLater(() -> cb_estatus.setValue(null));
            return;
        }

        String estatusActual = seleccionado.getEstatus();

        if (nuevoEstatus.equals(estatusActual)) {
            UtilidadesFX.mostrarAlertaSimple("Sin cambios",
                    "El pedido #" + seleccionado.getIdPedido() + " ya se encuentra con el estatus '" + nuevoEstatus + "'.",
                    Alert.AlertType.INFORMATION);
            javafx.application.Platform.runLater(() -> cb_estatus.setValue(null));
            return;
        }

        if ("Cancelado".equals(estatusActual)) {
            UtilidadesFX.mostrarAlertaSimple("Acción no permitida",
                    "No es posible modificar el estatus de un pedido que ya ha sido cancelado.",
                    Alert.AlertType.WARNING);
            javafx.application.Platform.runLater(() -> cb_estatus.setValue(null));
            return;
        }

        if ("Entregado".equals(estatusActual)) {
            UtilidadesFX.mostrarAlertaSimple("Acción no permitida",
                    "No es posible cambiar a '" + nuevoEstatus + "' un pedido ya entregado.",
                    Alert.AlertType.WARNING);
            javafx.application.Platform.runLater(() -> cb_estatus.setValue(null));
            return;
        }

        boolean confirmado = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar cambio de estatus",
                "¿Estás seguro de marcar el pedido #" + seleccionado.getIdPedido() + " como " + nuevoEstatus + "?",
                "El estatus no podrá ser cambiado después de esta acción."
        );

        if (confirmado) {
            try {
                if (pedidosDAO.cambiarEstatus(seleccionado.getIdPedido(), nuevoEstatus)) {
                    cargarTodos();
                } else {
                    UtilidadesFX.mostrarAlertaSimple("Error", "No se pudo actualizar el estatus.", Alert.AlertType.WARNING);
                }
            } catch( SQLException | ClassNotFoundException | NullPointerException | IOException e ){
                UtilidadesFX.mostrarAlertaSimple("No se pudo cambiar el estado del pedido",
                        e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }

        Platform.runLater(() -> cb_estatus.setValue(null));
    }

    @FXML
    private void clicExportarPDF(ActionEvent event) {
        List<PedidoDTO> pedidos = new ArrayList<>(this.pedidos);
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
            List<PedidoDTO> pedidosCompletos = new ArrayList<>();
            for (PedidoDTO p : pedidos) {
                try {
                    PedidoDTO completo = pedidosDAO.buscar(p.getIdPedido());
                    pedidosCompletos.add(completo != null ? completo : p);
                } catch (Exception ignored) {
                    pedidosCompletos.add(p);
                }
            }

            ExportadorPDFGeneral.generarReportePedidos(archivo.getAbsolutePath(), pedidosCompletos);
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

    @FXML
    private void clicExportarCSV(ActionEvent event) {
        List<PedidoDTO> pedidos = new ArrayList<>(this.pedidos);
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

    // Navegacion
    @FXML
    private void clicUsuarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion", "Productos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        } catch (IOException e) {
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
            SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
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