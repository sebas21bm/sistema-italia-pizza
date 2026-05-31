package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dao.ProductoDAO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class PedidoEdicionController implements Initializable {

    // ── Encabezado ────────────────────────────────────────────────────────────
    @FXML private Label  lbl_titulo;

    // ── Tabla cliente (solo lectura) ──────────────────────────────────────────
    @FXML private TableView<PedidoDTO>           tbl_cliente;
    @FXML private TableColumn<PedidoDTO, String> col_nombre;
    @FXML private TableColumn<PedidoDTO, String> col_numeroTelefono;
    @FXML private TableColumn<PedidoDTO, String> col_calle;
    @FXML private TableColumn<PedidoDTO, String> col_numeroCalle;
    @FXML private TableColumn<PedidoDTO, String> col_codigoPostal;
    @FXML private TableColumn<PedidoDTO, String> col_ciudad;

    // ── Sección productos ─────────────────────────────────────────────────────
    @FXML private TextField  txt_busquedaProducto;
    @FXML private Button     btn_buscarProducto;
    @FXML private ScrollPane scroll_productos;
    @FXML private FlowPane   flow_productos;
    @FXML private Label      lbl_total;

    // ── Botones ───────────────────────────────────────────────────────────────
    @FXML private Button btn_cancelar;
    @FXML private Button btn_guardar;

    // ── Estado interno ────────────────────────────────────────────────────────
    private final PedidosDAO  pedidosDAO  = new PedidosDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private PedidoDTO              pedidoActual;
    private List<ProductoVentaDTO> todosLosProductos  = new ArrayList<>();
    private int[]                  cantidades         = new int[0];

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarIconoBuscar();
        cargarProductos();
        // El pedido se inyecta después vía setPedido()
    }

    // ── Carga el ícono del botón buscar desde Java (evita rutas con espacios en FXML) ──
    private void cargarIconoBuscar() {
        try {
            URL iconUrl = getClass().getResource("/imagenes/search icon.png");
            if (iconUrl != null) {
                ImageView icono = new ImageView(new Image(iconUrl.toExternalForm()));
                icono.setFitWidth(16);
                icono.setFitHeight(16);
                icono.setPreserveRatio(true);
                btn_buscarProducto.setGraphic(icono);
            }
        } catch (Exception ignored) {
            // Si no carga el ícono el botón queda sin gráfico pero funciona igual
        }
    }

    // ── Inyección del pedido a editar ─────────────────────────────────────────
    public void setPedido(PedidoDTO pedido) {
        this.pedidoActual = pedido;
        cargarDatosPedido();
    }

    // ── Configurar columnas de la tabla de cliente ────────────────────────────
    private void configurarColumnas() {
        tbl_cliente.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

    // ── Carga todos los productos desde BD ───────────────────────────────────
    private void cargarProductos() {
        try {
            List<ProductoVentaDTO> lista = productoDAO.mostrarTodos();
            todosLosProductos = lista != null ? lista : new ArrayList<>();
            cantidades = new int[todosLosProductos.size()];
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error",
                    "No se pudieron cargar productos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ── Carga datos del pedido en pantalla ────────────────────────────────────
    private void cargarDatosPedido() {
        if (pedidoActual == null) return;

        if (lbl_titulo != null) {
            lbl_titulo.setText("Editar pedido #" + pedidoActual.getIdPedido());
        }

        // Tabla del cliente: una sola fila.
        // Se usa Platform.runLater para que el setItems se ejecute DESPUÉS de que
        // el scene graph esté completamente renderizado. Sin esto, la tabla aparece
        // vacía hasta que otra operación fuerza un layout pass (el bug original).
        ObservableList<PedidoDTO> fila = FXCollections.observableArrayList(pedidoActual);
        Platform.runLater(() -> {
            tbl_cliente.setItems(fila);
            tbl_cliente.refresh();
        });

        // Mapear cantidades existentes del pedido → array cantidades[]
        if (pedidoActual.getDetalles() != null) {
            for (DetallePedidoDTO det : pedidoActual.getDetalles()) {
                for (int i = 0; i < todosLosProductos.size(); i++) {
                    if (todosLosProductos.get(i).getCodigoMenu().equals(det.getCodigoMenu())) {
                        cantidades[i] = det.getCantidad();
                        break;
                    }
                }
            }
        }

        // Renderizar todas las tarjetas al abrir (sin filtro)
        renderizarProductos(todosLosProductos);
        actualizarTotal();
    }

    // ── Botón buscar: filtra y redibuja el FlowPane ───────────────────────────
    @FXML
    private void clicBuscarProducto(ActionEvent event) {
        String filtro = txt_busquedaProducto.getText() == null
                ? "" : txt_busquedaProducto.getText().trim().toLowerCase();

        List<ProductoVentaDTO> filtrados = new ArrayList<>();
        for (ProductoVentaDTO p : todosLosProductos) {
            if (filtro.isEmpty()
                    || (p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro))) {
                filtrados.add(p);
            }
        }
        renderizarProductos(filtrados);
    }

    // ── Dibuja las tarjetas en el FlowPane con la lista recibida ─────────────
    private void renderizarProductos(List<ProductoVentaDTO> lista) {
        flow_productos.getChildren().clear();

        if (lista.isEmpty()) {
            Label sinResultados = new Label("Sin resultados para esa búsqueda.");
            sinResultados.setTextFill(Color.web("#8892a0"));
            sinResultados.setFont(Font.font("System", 13));
            flow_productos.getChildren().add(sinResultados);
            return;
        }

        for (ProductoVentaDTO p : lista) {
            int idx = todosLosProductos.indexOf(p);
            flow_productos.getChildren().add(crearTarjeta(p, idx));
        }
    }

    // ── Construye una tarjeta de producto dinámicamente ───────────────────────
    private VBox crearTarjeta(ProductoVentaDTO p, int idx) {
        VBox card = new VBox(6);
        card.setPrefWidth(180);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white;" +
                "-fx-border-color: #e2e6ed; -fx-border-width: 1;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;");

        // Imagen del producto
        ImageView imgView = new ImageView();
        imgView.setFitWidth(150);
        imgView.setFitHeight(80);
        imgView.setPreserveRatio(true);
        if (p.getFoto() != null && !p.getFoto().isEmpty()) {
            Image img = cargarImagen(p.getFoto());
            if (img != null) {
                imgView.setImage(img);
            }
        }

        // Nombre
        Label lblNombre = new Label(p.getNombre() != null ? p.getNombre() : "—");
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblNombre.setTextFill(Color.web("#1a2535"));
        lblNombre.setWrapText(true);

        // Precio
        Label lblPrecio = new Label(String.format("$%.2f", p.getPrecio()));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblPrecio.setTextFill(Color.web("#1265f5"));

        // Límite
        Label lblLimite = new Label("Límite: " + p.getLimite() + " u.");
        lblLimite.setFont(Font.font("System", 11));
        lblLimite.setTextFill(Color.web("#8892a0"));

        // Cantidad en pedido (lee el array global para preservar cantidades entre búsquedas)
        Label lblCantidad = new Label("En pedido: " + cantidades[idx]);
        lblCantidad.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblCantidad.setTextFill(Color.web("#202932"));

        // Botones − / +
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

    // ── Recalcula y muestra el total ──────────────────────────────────────────
    private void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < todosLosProductos.size(); i++) {
            total += todosLosProductos.get(i).getPrecio() * cantidades[i];
        }
        lbl_total.setText(String.format("$%.2f", total));
    }

    // ── Cancelar ─────────────────────────────────────────────────────────────
    @FXML
    private void clicCancelar(ActionEvent event) { cerrarVentana(); }

    // ── Guardar cambios ───────────────────────────────────────────────────────
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
                    "El pedido debe tener al menos un producto.\n" +
                            "Si deseas cancelarlo, usa el cambio de estatus.",
                    Alert.AlertType.WARNING);
            return;
        }

        pedidoActual.getDetalles().clear();
        double total = 0;
        for (int i = 0; i < cantidades.length; i++) {
            if (cantidades[i] > 0 && i < todosLosProductos.size()) {
                ProductoVentaDTO prod = todosLosProductos.get(i);
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
                    "Los cambios se guardaron correctamente.\nTotal: $" + String.format("%.2f", total),
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
        ((Stage) btn_cancelar.getScene().getWindow()).close();
    }

    private Image cargarImagen(String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isBlank()) {
            return null;
        }

        rutaFoto = rutaFoto.trim().replace("\\", "/");

        try {
            if (rutaFoto.startsWith("/imagenes/") || rutaFoto.startsWith("imagenes/")) {
                String rutaRecurso = rutaFoto.startsWith("/") ? rutaFoto : "/" + rutaFoto;
                var recurso = getClass().getResourceAsStream(rutaRecurso);
                if (recurso == null) {
                    return null;
                }
                return new Image(recurso);
            }

            File archivo = new File(rutaFoto);
            if (archivo.exists()) {
                return new Image(archivo.toURI().toString());
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }
}