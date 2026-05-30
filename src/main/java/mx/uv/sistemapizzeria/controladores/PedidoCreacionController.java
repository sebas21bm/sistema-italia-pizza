package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.ClienteDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidoCreacionController implements Initializable {

    // ── Búsqueda / tabla de clientes ─────────────────────────────────────────
    @FXML private TextField                          txt_busqueda;
    @FXML private TableView<ClienteDTO>              tbl_clientes;
    @FXML private TableColumn<ClienteDTO, String>    col_nombre;
    @FXML private TableColumn<ClienteDTO, String>    col_telefono;
    @FXML private TableColumn<ClienteDTO, String>    col_email;
    @FXML private ComboBox<DireccionDTO>             cmb_cliente;

    // ── Tarjeta 1 ────────────────────────────────────────────────────────────
    @FXML private ImageView img_uno;
    @FXML private Label     lbl_cantUno;
    @FXML private Label     lbl_nombreUno;
    @FXML private Label     lbl_precioUno;
    @FXML private Label     lbl_limiteUno;

    // ── Tarjeta 2 ────────────────────────────────────────────────────────────
    @FXML private ImageView img_dos;
    @FXML private Label     lbl_cantDos;
    @FXML private Label     lbl_nombreDos;
    @FXML private Label     lbl_precioDos;
    @FXML private Label     lbl_limiteDos;

    // ── Tarjeta 3 ────────────────────────────────────────────────────────────
    @FXML private ImageView img_tres;
    @FXML private Label     lbl_cantTres;
    @FXML private Label     lbl_nombreTres;
    @FXML private Label     lbl_precioTres;
    @FXML private Label     lbl_limiteTres;

    // ── Botones ───────────────────────────────────────────────────────────────
    @FXML private javafx.scene.control.Button btn_cancelar;
    @FXML private javafx.scene.control.Button btn_guardar;
    @FXML private javafx.scene.control.Button btn_buscar;
    @FXML private javafx.scene.control.Button btn_disminuirUno;
    @FXML private javafx.scene.control.Button btn_agregarUno;
    @FXML private javafx.scene.control.Button btn_disminuirDos;
    @FXML private javafx.scene.control.Button btn_agregarDos;
    @FXML private javafx.scene.control.Button btn_disminuirTres;
    @FXML private javafx.scene.control.Button btn_agregarTres;

    // ── Estado interno ────────────────────────────────────────────────────────
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private final List<ClienteDTO>             todosLosClientes = new ArrayList<>();
    private final ObservableList<ClienteDTO>   listaTabla       = FXCollections.observableArrayList();

    // Lista dinámica: ya no es fija de tamaño 3
    private List<ProductoVentaDTO> productos  = new ArrayList<>();
    private int[]                  cantidades = new int[0];
    private Label[]                lblCantidades;

    private ClienteDTO clienteSeleccionado;

    // Arrays de los controles de las tarjetas (en orden 0-1-2)
    // Si el día de mañana se agrega una tarjeta 4 en el FXML, solo se añade aquí.
    private final ImageView[]                                imgs       = new ImageView[3];
    private final Label[]                                    lblNombres = new Label[3];
    private final Label[]                                    lblPrecios = new Label[3];
    private final Label[]                                    lblLimites = new Label[3];
    private final javafx.scene.control.Button[]              btnDismin  = new javafx.scene.control.Button[3];
    private final javafx.scene.control.Button[]              btnAgreg   = new javafx.scene.control.Button[3];

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Poblar arrays de controles desde los campos @FXML
        imgs[0] = img_uno;   imgs[1] = img_dos;   imgs[2] = img_tres;
        lblNombres[0] = lbl_nombreUno; lblNombres[1] = lbl_nombreDos; lblNombres[2] = lbl_nombreTres;
        lblPrecios[0] = lbl_precioUno; lblPrecios[1] = lbl_precioDos; lblPrecios[2] = lbl_precioTres;
        lblLimites[0] = lbl_limiteUno; lblLimites[1] = lbl_limiteDos; lblLimites[2] = lbl_limiteTres;
        lblCantidades  = new Label[]{ lbl_cantUno, lbl_cantDos, lbl_cantTres };
        btnDismin[0]   = btn_disminuirUno; btnDismin[1] = btn_disminuirDos; btnDismin[2] = btn_disminuirTres;
        btnAgreg[0]    = btn_agregarUno;   btnAgreg[1]  = btn_agregarDos;   btnAgreg[2]  = btn_agregarTres;

        configurarColumnas();
        tbl_clientes.setItems(listaTabla);

        // Al seleccionar un cliente, llenar el ComboBox con TODAS sus direcciones
        tbl_clientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, nuevo) -> {
                    clienteSeleccionado = nuevo;
                    cmb_cliente.getItems().clear();
                    if (nuevo != null
                            && nuevo.getDirecciones() != null
                            && !nuevo.getDirecciones().isEmpty()) {
                        cmb_cliente.getItems().addAll(nuevo.getDirecciones());
                        cmb_cliente.getSelectionModel().selectFirst();
                    }
                });

        cargarClientes();
        cargarProductos();
    }

    // ── Columnas de la tabla de clientes ─────────────────────────────────────
    private void configurarColumnas() {
        col_nombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));
        col_telefono.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTelefono() != null ? data.getValue().getTelefono() : ""));
        col_email.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEmail() != null ? data.getValue().getEmail() : ""));
    }

    // ── Carga clientes desde BD ───────────────────────────────────────────────
    private void cargarClientes() {
        try {
            List<ClienteDTO> lista = clienteDAO.mostrarTodos();
            todosLosClientes.clear();
            if (lista != null) todosLosClientes.addAll(lista);
            listaTabla.setAll(todosLosClientes);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron cargar los clientes:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ── Carga productos desde BD de forma dinámica ────────────────────────────
    // Rellena solo las tarjetas que existan en el FXML (máx 3 por ahora).
    // Si hay menos productos que tarjetas, las sobrantes se deshabilitan.
    private void cargarProductos() {
        try {
            List<ProductoVentaDTO> lista = productoDAO.mostrarTodos();
            productos  = lista != null ? lista : new ArrayList<>();
            cantidades = new int[productos.size()];  // todo en 0

            int tarjetas = imgs.length; // 3 tarjetas en el FXML actual
            for (int i = 0; i < tarjetas; i++) {
                if (i < productos.size()) {
                    ProductoVentaDTO p = productos.get(i);
                    lblNombres[i].setText(p.getNombre() != null ? p.getNombre() : "—");
                    lblPrecios[i].setText("Precio: $" + String.format("%.2f", p.getPrecio()));
                    lblLimites[i].setText("Límite por cliente: " + p.getLimite());
                    lblCantidades[i].setText("Agregados al pedido: 0");
                    if (p.getFoto() != null && !p.getFoto().isEmpty()) {
                        try {
                            imgs[i].setImage(new Image("file:" + p.getFoto(), true));
                        } catch (Exception ignored) { }
                    }
                } else {
                    // No hay producto para esta tarjeta: deshabilitar
                    lblNombres[i].setText("Sin producto");
                    lblPrecios[i].setText("—");
                    lblLimites[i].setText("—");
                    btnDismin[i].setDisable(true);
                    btnAgreg[i].setDisable(true);
                }
            }
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron cargar los productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ── Modificar cantidad en una tarjeta ────────────────────────────────────
    private void modificarCantidad(int idx, int delta) {
        if (idx >= productos.size()) return;
        int nueva  = cantidades[idx] + delta;
        if (nueva < 0) nueva = 0;
        int limite = productos.get(idx).getLimite();
        if (limite > 0 && nueva > limite) {
            UtilidadesFX.mostrarAlertaSimple("Límite alcanzado",
                    "No puedes agregar más de " + limite
                            + " unidades de " + productos.get(idx).getNombre() + ".",
                    Alert.AlertType.WARNING);
            return;
        }
        cantidades[idx] = nueva;
        lblCantidades[idx].setText("Agregados al pedido: " + nueva);
    }

    // ── Handlers botones Disminuir / Agregar ─────────────────────────────────
    @FXML private void clicDisminuirUno(ActionEvent e)  { modificarCantidad(0, -1); }
    @FXML private void clicAgregarUno(ActionEvent e)    { modificarCantidad(0,  1); }
    @FXML private void clicDisminuirDos(ActionEvent e)  { modificarCantidad(1, -1); }
    @FXML private void clicAgregarDos(ActionEvent e)    { modificarCantidad(1,  1); }
    @FXML private void clicDisminuirTres(ActionEvent e) { modificarCantidad(2, -1); }
    @FXML private void clicAgregarTres(ActionEvent e)   { modificarCantidad(2,  1); }

    // ── Buscar cliente ────────────────────────────────────────────────────────
    @FXML
    private void clicBuscar(ActionEvent event) {
        String termino = txt_busqueda.getText() == null
                ? "" : txt_busqueda.getText().trim().toLowerCase();
        if (termino.isEmpty()) {
            listaTabla.setAll(todosLosClientes);
            return;
        }
        List<ClienteDTO> filtrados = new ArrayList<>();
        for (ClienteDTO c : todosLosClientes) {
            if (c.getNombreCompleto().toLowerCase().contains(termino)
                    || (c.getTelefono() != null && c.getTelefono().contains(termino))
                    || (c.getEmail()    != null && c.getEmail().toLowerCase().contains(termino))) {
                filtrados.add(c);
            }
        }
        listaTabla.setAll(filtrados);
    }

    // ── Nuevo cliente ─────────────────────────────────────────────────────────
    @FXML
    private void clicNuevoCliente(ActionEvent event) {
        UtilidadesFX.mostrarAlertaSimple("Nuevo cliente",
                "Para registrar un nuevo cliente usa el módulo Usuarios.",
                Alert.AlertType.INFORMATION);
    }

    // ── Cancelar ─────────────────────────────────────────────────────────────
    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    // ── Guardar → validar y abrir confirmación ───────────────────────────────
    @FXML
    private void clicGuardar(ActionEvent event) {

        // 1. Validar cliente seleccionado
        if (clienteSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Cliente requerido",
                    "Selecciona un cliente de la tabla antes de continuar.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 2. Validar dirección seleccionada
        DireccionDTO dirSeleccionada = cmb_cliente.getValue();
        if (dirSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Dirección requerida",
                    "El cliente no tiene dirección registrada o no seleccionaste ninguna.\n"
                            + "Registra una dirección antes de continuar.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 3. Validar que haya al menos un producto
        boolean hayProductos = false;
        for (int c : cantidades) if (c > 0) { hayProductos = true; break; }
        if (!hayProductos) {
            UtilidadesFX.mostrarAlertaSimple("Sin productos",
                    "Agrega al menos un producto al pedido.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 4. Construir el pedido
        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setCliente(clienteSeleccionado);
        pedido.setNoCliente(clienteSeleccionado.getNoCliente());
        pedido.setDireccion(dirSeleccionada);   // dirección viaja en el pedido

        // 5. Agregar detalles de forma dinámica (no limitado a 3)
        double total = 0;
        for (int i = 0; i < productos.size(); i++) {
            if (cantidades[i] > 0) {
                ProductoVentaDTO p = productos.get(i);
                DetallePedidoDTO det = new DetallePedidoDTO();
                det.setCodigoMenu(p.getCodigoMenu());
                det.setCantidad(cantidades[i]);
                det.setCosto(p.getPrecio());
                det.setProductoVenta(p);
                pedido.agregarDetalle(det);
                total += p.getPrecio() * cantidades[i];
            }
        }
        pedido.setTotalPagar(total);

        abrirConfirmacion(pedido);
    }

    // ── Oculta esta ventana y abre PedidoConfirmacion ────────────────────────
    private void abrirConfirmacion(PedidoDTO pedido) {
        try {
            Stage stageCreacion = (Stage) btn_cancelar.getScene().getWindow();

            FXMLLoader loader = UtilidadesFX.cargarFXML("PedidoConfirmacion");
            Parent vista = loader.load();

            PedidoConfirmacionController ctrl = loader.getController();
            ctrl.setPedido(pedido, stageCreacion);

            Stage stageConf = new Stage();
            stageConf.initOwner(stageCreacion);
            stageConf.initModality(Modality.WINDOW_MODAL);
            stageConf.setTitle("Confirmar pedido");
            stageConf.setResizable(false);
            stageConf.setScene(new Scene(vista));
            stageConf.centerOnScreen();

            stageCreacion.hide();
            stageConf.showAndWait();

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudo abrir la confirmación:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();
        stage.close();
    }
}
