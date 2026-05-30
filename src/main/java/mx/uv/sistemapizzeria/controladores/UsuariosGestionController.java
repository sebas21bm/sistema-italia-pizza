package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperOffset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import mx.uv.sistemapizzeria.modelo.dto.Persona;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

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

    @FXML
    private TableView<Persona> tbl_usuarios;
    @FXML
    private TableColumn<Persona, String> col_nombre;
    @FXML
    private TableColumn<Persona, String> col_telefono;
    @FXML
    private TableColumn<Persona, String> col_email;
    @FXML
    private TableColumn<Persona, Boolean> col_estatus;


    //TODO cambiar esto porque no serán radio buttons, sino unas combobbox

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

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ClienteDAO  clienteDAO  = new ClienteDAO();

    private ObservableList<Persona> usuarios;
    // ObservableList para combobox filtro 1
    // ObservalList para comobvox filtro 2


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTodosEmpleados();
    }

    private void configurarColumnas() {
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        col_estatus.setCellFactory(col -> new TableCell<Persona, Boolean>() {

            @Override
            protected void updateItem(Boolean estatus, boolean empty) {
                super.updateItem(estatus, empty);
                if (empty || estatus == null) {
                    setText(null);
                } else {
                    setText((estatus) ? "Activo" : "Inactivo");
                }
            }
        });
    }

    private void cargarTodosEmpleados() {
        try {
            usuarios = FXCollections.observableArrayList();
            List<EmpleadoDTO> empleadoAlmacenadosBD = empleadoDAO.mostrarTodos();
            usuarios.addAll(empleadoAlmacenadosBD);
            tbl_usuarios.setItems(usuarios);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos del inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }



    }

    @FXML
    private void clicBuscar(ActionEvent actionEvent) {
    }
    /*
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

     */

    /*
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
            // Sin radio seleccionado: buscar all
            if (obj instanceof EmpleadoDTO e)
                return e.getNombreCompleto().toLowerCase().contains(termino)
                        || (e.getTelefono() != null && e.getTelefono().contains(termino));
            if (obj instanceof ClienteDTO c)
                return c.getNombreCompleto().toLowerCase().contains(termino)
                        || (c.getTelefono() != null && c.getTelefono().contains(termino));
        }
        return false;
    }

     */


    @FXML
    private void clicFiltroTipo(ActionEvent event) {
        txt_buscar.clear();
        cargarTodosEmpleados();
    }

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
            cargarTodosEmpleados(); // refrescar al volver
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            cargarTodosEmpleados();
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
                        cargarTodosEmpleados();
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


    // NAVEGACIÓN
    @FXML
    private void clicUsuarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion", "Productos");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicValidacionInventarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try {
            SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
