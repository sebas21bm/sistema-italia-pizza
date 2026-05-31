package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dto.*;

public class PedidoTicketController implements Initializable {

    @FXML
    private
    Label lbl_fechaHora;
    @FXML
    private
    Label lbl_empleado;
    @FXML
    private
    Label lbl_cliente;
    @FXML
    private
    Label lbl_direccion;
    @FXML
    private
    Label lbl_telefono;

    @FXML
    private
    TableView<DetallePedidoDTO> tbl_productosTicket;
    @FXML
    private
    TableColumn<DetallePedidoDTO, Integer> col_cantidad;
    @FXML
    private
    TableColumn<DetallePedidoDTO, ProductoVentaDTO> col_descripcion;
    @FXML
    private
    TableColumn<DetallePedidoDTO, Double> col_subtotal;

    @FXML
    private Label lbl_totalPagar;
    @FXML
    private Label lbl_numeroPedido;
    @FXML
    private Label lbl_estadoPedido;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private boolean columnasConfiguradas = false;
    private PedidoDTO pedido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
    }

    public void setPedido(PedidoDTO pedidoConfirmado) {
        if (pedidoConfirmado == null) {
            return;
        }
        this.pedido = pedidoConfirmado;

        if (!columnasConfiguradas) {
            configurarColumnas();
        }

        poblarEncabezado();
        poblarTabla();
        poblarTotales();

    }

    private void configurarColumnas() {
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("productoVenta"));
        col_descripcion.setCellFactory(col ->
                new TableCell<DetallePedidoDTO, ProductoVentaDTO>() {

                    @Override
                    protected void updateItem(ProductoVentaDTO productoVentaDTO, boolean empty) {
                        super.updateItem(productoVentaDTO, empty);
                        if (empty || productoVentaDTO == null) {
                            setText(null);
                        } else {
                            setText(productoVentaDTO.getDescripcion());
                        }
                    }
                });
        col_subtotal.setCellValueFactory(new PropertyValueFactory<>("costo"));
        columnasConfiguradas = true;
    }

    private void poblarEncabezado() {
        lbl_fechaHora.setText(pedido.getFecha().format(FMT));
        lbl_empleado.setText(Sesion.empleadoSesion.getNoEmpleado());
        lbl_cliente.setText(pedido.getCliente().getNombreCompleto());
        lbl_direccion.setText(pedido.getDireccion().toString());
        lbl_telefono.setText(pedido.getCliente().getTelefono());
    }

    private void poblarTabla() {
        ObservableList<DetallePedidoDTO> detalles = FXCollections.observableArrayList();
        if (pedido.getDetalles() != null) {
            detalles.addAll(pedido.getDetalles());
        }
        tbl_productosTicket.setItems(detalles);
    }

    private void poblarTotales() {
        lbl_totalPagar.setText("$" + String.format("%.2f", pedido.getTotalPagar()));
        lbl_numeroPedido.setText(String.valueOf(pedido.getIdPedido()));
        lbl_estadoPedido.setText(pedido.getEstatus() != null ? pedido.getEstatus() : "En proceso");
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        Stage stage = (Stage) tbl_productosTicket.getScene().getWindow();
        stage.close();
    }
}
