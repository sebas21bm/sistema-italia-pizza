package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ClienteDAO;
import mx.uv.sistemapizzeria.modelo.dao.EmpleadoDAO;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

public class UsuariosGestionController implements Initializable {

    // ── FXML ──────────────────────────────────────────────────────────────────
    @FXML private AnchorPane pnl_menuLateral;
    @FXML private ImageView img_logo;
    @FXML private Accordion ac_menu;
    @FXML private TitledPane tp_administracion;
    @FXML private TitledPane tp_inventarios;
    @FXML private TitledPane tp_pedidos;
    @FXML private AnchorPane pnl_contenido;
    @FXML private HBox hbox_busqueda;
    @FXML private TextField txt_buscar;

    // TableView tipada como Object para manejar EmpleadoDTO y ClienteDTO
    @FXML private TableView<Object> tbl_usuarios;
    @FXML private TableColumn<Object, String> col_nombre;
    @FXML private TableColumn<Object, String> col_telefono;
    @FXML private TableColumn<Object, String> col_email;
    @FXML private TableColumn<Object, String> col_direccion;
    @FXML private TableColumn<Object, String> col_estatus;
    @FXML private TableColumn<Object, String> col_tipo;

    // Filtros de búsqueda (campo por el que buscar)
    @FXML private TitledPane tp_filtroEstatus;
    @FXML private ToggleGroup tg_estatus;
    @FXML private RadioButton rb_nombre;
    @FXML private RadioButton rb_telefono;
    @FXML private RadioButton rb_direccion;

    // Filtros de tipo (Empleado / Cliente)
    @FXML private TitledPane tp_filtroTipo;
    @FXML private ToggleGroup tg_tipo;
    @FXML private RadioButton rb_tipoEmpleado;
    @FXML private RadioButton rb_tipoCliente;

    // Botones
    @FXML private Button btn_nuevoUsuario;
    @FXML private Button btn_editar;
    @FXML private Button btn_eliminar;
    @FXML private Button btn_buscar;
    @FXML private Button btn_menuUsuarios;
    @FXML private Button btn_menuProductos;
    @FXML private Button btn_menuInsumos;
    @FXML private Button btn_menuValidacionInventarios;
    @FXML private Button btn_menuPedidos;
    @FXML private Button btn_cerrarSesion;
    @FXML private Button btn_ayudaAcercaDe;

    // ── DAOs ──────────────────────────────────────────────────────────────────
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ClienteDAO  clienteDAO  = new ClienteDAO();

    // ── Lista observable directa (igual que PedidosGestionController) ─────────
    private final ObservableList<Object> listaTabla = FXCollections.observableArrayList();

    // ── Inicialización ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        tbl_usuarios.setItems(listaTabla);

        // Carga inicial: todos los empleados (rb_tipoEmpleado viene seleccionado por defecto)
        cargarTodos();
    }

    // ── Configuración de columnas ─────────────────────────────────────────────
    // Misma técnica que PedidosGestionController: lambda con lógica según tipo
    private void configurarColumnas() {

        col_nombre.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "";
            if (obj instanceof EmpleadoDTO e) {
                valor = e.getNombreCompleto();
            } else if (obj instanceof ClienteDTO c) {
                valor = c.getNombreCompleto();
            }
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        col_telefono.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "";
            if (obj instanceof EmpleadoDTO e) valor = e.getTelefono() != null ? e.getTelefono() : "-";
            else if (obj instanceof ClienteDTO c) valor = c.getTelefono() != null ? c.getTelefono() : "-";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        col_email.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "";
            if (obj instanceof EmpleadoDTO e) valor = e.getEmail() != null ? e.getEmail() : "-";
            else if (obj instanceof ClienteDTO c) valor = c.getEmail() != null ? c.getEmail() : "-";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        col_direccion.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "-";
            DireccionDTO dir = null;
            if (obj instanceof EmpleadoDTO e) dir = e.getDireccion();
            else if (obj instanceof ClienteDTO c) dir = c.getDireccion();

            if (dir != null) {
                valor = dir.getCalle() + " " + dir.getNumero()
                        + ", " + dir.getCiudad();
            }
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        col_estatus.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "-";
            if (obj instanceof EmpleadoDTO e) valor = e.isEstatus() ? "Activo" : "Inactivo";
            else if (obj instanceof ClienteDTO c) valor = "1".equals(c.getEstatus()) ? "Activo" : "Inactivo";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        col_tipo.setCellValueFactory(data -> {
            Object obj = data.getValue();
            String valor = "-";
            if (obj instanceof EmpleadoDTO e)
                valor = e.getTipoEmpleado() != null ? e.getTipoEmpleado().toString() : "-";
            else if (obj instanceof ClienteDTO)
                valor = "Cliente";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });
    }

    // ── Carga todos según el tipo seleccionado ────────────────────────────────
    private void cargarTodos() {
        try {
            if (esEmpleado()) {
                List<EmpleadoDTO> todos = empleadoDAO.mostrarTodos();
                listaTabla.setAll(todos != null ? todos : new ArrayList<>());
            } else {
                List<ClienteDTO> todos = clienteDAO.mostrarTodos();
                listaTabla.setAll(todos != null ? todos : new ArrayList<>());
            }
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudieron cargar los usuarios:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Búsqueda: botón buscar (igual que PedidosGestionController) ───────────
    @FXML
    private void clicBuscar(ActionEvent event) {
        try {
            String termino = txt_buscar.getText() == null
                    ? "" : txt_buscar.getText().trim().toLowerCase();

            // 1. Cargar lista base según tipo
            List<Object> base = new ArrayList<>();
            if (esEmpleado()) {
                List<EmpleadoDTO> todos = empleadoDAO.mostrarTodos();
                if (todos != null) base.addAll(todos);
            } else {
                List<ClienteDTO> todos = clienteDAO.mostrarTodos();
                if (todos != null) base.addAll(todos);
            }

            // 2. Si hay texto, filtrar localmente según el campo seleccionado
            if (termino.isEmpty()) {
                listaTabla.setAll(base);
            } else {
                List<Object> filtrados = new ArrayList<>();
                for (Object obj : base) {
                    if (coincideConTermino(obj, termino)) {
                        filtrados.add(obj);
                    }
                }
                listaTabla.setAll(filtrados);
            }

        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error", "No se pudo realizar la búsqueda:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ── Verifica si el objeto coincide con el término según el radio seleccionado
    private boolean coincideConTermino(Object obj, String termino) {
        if (rb_nombre != null && rb_nombre.isSelected()) {
            // Buscar por nombre
            if (obj instanceof EmpleadoDTO e)
                return e.getNombreCompleto().toLowerCase().contains(termino);
            if (obj instanceof ClienteDTO c)
                return c.getNombreCompleto().toLowerCase().contains(termino);

        } else if (rb_telefono != null && rb_telefono.isSelected()) {
            // Buscar por teléfono
            if (obj instanceof EmpleadoDTO e)
                return e.getTelefono() != null && e.getTelefono().contains(termino);
            if (obj instanceof ClienteDTO c)
                return c.getTelefono() != null && c.getTelefono().contains(termino);

        } else if (rb_direccion != null && rb_direccion.isSelected()) {
            // Buscar por dirección
            DireccionDTO dir = null;
            if (obj instanceof EmpleadoDTO e) dir = e.getDireccion();
            if (obj instanceof ClienteDTO c) dir = c.getDireccion();
            if (dir != null) {
                String dirCompleta = (dir.getCalle() + " " + dir.getCiudad()).toLowerCase();
                return dirCompleta.contains(termino);
            }
        } else {
            // Sin radio seleccionado: buscar en todo
            if (obj instanceof EmpleadoDTO e)
                return e.getNombreCompleto().toLowerCase().contains(termino)
                        || (e.getTelefono() != null && e.getTelefono().contains(termino));
            if (obj instanceof ClienteDTO c)
                return c.getNombreCompleto().toLowerCase().contains(termino)
                        || (c.getTelefono() != null && c.getTelefono().contains(termino));
        }
        return false;
    }

    // ── Al cambiar el filtro de tipo: recargar la tabla ───────────────────────
    @FXML
    private void clicFiltroTipo(ActionEvent event) {
        txt_buscar.clear();
        cargarTodos();
    }

    // ── Nuevo usuario ─────────────────────────────────────────────────────────
    @FXML
    private void clicNuevoUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioTipo");
            Parent vista = loader.load();
            Scene scene = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setResizable(false);
            stage.setScene(scene);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos(); // refrescar al volver
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Editar ────────────────────────────────────────────────────────────────
    @FXML
    private void clicEditar(ActionEvent event) {
        Object seleccionado = tbl_usuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un usuario de la tabla para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioTipo");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarTodos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Eliminar (baja lógica) ────────────────────────────────────────────────
    @FXML
    private void clicEliminar(ActionEvent event) {
        Object seleccionado = tbl_usuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un usuario de la tabla para eliminarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Nombre del seleccionado para el mensaje de confirmación
        String nombreMostrar = (seleccionado instanceof EmpleadoDTO e)
                ? e.getNombreCompleto()
                : (seleccionado instanceof ClienteDTO c) ? c.getNombreCompleto() : "?";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas dar de baja a " + nombreMostrar + "?\nEsta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    boolean resultado = false;

                    if (seleccionado instanceof EmpleadoDTO e) {
                        resultado = empleadoDAO.eliminar(e.getNoEmpleado());
                    } else if (seleccionado instanceof ClienteDTO c) {
                        resultado = clienteDAO.eliminar(c.getNoCliente());
                    }

                    if (resultado) {
                        UtilidadesFX.mostrarAlertaSimple("Baja exitosa",
                                nombreMostrar + " fue dado de baja correctamente.",
                                Alert.AlertType.INFORMATION);
                        cargarTodos();
                    } else {
                        UtilidadesFX.mostrarAlertaSimple("Sin cambios",
                                "No se encontró el registro o ya estaba inactivo.",
                                Alert.AlertType.WARNING);
                    }
                } catch (Exception e) {
                    UtilidadesFX.mostrarAlertaSimple("Error",
                            "No se pudo eliminar el usuario:\n" + e.getMessage(),
                            Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    // ── Helper: saber qué tipo está seleccionado ──────────────────────────────
    private boolean esEmpleado() {
        return rb_tipoEmpleado == null || rb_tipoEmpleado.isSelected();
    }

    // ── Navegación (idéntica al original) ─────────────────────────────────────
    @FXML
    private void clicUsuarios(ActionEvent event) {
        try { SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosGestion", "Productos"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
        try { SistemaPizzeria.setRoot("PedidosGestion", "Pedidos"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicValidacionInventarios(ActionEvent event) {
        try { SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try { SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("AcercaDe");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Acerca de");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
