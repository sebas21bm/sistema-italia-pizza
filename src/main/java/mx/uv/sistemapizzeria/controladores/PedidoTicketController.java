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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;

public class PedidoTicketController implements Initializable {

    @FXML private Label lbl_fechaHora;
    @FXML private Label lbl_empleado;
    @FXML private Label lbl_cliente;
    @FXML private Label lbl_direccion;
    @FXML private Label lbl_telefono;

    @FXML private TableView<DetallePedidoDTO>           tbl_productosTicket;
    @FXML private TableColumn<DetallePedidoDTO, String> col_cantidad;
    @FXML private TableColumn<DetallePedidoDTO, String> col_descripcion;
    @FXML private TableColumn<DetallePedidoDTO, String> col_precioUnitario;
    @FXML private TableColumn<DetallePedidoDTO, String> col_subtotal;

    @FXML private Label lbl_totalPagar;
    @FXML private Label lbl_numeroPedido;
    @FXML private Label lbl_estadoPedido;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
    }

    /**
     * Inyectar el pedido DESPUÉS de loader.load() y ANTES de stage.show().
     */
    public void setPedido(PedidoDTO pedido) {
        if (pedido == null) return;
        poblarEncabezado(pedido);
        poblarTabla(pedido);
        poblarTotales(pedido);
    }

    private void configurarColumnas() {
        col_cantidad.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));

        col_descripcion.setCellValueFactory(data -> {
            DetallePedidoDTO det = data.getValue();
            String nombre = "-";
            if (det.getProductoVenta() != null && det.getProductoVenta().getNombre() != null) {
                nombre = det.getProductoVenta().getNombre();
            } else if (det.getCodigoMenu() != null) {
                nombre = det.getCodigoMenu();
            }
            return new SimpleStringProperty(nombre);
        });

        col_precioUnitario.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getCosto())));

        col_subtotal.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getSubtotal())));
    }

    private void poblarEncabezado(PedidoDTO pedido) {
        if (lbl_fechaHora != null) {
            lbl_fechaHora.setText(pedido.getFecha() != null ? pedido.getFecha().format(FMT) : "-");
        }

        if (lbl_empleado != null) {
            EmpleadoDTO emp = Sesion.empleadoSesion;
            lbl_empleado.setText(emp != null ? emp.getNombreCompleto() : "-");
        }

        if (lbl_cliente != null) {
            lbl_cliente.setText(pedido.getCliente() != null
                    ? pedido.getCliente().getNombreCompleto() : "-");
        }

        // La dirección viene del pedido, no del cliente
        if (lbl_direccion != null) {
            DireccionDTO d = pedido.getDireccion();
            if (d != null) {
                String calle  = d.getCalle()        != null ? d.getCalle()        : "";
                String numero = d.getNumero()       != null ? d.getNumero()       : "";
                String ciudad = d.getCiudad()       != null ? d.getCiudad()       : "";
                String cp     = d.getCodigoPostal() != null ? d.getCodigoPostal() : "";
                lbl_direccion.setText((calle + " " + numero + ", " + ciudad + " C.P. " + cp).trim());
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

    private void poblarTabla(PedidoDTO pedido) {
        ObservableList<DetallePedidoDTO> detalles = FXCollections.observableArrayList();
        if (pedido.getDetalles() != null) detalles.addAll(pedido.getDetalles());
        tbl_productosTicket.setItems(detalles);
    }

    private void poblarTotales(PedidoDTO pedido) {
        if (lbl_totalPagar != null)
            lbl_totalPagar.setText("$" + String.format("%.2f", pedido.getTotalPagar()));

        if (lbl_numeroPedido != null) {
            String folio = pedido.getIdPedido() > 0 ? String.valueOf(pedido.getIdPedido()) : "—";
            lbl_numeroPedido.setText("Pedido #" + folio);
        }

        if (lbl_estadoPedido != null) {
            lbl_estadoPedido.setText(pedido.getEstatus() != null ? pedido.getEstatus() : "En proceso");
        }
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        Stage stage = (Stage) tbl_productosTicket.getScene().getWindow();
        stage.close();
    }
}
