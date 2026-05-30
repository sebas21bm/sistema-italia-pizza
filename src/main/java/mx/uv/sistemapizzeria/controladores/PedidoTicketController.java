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
    private Label lbl_fechaHora;
    @FXML
    private Label lbl_empleado;
    @FXML
    private Label lbl_cliente;
    @FXML
    private Label lbl_direccion;
    @FXML
    private Label lbl_telefono;

    @FXML
    private TableView<DetallePedidoDTO> tbl_productosTicket;
    @FXML
    private TableColumn<DetallePedidoDTO, Integer> col_cantidad;
    @FXML
    private TableColumn<DetallePedidoDTO, ProductoVentaDTO> col_descripcion;
    @FXML
    private TableColumn<DetallePedidoDTO, Double> col_subtotal;


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

        if (lbl_fechaHora != null)
            lbl_fechaHora.setText(pedido.getFecha() != null ? pedido.getFecha().format(FMT) : "-");

        if (lbl_empleado != null) {
            EmpleadoDTO emp = Sesion.empleadoSesion;
            lbl_empleado.setText(emp != null ? emp.getNombreCompleto() : "-");
        }

        if (lbl_cliente != null) {
            lbl_cliente.setText(pedido.getCliente() != null
                    ? pedido.getCliente().getNombreCompleto() : "-");
        }

        if (lbl_direccion != null) {
            DireccionDTO d = pedido.getDireccion();

            if (d == null && pedido.getCliente() != null) {
                ClienteDTO cliente = pedido.getCliente();
                if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
                    d = cliente.getDirecciones().get(0);
                }
            }

            if (d != null) {
                String calle  = d.getCalle()        != null ? d.getCalle()        : "";
                String numero = d.getNumero()       != null ? d.getNumero()       : "";
                String ciudad = d.getCiudad()       != null ? d.getCiudad()       : "";
                String cp     = d.getCodigoPostal() != null ? d.getCodigoPostal() : "";
                lbl_direccion.setText((calle + " #" + numero + ", " + ciudad + " C.P. " + cp).trim());
            } else {
                lbl_direccion.setText("-");
            }
        }

        if (lbl_telefono != null) {
            String tel = (pedido.getCliente() != null && pedido.getCliente().getTelefono() != null)
                    ? pedido.getCliente().getTelefono() : "-";
            lbl_telefono.setText(tel);
        }
    }

    private void poblarTabla() {
        ObservableList<DetallePedidoDTO> detalles = FXCollections.observableArrayList();
        if (pedido.getDetalles() != null) detalles.addAll(pedido.getDetalles());
        tbl_productosTicket.setItems(detalles);
    }

    private void poblarTotales() {
        if (lbl_totalPagar != null)
            lbl_totalPagar.setText("$" + String.format("%.2f", pedido.getTotalPagar()));

        if (lbl_numeroPedido != null) {
            String folio = pedido.getIdPedido() > 0 ? String.valueOf(pedido.getIdPedido()) : "—";
            lbl_numeroPedido.setText(folio);
        }

        if (lbl_estadoPedido != null)
            lbl_estadoPedido.setText(pedido.getEstatus() != null ? pedido.getEstatus() : "En proceso");
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        Stage stage = (Stage) tbl_productosTicket.getScene().getWindow();
        stage.close();
    }
}
