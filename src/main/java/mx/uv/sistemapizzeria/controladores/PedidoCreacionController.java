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

    private final List<ClienteDTO> todosLosClientes = new ArrayList<>();
    private final ObservableList<ClienteDTO> listaTabla = FXCollections.observableArrayList();

    private final ProductoVentaDTO[] productos = new ProductoVentaDTO[3];
    private final int[] cantidades = {0, 0, 0};
    private Label[] lblCantidades;
    private ClienteDTO clienteSeleccionado;

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCantidades = new Label[]{ lbl_cantUno, lbl_cantDos, lbl_cantTres };

        configurarColumnas();
        tbl_clientes.setItems(listaTabla);

        tbl_clientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, nuevo) -> {
                    clienteSeleccionado = nuevo;
                    cmb_cliente.getItems().clear();
                    /*
                    TODO se va a cambiar a que muestre toda la List<DireccionesDTO> del cliente seleccionado, no es solo uno
                    if (nuevo != null && nuevo.getDireccion() != null) {
                        cmb_cliente.getItems().add(nuevo.getDireccion());
                        cmb_cliente.getSelectionModel().selectFirst();
                    }

                     */
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

    // ── Carga los primeros 3 productos activos desde BD y rellena tarjetas ───
    private void cargarProductos() {
        try {
            List<ProductoVentaDTO> lista = productoDAO.mostrarTodos();
            ImageView[] imgs = { img_uno, img_dos, img_tres };
            Label[]  nombres  = { lbl_nombreUno,  lbl_nombreDos,  lbl_nombreTres  };
            Label[]  precios  = { lbl_precioUno,  lbl_precioDos,  lbl_precioTres  };
            Label[]  limites  = { lbl_limiteUno,  lbl_limiteDos,  lbl_limiteTres  };

            for (int i = 0; i < 3; i++) {
                if (lista != null && i < lista.size()) {
                    ProductoVentaDTO p = lista.get(i);
                    productos[i] = p;

                    nombres[i].setText(p.getNombre() != null ? p.getNombre() : "—");
                    precios[i].setText("Precio: $" + String.format("%.2f", p.getPrecio()));
                    limites[i].setText("Límite por cliente: " + p.getLimite());
                    lblCantidades[i].setText("Agregados al pedido: 0");

                    if (p.getFoto() != null && !p.getFoto().isEmpty()) {
                        try {
                            imgs[i].setImage(new Image("file:" + p.getFoto(), true));
                        } catch (Exception ignored) { }
                    }
                } else {
                    nombres[i].setText("Sin producto");
                    precios[i].setText("—");
                    limites[i].setText("—");
                    deshabilitarTarjeta(i);
                }
            }
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron cargar los productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void deshabilitarTarjeta(int i) {
        javafx.scene.control.Button[] disminuir = { btn_disminuirUno, btn_disminuirDos, btn_disminuirTres };
        javafx.scene.control.Button[] agregar   = { btn_agregarUno,   btn_agregarDos,   btn_agregarTres   };
        disminuir[i].setDisable(true);
        agregar[i].setDisable(true);
    }

    // ── Modificar cantidad en una tarjeta ────────────────────────────────────
    private void modificarCantidad(int idx, int delta) {
        if (productos[idx] == null) return;
        int nueva = cantidades[idx] + delta;
        if (nueva < 0) nueva = 0;
        int limite = productos[idx].getLimite();
        if (limite > 0 && nueva > limite) {
            UtilidadesFX.mostrarAlertaSimple("Límite alcanzado",
                    "No puedes agregar más de " + limite
                            + " unidades de " + productos[idx].getNombre() + ".",
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
                    || (c.getEmail() != null && c.getEmail().toLowerCase().contains(termino))) {
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

        if (clienteSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Cliente requerido",
                    "Selecciona un cliente de la tabla antes de continuar.",
                    Alert.AlertType.WARNING);
            return;
        }

        boolean hayProductos = false;
        for (int c : cantidades) if (c > 0) { hayProductos = true; break; }
        if (!hayProductos) {
            UtilidadesFX.mostrarAlertaSimple("Sin productos",
                    "Agrega al menos un producto al pedido.",
                    Alert.AlertType.WARNING);
            return;
        }

        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setCliente(clienteSeleccionado);
        pedido.setNoCliente(clienteSeleccionado.getNoCliente());

        /*
        DireccionDTO dirSeleccionada = cmb_cliente.getValue();
        if (dirSeleccionada == null && clienteSeleccionado.getDireccion() != null) {
            dirSeleccionada = clienteSeleccionado.getDireccion();
        }
        if (dirSeleccionada != null) {
            pedido.setDireccion(dirSeleccionada.getIdDireccion());
            clienteSeleccionado.setDireccion(dirSeleccionada);
        } else {
            UtilidadesFX.mostrarAlertaSimple("Dirección requerida",
                    "El cliente no tiene dirección registrada. " +
                            "Registra una dirección antes de continuar.",
                    javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }
        
         */

        double total = 0;
        for (int i = 0; i < 3; i++) {
            if (cantidades[i] > 0 && productos[i] != null) {
                DetallePedidoDTO det = new DetallePedidoDTO();
                det.setCodigoMenu(productos[i].getCodigoMenu());
                det.setCantidad(cantidades[i]);
                det.setCosto(productos[i].getPrecio());
                det.setProductoVenta(productos[i]);
                pedido.agregarDetalle(det);
                total += productos[i].getPrecio() * cantidades[i];
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
            // CORRECCIÓN: initOwner e initModality ANTES de setScene
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
