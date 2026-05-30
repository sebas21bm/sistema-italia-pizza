package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidoEdicionController implements Initializable {

    @FXML private Button btn_cerrar;

    // Tabla de info del pedido (cliente + dirección del pedido)
    @FXML private TableView<PedidoDTO>              tbl_cliente;
    @FXML private TableColumn<PedidoDTO, String>    col_nombre;
    @FXML private TableColumn<PedidoDTO, String>    col_numeroTelefono;
    @FXML private TableColumn<PedidoDTO, String>    col_calle;
    @FXML private TableColumn<PedidoDTO, String>    col_numeroCalle;
    @FXML private TableColumn<PedidoDTO, String>    col_codigoPostal;
    @FXML private TableColumn<PedidoDTO, String>    col_ciudad;

    @FXML private Button btn_disminuirUno;
    @FXML private Button btn_agregarUno;
    @FXML private Button btn_disminuirDos;
    @FXML private Button btn_agregarDos;
    @FXML private Button btn_disminuirTres;
    @FXML private Button btn_agregarTres;
    @FXML private Button btn_cancelar;
    @FXML private Button btn_guardar;

    private final PedidosDAO  pedidosDAO  = new PedidosDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private PedidoDTO              pedidoActual;
    private List<ProductoVentaDTO> productos  = new ArrayList<>();

    // Tamaño dinámico: se ajusta después de cargar productos
    private int[]   cantidades    = new int[0];
    private Label[] lblCantidades = new Label[0];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProductos();
        configurarColumnas();
        // El pedido se inyecta desde setPedido(), llamado por PedidosGestionController
    }

    /** Inyección del pedido a editar */
    public void setPedido(PedidoDTO pedido) {
        this.pedidoActual = pedido;
        cargarDatosPedido();
    }

    // ── Configurar columnas ───────────────────────────────────────────────────
    // La tabla muestra una sola fila con los datos del pedido:
    // nombre del cliente y dirección de entrega (que viaja en el PedidoDTO).
    private void configurarColumnas() {
        col_nombre.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String nombre = (p.getCliente() != null) ? p.getCliente().getNombreCompleto() : "-";
            return new SimpleStringProperty(nombre);
        });

        col_numeroTelefono.setCellValueFactory(data -> {
            PedidoDTO p = data.getValue();
            String tel = (p.getCliente() != null && p.getCliente().getTelefono() != null)
                    ? p.getCliente().getTelefono() : "-";
            return new SimpleStringProperty(tel);
        });

        col_calle.setCellValueFactory(data -> {
            DireccionDTO d = data.getValue().getDireccion();
            return new SimpleStringProperty(d != null && d.getCalle() != null ? d.getCalle() : "-");
        });

        col_numeroCalle.setCellValueFactory(data -> {
            DireccionDTO d = data.getValue().getDireccion();
            return new SimpleStringProperty(d != null && d.getNumero() != null ? d.getNumero() : "-");
        });

        col_codigoPostal.setCellValueFactory(data -> {
            DireccionDTO d = data.getValue().getDireccion();
            return new SimpleStringProperty(d != null && d.getCodigoPostal() != null ? d.getCodigoPostal() : "-");
        });

        col_ciudad.setCellValueFactory(data -> {
            DireccionDTO d = data.getValue().getDireccion();
            return new SimpleStringProperty(d != null && d.getCiudad() != null ? d.getCiudad() : "-");
        });
    }

    // ── Carga productos desde BD ──────────────────────────────────────────────
    private void cargarProductos() {
        try {
            productos  = productoDAO.mostrarTodos();
            if (productos == null) productos = new ArrayList<>();
            cantidades    = new int[productos.size()];
            lblCantidades = new Label[productos.size()];
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ── Carga datos del pedido en pantalla ────────────────────────────────────
    private void cargarDatosPedido() {
        if (pedidoActual == null) return;

        // Actualizar título
        AnchorPane raiz = (AnchorPane) btn_cancelar.getParent();
        for (Node nodo : raiz.getChildren()) {
            if (nodo instanceof Label lbl && lbl.getText() != null
                    && lbl.getText().startsWith("Editar Pedido")) {
                lbl.setText("Editar Pedido #" + pedidoActual.getIdPedido());
                break;
            }
        }

        // Poblar tabla con el pedido (una sola fila: cliente + dirección del pedido)
        ObservableList<PedidoDTO> filaPedido = FXCollections.observableArrayList();
        filaPedido.add(pedidoActual);
        tbl_cliente.setItems(filaPedido);

        // Mapear cantidades del pedido a los índices del catálogo de productos
        if (pedidoActual.getDetalles() != null) {
            for (DetallePedidoDTO det : pedidoActual.getDetalles()) {
                for (int i = 0; i < productos.size(); i++) {
                    if (productos.get(i).getCodigoMenu().equals(det.getCodigoMenu())) {
                        cantidades[i] = det.getCantidad();
                        break;
                    }
                }
            }
        }

        actualizarPanelesProductos(raiz);
    }

    // ── Actualiza los labels de las tarjetas de producto ─────────────────────
    private void actualizarPanelesProductos(AnchorPane raiz) {
        for (Node nodo : raiz.getChildren()) {
            if (nodo instanceof HBox hbox) {
                List<Node> vboxes = hbox.getChildren();
                for (int i = 0; i < vboxes.size() && i < productos.size(); i++) {
                    if (vboxes.get(i) instanceof VBox vbox) {
                        ProductoVentaDTO prod = productos.get(i);
                        for (Node hijo : vbox.getChildren()) {
                            if (hijo instanceof Label lbl) {
                                String texto = lbl.getText();
                                if (texto == null) continue;
                                if (texto.startsWith("Agregados")) {
                                    lbl.setText("Agregados al pedido: " + cantidades[i]);
                                    lblCantidades[i] = lbl;
                                } else if (texto.startsWith("Precio")) {
                                    lbl.setText("Precio: $" + String.format("%.2f", prod.getPrecio()));
                                } else if (texto.startsWith("Límite") || texto.startsWith("Limite")) {
                                    lbl.setText("Límite por cliente: " + prod.getLimite());
                                } else if (texto.startsWith("Código") || texto.startsWith("Codigo")) {
                                    lbl.setText("Código: " + prod.getCodigoMenu());
                                } else if (!texto.isEmpty() && !texto.startsWith("$")) {
                                    lbl.setText(prod.getNombre());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Modificar cantidad ────────────────────────────────────────────────────
    private void modificarCantidad(int indice, int delta) {
        if (productos == null || indice >= productos.size()) return;
        ProductoVentaDTO prod = productos.get(indice);
        int nueva = cantidades[indice] + delta;
        if (nueva < 0) nueva = 0;
        int limite = prod.getLimite();
        if (limite > 0 && nueva > limite) {
            UtilidadesFX.mostrarAlertaSimple("Límite alcanzado",
                    "No puedes agregar más de " + limite + " unidades de " + prod.getNombre(),
                    Alert.AlertType.WARNING);
            return;
        }
        cantidades[indice] = nueva;
        if (lblCantidades[indice] != null)
            lblCantidades[indice].setText("Agregados al pedido: " + nueva);
    }

    @FXML private void clicDisminuirUno(ActionEvent event)  { modificarCantidad(0, -1); }
    @FXML private void clicAgregarUno(ActionEvent event)    { modificarCantidad(0,  1); }
    @FXML private void clicDisminuirDos(ActionEvent event)  { modificarCantidad(1, -1); }
    @FXML private void clicAgregarDos(ActionEvent event)    { modificarCantidad(1,  1); }
    @FXML private void clicDisminuirTres(ActionEvent event) { modificarCantidad(2, -1); }
    @FXML private void clicAgregarTres(ActionEvent event)   { modificarCantidad(2,  1); }

    @FXML
    private void clicCerrar(ActionEvent event) { cerrarVentana(); }

    @FXML
    private void clicCancelar(ActionEvent event) { cerrarVentana(); }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (pedidoActual == null) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No hay pedido cargado para editar.",
                    Alert.AlertType.ERROR);
            return;
        }

        boolean hayProductos = false;
        for (int c : cantidades) if (c > 0) { hayProductos = true; break; }
        if (!hayProductos) {
            UtilidadesFX.mostrarAlertaSimple("Sin productos",
                    "El pedido debe tener al menos un producto con cantidad mayor a cero.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Reconstruir detalles de forma dinámica
        pedidoActual.getDetalles().clear();
        double total = 0;
        for (int i = 0; i < cantidades.length; i++) {
            if (cantidades[i] > 0 && i < productos.size()) {
                ProductoVentaDTO prod = productos.get(i);
                DetallePedidoDTO det  = new DetallePedidoDTO();
                det.setIdPedido(pedidoActual.getIdPedido());
                det.setCodigoMenu(prod.getCodigoMenu());
                det.setCantidad(cantidades[i]);
                det.setCosto(prod.getPrecio());
                det.setProductoVenta(prod);
                pedidoActual.agregarDetalle(det);
                total += prod.getPrecio() * cantidades[i];
            }
        }
        pedidoActual.setTotalPagar(total);

        try {
            pedidosDAO.editar(pedidoActual);
            UtilidadesFX.mostrarAlertaSimple("Pedido actualizado",
                    "Los cambios se guardaron correctamente. Total: $" + String.format("%.2f", total),
                    Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al guardar",
                    "No se pudo actualizar el pedido:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();
        stage.close();
    }
}
