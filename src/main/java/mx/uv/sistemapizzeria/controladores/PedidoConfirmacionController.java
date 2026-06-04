package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class PedidoConfirmacionController implements Initializable {

    @FXML
    private Label lbl_subtitulo;
    @FXML
    private Label lbl_fechaHora;
    @FXML
    private Label lbl_atiende;
    @FXML
    private Label lbl_cliente;
    @FXML
    private Label lbl_direccion;
    @FXML
    private Label lbl_telefono;
    @FXML
    private Label  lbl_total;

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
        if (pedido == null) {
            return;
        }

        lbl_subtitulo.setText("Revisa los datos antes de confirmar");
        lbl_fechaHora.setText(pedido.getFecha().format(FMT));
        lbl_atiende.setText(Sesion.empleadoSesion.getNombreCompleto());
        lbl_cliente.setText(pedido.getCliente().getNombreCompleto());
        lbl_direccion.setText(pedido.getDireccion().toString());
        lbl_telefono.setText(pedido.getCliente().getTelefono());

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
        lbl_total.setText(sb.toString());

    }

    @FXML
    private void clicAtras(ActionEvent event) {
        cerrarEstaVentana();
    }

    @FXML
    private void clicConfirmar(ActionEvent event) {
        if (pedido == null) return;
        try {
            pedidosDAO.registrar(pedido);
            cerrarEstaVentana();
            abrirTicket(pedido);
            if (stageCreacion != null) {
                stageCreacion.close();
            }
        } catch(SQLException | IOException | ClassNotFoundException | NullPointerException e ){
            UtilidadesFX.mostrarAlertaSimple("Error al registrar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
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

        } catch (IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudo abrir el ticket:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarEstaVentana() {
        Stage stage = (Stage) lbl_atiende.getScene().getWindow();
        stage.close();
    }
}
