package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ClienteDAO;
import mx.uv.sistemapizzeria.modelo.dao.EmpleadoDAO;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

public class UsuariosGestionController implements Initializable {

    @FXML
    private TextField txt_buscar;

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
    @FXML
    private ComboBox<String> cb_filtro;
    @FXML
    private ComboBox<String> cb_tipo;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ClienteDAO  clienteDAO  = new ClienteDAO();

    private ObservableList<Persona> usuarios;
    private final ObservableList<String> filtros = FXCollections.observableArrayList(
            "Por nombre", "Por telefono", "Por direccion", "Ver todos");
    private final ObservableList<String> tipos = FXCollections.observableArrayList(
            "Empleado", "Cliente");

    private String filtroBusqueda = "";
    private String filtroTipo = "Empleado";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_filtro.setItems(filtros);
        cb_tipo.setItems(tipos);
        cb_tipo.setValue(tipos.get(0));
        configurarColumnas();
        configurarSeleccionTipo();
        configurarSeleccionFiltro();
        cargarTodosEmpleados();
        filtroTipo = "Empleado";
    }

    private void configurarColumnas() {
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        col_estatus.setCellFactory(col -> new TableCell<Persona, Boolean>() {
            @Override
            protected void updateItem(Boolean estatus, boolean empty) {
                super.updateItem(estatus, empty);
                setText(empty || estatus == null ? null : (estatus ? "Activo" : "Inactivo"));
            }
        });
    }

    private void configurarSeleccionTipo() {
        cb_tipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            txt_buscar.setText("");
            filtroBusqueda = "";
            filtroTipo = newVal;
            actualizarInformacion();
        });
    }

    private void configurarSeleccionFiltro() {
        cb_filtro.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.equals("Ver todos")) {
                txt_buscar.setText("");
                filtroBusqueda = "";
                actualizarInformacion();
            } else {
                filtroBusqueda = newVal;
            }
        });
    }

    private void actualizarInformacion() {
        if (filtroTipo.equals("Empleado")){
            cargarTodosEmpleados();
        } else {
            cargarTodosClientes();
        }
    }

    private void cargarTodosEmpleados() {
        try {
            usuarios = FXCollections.observableArrayList();
            List<EmpleadoDTO> lista = empleadoDAO.mostrarTodos();
            usuarios.addAll(lista);
            tbl_usuarios.setItems(usuarios);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar empleados", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    private void cargarTodosClientes() {
        try {
            usuarios = FXCollections.observableArrayList();
            List<ClienteDTO> lista = clienteDAO.mostrarTodos();
            usuarios.addAll(lista);
            tbl_usuarios.setItems(usuarios);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar clientes", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String campoBusqueda = txt_buscar.getText();

        if (campoBusqueda == null || campoBusqueda.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin nombre",
                    "Por favor ingresa un nombre de cliente para buscar.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (filtroBusqueda.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Sin filtro",
                    "Por favor selecciona un filtro para realizar la búsqueda.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            usuarios = FXCollections.observableArrayList();
            usuarios.addAll(buscarPorFiltro(campoBusqueda));
            tbl_usuarios.setItems(usuarios);
        } catch (SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException | ClassNotFoundException | IOException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al buscar", MSJ_ERROR_CARGA_DATOS, Alert.AlertType.ERROR);
        }
    }

    private List<? extends Persona> buscarPorFiltro(String campoBusqueda)
            throws SQLException, ClassNotFoundException, IOException, NullPointerException {
        if (filtroTipo.equals("Empleado")) {
            return switch (filtroBusqueda) {
                case "Por nombre" -> empleadoDAO.buscarPorNombre(campoBusqueda);
                case "Por telefono" -> empleadoDAO.buscarPorTelefono(campoBusqueda);
                default -> empleadoDAO.buscarPorDireccion(campoBusqueda);
            };
        } else {
            return switch (filtroBusqueda) {
                case "Por nombre" -> clienteDAO.buscarPorNombre(campoBusqueda);
                case "Por telefono" -> clienteDAO.buscarPorTelefono(campoBusqueda);
                default -> clienteDAO.buscarPorDireccion(campoBusqueda);
            };
        }
    }

    @FXML
    private void clicNuevoUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioTipo");
            Parent vista = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setResizable(false);
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            actualizarInformacion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Persona seleccionado = tbl_usuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un usuario de la tabla para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            if (seleccionado instanceof EmpleadoDTO empleado) {
                SistemaPizzeria.setMetadatos("registrar-empleado", false);
                FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioEmpleadoOperaciones");
                Parent vista = loader.load();
                UsuarioEmpleadoOperacionesController ctrl = loader.getController();
                ctrl.mostrarEmpleado(empleado);

                Stage stage = new Stage();
                stage.setTitle("Editar Empleado");
                stage.setResizable(false);
                stage.setScene(new Scene(vista));
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } else if (seleccionado instanceof ClienteDTO cliente) {
                SistemaPizzeria.setMetadatos("registrar-cliente", false);
                FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioClienteOperaciones");
                Parent vista = loader.load();
                UsuarioClienteOperacionesController ctrl = loader.getController();
                ctrl.mostrarCliente(cliente);

                Stage stage = new Stage();
                stage.setTitle("Editar Cliente");
                stage.setResizable(false);
                stage.setScene(new Scene(vista));
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }

            actualizarInformacion();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Persona seleccionado = tbl_usuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilidadesFX.mostrarAlertaSimple("Selección requerida",
                    "Selecciona un usuario de la tabla para eliminarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        String nombreMostrar = seleccionado.getNombreCompleto();

        if (seleccionado instanceof EmpleadoDTO empleado) {
            if (empleado.getNoEmpleado().equals(Sesion.empleadoSesion.getNoEmpleado())) {
                UtilidadesFX.mostrarAlertaSimple("Operación no permitida",
                        "No puedes dar de baja al usuario con sesión activa.",
                        Alert.AlertType.WARNING);
                return;
            }
        }

        if (!seleccionado.getEstatus()) {
            UtilidadesFX.mostrarAlertaSimple("Sin cambios",
                    "El usuario ya se encuentra eliminado.",
                    Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar eliminación",
                "¿Estás seguro que deseas eliminar a: " + seleccionado.getNombreCompleto() +  "?",
                null
        );

        if (confirmado) {
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
                }
            } catch (SQLException | IOException | ClassNotFoundException e) {
                UtilidadesFX.mostrarAlertaSimple("Error",
                        "No se pudo eliminar el usuario:\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }
        actualizarInformacion();
    }

    // Navegacion
    @FXML private void clicUsuarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion", "Productos");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicValidacionInventarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try {
            SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void clicAyudaAcercaDe(ActionEvent event) {
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
        } catch (IOException e) { e.printStackTrace(); }
    }
}