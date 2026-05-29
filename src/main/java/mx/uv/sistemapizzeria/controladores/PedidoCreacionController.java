package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.ClienteDAO;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidoCreacionController implements Initializable {

    @FXML private TableView<ClienteDTO> tbl_clientes;
    @FXML private TableColumn<ClienteDTO, String> col_nombre;
    @FXML private TableColumn<ClienteDTO, String> col_telefono;
    @FXML private TableColumn<ClienteDTO, String> col_email;
    @FXML private ComboBox<DireccionDTO> cmb_cliente;
    @FXML private TextField txt_busqueda;
    @FXML private Button btn_disminuirUno;
    @FXML private Button btn_agregarUno;
    @FXML private Button btn_disminuirDos;
    @FXML private Button btn_agregarDos;
    @FXML private Button btn_disminuirTres;
    @FXML private Button btn_agregarTres;
    @FXML private Button btn_cancelar;
    @FXML private Button btn_guardar;
    @FXML private Button btn_buscar;
    @FXML private HBox AQUI;

    // Labels de los productos (accedidos por lookup en la jerarquía)
    private Label lbl_cantUno;
    private Label lbl_cantDos;
    private Label lbl_cantTres;
    private Label lbl_nombreUno;
    private Label lbl_nombreDos;
    private Label lbl_nombreTres;
    private Label lbl_precioUno;
    private Label lbl_precioDos;
    private Label lbl_precioTres;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidosDAO pedidosDAO = new PedidosDAO();

    private final ObservableList<ClienteDTO> listaClientes = FXCollections.observableArrayList();
    private List<ProductoVentaDTO> productos = new ArrayList<>();

    // Cantidades seleccionadas para cada producto (índices 0, 1, 2)
    private final int[] cantidades = {0, 0, 0};

    // Cliente seleccionado
    private ClienteDTO clienteSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        tbl_clientes.setItems(listaClientes);

        // Cuando el usuario selecciona un cliente, cargar sus direcciones
        tbl_clientes.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            clienteSeleccionado = nuevo;
            cmb_cliente.getItems().clear();
            if (nuevo != null && nuevo.getDireccion() != null) {
                cmb_cliente.getItems().add(nuevo.getDireccion());
                cmb_cliente.getSelectionModel().selectFirst();
            }
        });

        cargarTodosLosClientes();
        cargarProductos();
    }

    private void configurarColumnas() {
        col_nombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));
        col_telefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelefono() != null
                        ? data.getValue().getTelefono() : ""));
        col_email.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail() != null
                        ? data.getValue().getEmail() : ""));
    }

    private void cargarTodosLosClientes() {
        try {
            List<ClienteDTO> todos = clienteDAO.mostrarTodos();
            listaClientes.setAll(todos != null ? todos : new ArrayList<>());
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar clientes:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void cargarProductos() {
        try {
            productos = productoDAO.mostrarTodos();
            // Actualizar los labels en los paneles de productos con datos reales
            actualizarPanelesProductos();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error", "No se pudieron cargar productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void actualizarPanelesProductos() {
        // Los paneles de producto están en AQUI.getParent() que es el AnchorPane
        // Buscamos los VBox dentro del HBox de productos (el segundo HBox del AnchorPane)
        // Como el FXML tiene labels estáticos, los actualizamos buscándolos por posición
        javafx.scene.layout.AnchorPane raiz = (javafx.scene.layout.AnchorPane)
                btn_cancelar.getParent();

        // El HBox de productos es el tercer elemento del AnchorPane (índice 4)
        for (javafx.scene.Node nodo : raiz.getChildren()) {
            if (nodo instanceof javafx.scene.layout.HBox hbox && hbox != AQUI) {
                List<javafx.scene.Node> vboxes = hbox.getChildren();
                for (int i = 0; i < vboxes.size() && i < productos.size(); i++) {
                    if (vboxes.get(i) instanceof VBox vbox) {
                        ProductoVentaDTO prod = productos.get(i);
                        for (javafx.scene.Node hijo : vbox.getChildren()) {
                            if (hijo instanceof Label lbl) {
                                String texto = lbl.getText();
                                if (texto != null && texto.startsWith("Agregados")) {
                                    lbl.setText("Agregados al pedido: " + cantidades[i]);
                                    almacenarLabelCantidad(i, lbl);
                                } else if (texto != null && (texto.startsWith("Pizza") ||
                                        texto.startsWith("Coca") || texto.startsWith("Agregados al"))) {
                                    // nombre del producto
                                } else if (texto != null && texto.startsWith("Precio")) {
                                    lbl.setText("Precio: $" + String.format("%.2f", prod.getPrecio()));
                                    almacenarLabelPrecio(i, lbl);
                                } else if (texto != null && texto.startsWith("Límite")) {
                                    lbl.setText("Límite por cliente: " + prod.getLimite());
                                }
                                // Nombre del producto
                                if (esLabelNombre(texto, i)) {
                                    lbl.setText(prod.getNombre());
                                    almacenarLabelNombre(i, lbl);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean esLabelNombre(String texto, int indice) {
        // El nombre del producto es el Label en bold (segundo label grande en el VBox)
        // Dado que no tenemos fx:id en esos labels, usamos el contexto
        return texto != null && (texto.equals("Pizza Hawaiana") || texto.equals("Coca-cola")
                || (!texto.startsWith("Agregados") && !texto.startsWith("Precio")
                && !texto.startsWith("Límite") && !texto.isEmpty()));
    }

    private void almacenarLabelCantidad(int indice, Label lbl) {
        if (indice == 0) lbl_cantUno = lbl;
        else if (indice == 1) lbl_cantDos = lbl;
        else if (indice == 2) lbl_cantTres = lbl;
    }

    private void almacenarLabelNombre(int indice, Label lbl) {
        if (indice == 0) lbl_nombreUno = lbl;
        else if (indice == 1) lbl_nombreDos = lbl;
        else if (indice == 2) lbl_nombreTres = lbl;
    }

    private void almacenarLabelPrecio(int indice, Label lbl) {
        if (indice == 0) lbl_precioUno = lbl;
        else if (indice == 1) lbl_precioDos = lbl;
        else if (indice == 2) lbl_precioTres = lbl;
    }

    private void actualizarCantidad(int indice) {
        Label lbl = null;
        if (indice == 0) lbl = lbl_cantUno;
        else if (indice == 1) lbl = lbl_cantDos;
        else if (indice == 2) lbl = lbl_cantTres;
        if (lbl != null) lbl.setText("Agregados al pedido: " + cantidades[indice]);
    }

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
        actualizarCantidad(indice);
    }

    // ── Handlers de botones ───────────────────────────────────────────────

    @FXML private void clicDisminuirUno(ActionEvent event)  { modificarCantidad(0, -1); }
    @FXML private void clicAgregarUno(ActionEvent event)    { modificarCantidad(0,  1); }
    @FXML private void clicDisminuirDos(ActionEvent event)  { modificarCantidad(1, -1); }
    @FXML private void clicAgregarDos(ActionEvent event)    { modificarCantidad(1,  1); }
    @FXML private void clicDisminuirTres(ActionEvent event) { modificarCantidad(2, -1); }
    @FXML private void clicAgregarTres(ActionEvent event)   { modificarCantidad(2,  1); }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String termino = txt_busqueda.getText() == null ? "" : txt_busqueda.getText().trim().toLowerCase();
        if (termino.isEmpty()) {
            cargarTodosLosClientes();
            return;
        }
        List<ClienteDTO> filtrados = new ArrayList<>();
        for (ClienteDTO c : listaClientes) {
            if (c.getNombreCompleto().toLowerCase().contains(termino)
                    || (c.getTelefono() != null && c.getTelefono().contains(termino))
                    || (c.getEmail() != null && c.getEmail().toLowerCase().contains(termino))) {
                filtrados.add(c);
            }
        }
        listaClientes.setAll(filtrados);
    }

    @FXML
    private void clicNuevoCliente(ActionEvent event) {
        // Recargar lista después de cerrar (en el flujo real abriría el form de cliente)
        UtilidadesFX.mostrarAlertaSimple("Información",
                "Para agregar un nuevo cliente, use el módulo de Usuarios.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        // Validar selección de cliente
        if (clienteSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Cliente requerido",
                    "Selecciona un cliente de la tabla antes de guardar.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar que al menos un producto tenga cantidad > 0
        boolean hayProductos = false;
        for (int c : cantidades) if (c > 0) { hayProductos = true; break; }
        if (!hayProductos) {
            UtilidadesFX.mostrarAlertaSimple("Sin productos",
                    "Agrega al menos un producto al pedido.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Construir el PedidoDTO
        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setNoCliente(clienteSeleccionado.getNoCliente());
        pedido.setCliente(clienteSeleccionado);

        double total = 0;
        for (int i = 0; i < cantidades.length; i++) {
            if (cantidades[i] > 0 && i < productos.size()) {
                ProductoVentaDTO prod = productos.get(i);
                DetallePedidoDTO det = new DetallePedidoDTO();
                det.setCodigoMenu(prod.getCodigoMenu());
                det.setCantidad(cantidades[i]);
                det.setCosto(prod.getPrecio());
                det.setProductoVenta(prod);
                pedido.agregarDetalle(det);
                total += prod.getPrecio() * cantidades[i];
            }
        }
        pedido.setTotalPagar(total);

        try {
            pedidosDAO.registrar(pedido);
            UtilidadesFX.mostrarAlertaSimple("Pedido registrado",
                    "El pedido se registró correctamente. Total: $" + String.format("%.2f", total),
                    Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al guardar",
                    "No se pudo registrar el pedido:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();
        stage.close();
    }
}
