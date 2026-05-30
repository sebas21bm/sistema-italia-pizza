package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidoConfirmacionController implements Initializable {

    @FXML
    private Label  lblSubtitulo;
    @FXML
    private Label  lblFechaHora;
    @FXML
    private Label  lblAtiende;
    @FXML
    private Label  lblCliente;
    @FXML
    private Label  lblDireccion;
    @FXML
    private Label  lblTelefono;
    @FXML
    private Label  lblTotal;
    @FXML
    private Button btnAtras;
    @FXML
    private Button btnConfirmar;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidosDAO pedidosDAO = new PedidosDAO();

    private PedidoDTO pedido;
    private Stage stageCreacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    public void setPedido(PedidoDTO pedido, Stage stageCreacion) {
        this.pedido        = pedido;
        this.stageCreacion = stageCreacion;
        poblarResumen();
    }

    private void poblarResumen() {
        if (pedido == null) return;

        if (lblSubtitulo != null)
            lblSubtitulo.setText("Revisa los datos antes de confirmar");

        if (lblFechaHora != null) {
            String fecha = pedido.getFecha() != null ? pedido.getFecha().format(FMT) : "-";
            lblFechaHora.setText(fecha);
        }

        if (lblAtiende != null) {
            EmpleadoDTO emp = Sesion.empleadoSesion;
            lblAtiende.setText(emp != null ? emp.getNombreCompleto() : "-");
        }

        if (lblCliente != null) {
            String nombre = pedido.getCliente() != null
                    ? pedido.getCliente().getNombreCompleto() : "-";
            lblCliente.setText(nombre);
        }

        if (lblDireccion != null) {
            DireccionDTO d = pedido.getDireccion();
            if (d != null) {
                String dir = (d.getCalle()       != null ? d.getCalle()       : "") + " "
                        + (d.getNumero()      != null ? d.getNumero()      : "") + ", "
                        + (d.getCiudad()      != null ? d.getCiudad()      : "") + " C.P. "
                        + (d.getCodigoPostal()!= null ? d.getCodigoPostal(): "");
                lblDireccion.setText(dir.trim());
            } else {
                lblDireccion.setText("-");
            }
        }

        if (lblTelefono != null) {
            String tel = (pedido.getCliente() != null && pedido.getCliente().getTelefono() != null)
                    ? pedido.getCliente().getTelefono() : "-";
            lblTelefono.setText(tel);
        }

        if (lblTotal != null) {
            StringBuilder sb = new StringBuilder();
            if (pedido.getDetalles() != null) {
                for (var det : pedido.getDetalles()) {
                    String nombre = det.getProductoVenta() != null
                            ? det.getProductoVenta().getNombre() : det.getCodigoMenu();
                    sb.append("• ").append(det.getCantidad())
                            .append("x ").append(nombre)
                            .append("  $").append(String.format("%.2f", det.getSubtotal()))
                            .append("\n");
                }
            }
            sb.append("\nTotal: $").append(String.format("%.2f", pedido.getTotalPagar()));
            lblTotal.setText(sb.toString());
        }
    }

    @FXML
    private void clicAtras(ActionEvent event) {
        cerrarEstaVentana();
        if (stageCreacion != null) stageCreacion.show();
    }

    @FXML
    private void clicConfirmar(ActionEvent event) {
        if (pedido == null) return;
        try {
            pedidosDAO.registrar(pedido);
            cerrarEstaVentana();
            Platform.runLater(() -> {
                abrirTicket(pedido);
            });

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al confirmar",
                    "No se pudo guardar el pedido:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void abrirTicket(PedidoDTO pedidoConfirmado) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoTicket");
            Parent vista = loader.load();
            PedidoTicketController ticketCtrl = loader.getController();
            ticketCtrl.setPedido(pedidoConfirmado);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ticket de pedido");
            stage.setResizable(false);

            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudo abrir el ticket:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarEstaVentana() {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }
}
