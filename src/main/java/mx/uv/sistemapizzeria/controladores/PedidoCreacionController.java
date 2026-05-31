package mx.uv.sistemapizzeria.controladores;

import java.io.File;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    // ── Sección clientes ──────────────────────────────────────────────────────
    @FXML private TextField                       txt_busqueda;
    @FXML private TableView<ClienteDTO>           tbl_clientes;
    @FXML private TableColumn<ClienteDTO, String> col_nombre;
    @FXML private TableColumn<ClienteDTO, String> col_telefono;
    @FXML private TableColumn<ClienteDTO, String> col_email;
    @FXML private ComboBox<DireccionDTO>          cmb_cliente;

    // ── Sección productos ─────────────────────────────────────────────────────
    @FXML private TextField  txt_busquedaProducto;
    @FXML private Button     btn_buscarProducto;
    @FXML private ScrollPane scroll_productos;
    @FXML private FlowPane   flow_productos;
    @FXML private Label      lbl_total;

    // ── Botones de acción ─────────────────────────────────────────────────────
    @FXML private Button btn_cancelar;
    @FXML private Button btn_guardar;
    @FXML private Button btn_buscar;

    // ── Estado interno ────────────────────────────────────────────────────────
    private final ClienteDAO  clienteDAO  = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private final List<ClienteDTO>           todosLosClientes = new ArrayList<>();
    private final ObservableList<ClienteDTO> listaTabla       = FXCollections.observableArrayList();

    private List<ProductoVentaDTO> todosLosProductos   = new ArrayList<>();
    private List<ProductoVentaDTO> productosFiltrados  = new ArrayList<>();
    private int[]                  cantidades;          // índices alineados a todosLosProductos

    private ClienteDTO clienteSeleccionado;

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        tbl_clientes.setItems(listaTabla);

        // Al seleccionar cliente → llenar combo de direcciones
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

    // ── Carga clientes ────────────────────────────────────────────────────────
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

    // ── Carga productos desde BD y renderiza tarjetas ─────────────────────────
    private void cargarProductos() {
        try {
            List<ProductoVentaDTO> lista = productoDAO.mostrarTodos();
            todosLosProductos = lista != null ? lista : new ArrayList<>();
            cantidades = new int[todosLosProductos.size()]; // todos en 0
            filtrarYRenderizarProductos("");
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron cargar los productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ── Filtra la lista y vuelve a dibujar el FlowPane ───────────────────────
    private void filtrarYRenderizarProductos(String filtro) {
        flow_productos.getChildren().clear();
        productosFiltrados = new ArrayList<>();

        String lower = filtro.toLowerCase();
        for (ProductoVentaDTO p : todosLosProductos) {
            if (filtro.isEmpty()
                    || (p.getNombre() != null && p.getNombre().toLowerCase().contains(lower))) {
                productosFiltrados.add(p);
            }
        }

        if (productosFiltrados.isEmpty()) {
            Label noResultados = new Label("Sin resultados");
            noResultados.setTextFill(Color.web("#8892a0"));
            noResultados.setFont(Font.font("System", 13));
            flow_productos.getChildren().add(noResultados);
            return;
        }

        for (ProductoVentaDTO p : productosFiltrados) {
            // El índice real en todosLosProductos (para leer/escribir cantidades[])
            int idx = todosLosProductos.indexOf(p);
            flow_productos.getChildren().add(crearTarjeta(p, idx));
        }
    }

    // ── Construye una tarjeta de producto dinámicamente ───────────────────────
    private VBox crearTarjeta(ProductoVentaDTO p, int idx) {
        VBox card = new VBox(6);
        card.setPrefWidth(210);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white;" +
                "-fx-border-color: #e2e6ed; -fx-border-width: 1;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;");

        // Imagen del producto
        ImageView imgView = new ImageView();
        imgView.setFitWidth(186);
        imgView.setFitHeight(90);
        imgView.setPreserveRatio(true);
        imgView.setPickOnBounds(true);
        if (p.getFoto() != null && !p.getFoto().isEmpty()) {
            Image img = cargarImagen(p.getFoto());
            if (img != null) {
                imgView.setImage(img);
            }
        }

        // Nombre del producto
        Label lblNombre = new Label(p.getNombre() != null ? p.getNombre() : "—");
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setTextFill(Color.web("#1a2535"));
        lblNombre.setWrapText(true);

        // Precio
        Label lblPrecio = new Label(String.format("$%.2f", p.getPrecio()));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 15));
        lblPrecio.setTextFill(Color.web("#1265f5"));

        // Límite
        Label lblLimite = new Label("Límite: " + p.getLimite() + " u.");
        lblLimite.setFont(Font.font("System", 11));
        lblLimite.setTextFill(Color.web("#8892a0"));

        // Contador (muestra la cantidad actual del array global)
        Label lblCantidad = new Label("En pedido: " + cantidades[idx]);
        lblCantidad.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblCantidad.setTextFill(Color.web("#202932"));

        // Botones + / –
        Button btnMenos = new Button("−");
        btnMenos.setPrefWidth(52);
        btnMenos.setPrefHeight(28);
        btnMenos.setStyle("-fx-background-color: #ff7a1a; -fx-background-radius: 6;" +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Button btnMas = new Button("+");
        btnMas.setPrefWidth(52);
        btnMas.setPrefHeight(28);
        btnMas.setStyle("-fx-background-color: #1265f5; -fx-background-radius: 6;" +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        btnMenos.setOnAction(e -> {
            if (cantidades[idx] > 0) {
                cantidades[idx]--;
                lblCantidad.setText("En pedido: " + cantidades[idx]);
                actualizarTotal();
            }
        });

        btnMas.setOnAction(e -> {
            int limite = p.getLimite();
            if (limite > 0 && cantidades[idx] >= limite) {
                UtilidadesFX.mostrarAlertaSimple("Límite alcanzado",
                        "No puedes agregar más de " + limite + " unidades de " + p.getNombre() + ".",
                        Alert.AlertType.WARNING);
                return;
            }
            cantidades[idx]++;
            lblCantidad.setText("En pedido: " + cantidades[idx]);
            actualizarTotal();
        });

        HBox botones = new HBox(8, btnMenos, btnMas);
        botones.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(imgView, lblNombre, lblPrecio, lblLimite, lblCantidad, botones);
        return card;
    }

    // ── Recalcula el total visible ────────────────────────────────────────────
    private void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < todosLosProductos.size(); i++) {
            total += todosLosProductos.get(i).getPrecio() * cantidades[i];
        }
        lbl_total.setText(String.format("$%.2f", total));
    }

    // ── Buscar cliente (botón) ────────────────────────────────────────────────
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

    // ── Buscar producto (botón) ───────────────────────────────────────────────
    @FXML
    private void clicBuscarProducto(ActionEvent event) {
        String termino = txt_busquedaProducto.getText() == null
                ? "" : txt_busquedaProducto.getText().trim();
        filtrarYRenderizarProductos(termino);
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
        ((Stage) btn_cancelar.getScene().getWindow()).close();
    }

    // ── Guardar → validar y abrir confirmación ───────────────────────────────
    @FXML
    private void clicGuardar(ActionEvent event) {

        // 1. Cliente seleccionado
        if (clienteSeleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Cliente requerido",
                    "Selecciona un cliente de la tabla antes de continuar.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 2. Dirección seleccionada
        DireccionDTO dirSeleccionada = cmb_cliente.getValue();
        if (dirSeleccionada == null) {
            UtilidadesFX.mostrarAlertaSimple("Dirección requerida",
                    "El cliente no tiene dirección registrada o no seleccionaste ninguna.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 3. Al menos un producto
        boolean hayProductos = false;
        for (int c : cantidades) if (c > 0) { hayProductos = true; break; }
        if (!hayProductos) {
            UtilidadesFX.mostrarAlertaSimple("Sin productos",
                    "Agrega al menos un producto al pedido.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 4. Construir pedido
        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setCliente(clienteSeleccionado);
        pedido.setNoCliente(clienteSeleccionado.getNoCliente());
        pedido.setDireccion(dirSeleccionada);

        double total = 0;
        for (int i = 0; i < todosLosProductos.size(); i++) {
            if (cantidades[i] > 0) {
                ProductoVentaDTO p = todosLosProductos.get(i);
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

    // ── Abre PedidoConfirmacion ───────────────────────────────────────────────
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

    // Método basado enn el método de Yara (más o menos porque sigue guardando las imagenes en el proyecto)
    // Esta cosa no se ni como jalo, pero funciona
    // este método esta hecho para que alguien más (Isaac) lo continue, por eso están las alertas, emojis
    // ── Método adaptado CON MODO DEBUG (Rastreador de rutas) ──
    private Image cargarImagen(String rutaFoto) {
        System.out.println("\n--- Intentando cargar imagen: [" + rutaFoto + "] ---");

        if (rutaFoto == null || rutaFoto.isBlank()) {
            System.out.println("  -> La ruta está vacía o nula. Abortando.");
            return null;
        }

        // Limpieza original
        rutaFoto = rutaFoto.trim().replace("\\", "/");
        System.out.println("  -> Ruta limpiada: [" + rutaFoto + "]");

        try {
            // Adaptación: Si solo viene el nombre, inyectamos la carpeta
            if (!rutaFoto.contains("/")) {
                rutaFoto = "/imagenes/productos/" + rutaFoto;
                System.out.println("  -> Se le agregó la carpeta de tu módulo: [" + rutaFoto + "]");
            }

            // Lógica original del método de la clase ProductosGestion, (donde yara sube las imagenes usando ruta absoluta)
            if (rutaFoto.startsWith("/imagenes/") || rutaFoto.startsWith("imagenes/")) {
                String rutaRecurso = rutaFoto.startsWith("/") ? rutaFoto : "/" + rutaFoto;
                System.out.println("  -> 1. Buscando en Resources internos con la ruta EXACTA: [" + rutaRecurso + "]");

                var recurso = getClass().getResourceAsStream(rutaRecurso);

                if (recurso == null) {
                    System.out.println("  ❌ Falló: Maven no lo encontró en resources (¿Falta compilar o el nombre/extensión no coincide?).");
                } else {
                    System.out.println("  ✅ Recurso encontrado. Intentando construir la imagen...");
                    Image img = new Image(recurso);

                    if (img.isError()) {
                        System.out.println("  ⚠️ ALERTA: La imagen existe pero JavaFX no pudo leer los píxeles (¿Es un archivo dañado o WebP?).");
                    } else {
                        System.out.println("  ✅ ¡Imagen cargada exitosamente!");
                        return img;
                    }
                }
            }

            System.out.println("  -> 2. Intentando cargar como archivo físico absoluto en la PC...");
            java.io.File archivo = new java.io.File(rutaFoto);

            if (archivo.exists()) {
                System.out.println("  ✅ Archivo físico encontrado. Retornando imagen externa.");
                return new Image(archivo.toURI().toString());
            } else {
                System.out.println("  ❌ Tampoco existe el archivo físico en el disco duro.");
            }

        } catch (Exception e) {
            System.out.println("  ❌ Excepción crítica procesando la imagen: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- Fin del intento (Resultado: Imagen no cargada) ---");
        return null;
    }

}
