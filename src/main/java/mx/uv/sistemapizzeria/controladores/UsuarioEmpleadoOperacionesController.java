package mx.uv.sistemapizzeria.controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.EmpleadoDAO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;
import mx.uv.sistemapizzeria.utilidades.Validador;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioEmpleadoOperacionesController implements Initializable {

    @FXML
    private
    Label lb_contrasenia;
    @FXML
    private Label lb_formulario;
    @FXML
    private TextField txt_noEmpleado;
    @FXML
    private TextField txt_nombres;
    @FXML
    private TextField txt_apellidoPaterno;
    @FXML
    private TextField txt_apellidoMaterno;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_correoElectronico;
    @FXML
    private TextField txt_calle;
    @FXML
    private TextField txt_numero;
    @FXML
    private TextField txt_codigoPostal;
    @FXML
    private TextField txt_ciudad;
    @FXML
    private PasswordField psw_contrasena;
    @FXML
    private TextField     txt_contrasenaVisible;
    @FXML
    private CheckBox chkBtn_mostrar;
    @FXML
    private ComboBox<TipoEmpleado> cmb_tipoEmpleado;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    private Boolean registro;
    private EmpleadoDTO empleadoEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmb_tipoEmpleado.setItems(FXCollections.observableArrayList(TipoEmpleado.values()));

        this.registro = (Boolean) SistemaPizzeria.getMetadatos("registrar-empleado");

        txt_contrasenaVisible.promptTextProperty().bind(psw_contrasena.promptTextProperty());

        configurarCheckBoxMostrarContrasenia();

        txt_telefono.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,10}")) {
                return change;
            }
            return null;
        }));

        txt_codigoPostal.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,5}")) {
                return change;
            }
            return null;
        }));

        txt_noEmpleado.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase()); // Auto mayúscula
            if (change.getControlNewText().matches("[E]?[0-9]{0,4}")) {
                return change;
            }
            return null;
        }));

        if (registro) {
            lb_formulario.setText("Registrar empleado");
            psw_contrasena.setPromptText("Contraseña");
        } else {
            lb_formulario.setText("Editar empleado");
            txt_noEmpleado.setDisable(true);
            psw_contrasena.setVisible(false);
            txt_contrasenaVisible.setVisible(false);
            chkBtn_mostrar.setVisible(false);
            lb_contrasenia.setVisible(false);
        }
    }

    private void configurarCheckBoxMostrarContrasenia() {
        chkBtn_mostrar.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                txt_contrasenaVisible.setText(psw_contrasena.getText());
                psw_contrasena.setVisible(false);
                psw_contrasena.setManaged(false);
                txt_contrasenaVisible.setVisible(true);
                txt_contrasenaVisible.setManaged(true);
                txt_contrasenaVisible.requestFocus();
                txt_contrasenaVisible.end();
            } else {
                psw_contrasena.setText(txt_contrasenaVisible.getText());
                txt_contrasenaVisible.setVisible(false);
                txt_contrasenaVisible.setManaged(false);
                psw_contrasena.setVisible(true);
                psw_contrasena.setManaged(true);
                psw_contrasena.requestFocus();
                psw_contrasena.end();
            }
        });
    }

    public void mostrarEmpleado(EmpleadoDTO empleado) {
        this.empleadoEdicion = empleado;

        txt_noEmpleado.setText(empleado.getNoEmpleado());
        txt_nombres.setText(empleado.getNombre());
        txt_apellidoPaterno.setText(empleado.getPaterno());
        txt_apellidoMaterno.setText(empleado.getMaterno());
        txt_telefono.setText(empleado.getTelefono());
        txt_correoElectronico.setText(empleado.getEmail());
        cmb_tipoEmpleado.setValue(empleado.getTipoEmpleado());

        if (empleado.getDireccion() != null) {
            txt_calle.setText(empleado.getDireccion().getCalle());
            txt_numero.setText(empleado.getDireccion().getNumero());
            txt_codigoPostal.setText(empleado.getDireccion().getCodigoPostal());
            txt_ciudad.setText(empleado.getDireccion().getCiudad());
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        // Construir DTO con los datos del formulario
        EmpleadoDTO empleado = (!registro && empleadoEdicion != null) ? empleadoEdicion : new EmpleadoDTO();

        empleado.setNombre(txt_nombres.getText().trim());
        empleado.setPaterno(txt_apellidoPaterno.getText().trim());
        empleado.setMaterno(txt_apellidoMaterno.getText().trim());
        empleado.setTelefono(txt_telefono.getText().trim());
        empleado.setEmail(txt_correoElectronico.getText().trim());
        empleado.setTipoEmpleado(cmb_tipoEmpleado.getValue());
        empleado.setEstatus(true);

        // Dirección
        DireccionDTO dir = (!registro && empleadoEdicion != null && empleadoEdicion.getDireccion() != null)
                ? empleadoEdicion.getDireccion() : new DireccionDTO();
        dir.setCalle(txt_calle.getText().trim());
        dir.setNumero(txt_numero.getText().trim());
        dir.setCodigoPostal(txt_codigoPostal.getText().trim());
        dir.setCiudad(txt_ciudad.getText().trim());
        empleado.setDireccion(dir);

        // Contraseña (solo en registro o si se escribió algo en edición)
        String pass = psw_contrasena.isVisible()
                ? psw_contrasena.getText()
                : txt_contrasenaVisible.getText();
        if (registro) {
            // Registro: la contraseña es obligatoria
            if (pass == null || pass.isBlank()) {
                UtilidadesFX.mostrarAlertaSimple("Campo requerido",
                        "La contraseña es obligatoria para registrar un empleado.",
                        Alert.AlertType.WARNING);
                return;
            }
            empleado.setContrasenia(hashearContrasenia(pass));

            String noEmpleado = txt_noEmpleado.getText().trim().toUpperCase();
            if (noEmpleado.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Campo requerido",
                        "El número de empleado (ej. E0006) es obligatorio.",
                        Alert.AlertType.WARNING);
                return;
            }
            empleado.setNoEmpleado(noEmpleado);

            String usuario = (empleado.getNombre().charAt(0) + empleado.getPaterno())
                    .replaceAll("\\s+", "");
            empleado.setUsuario(usuario);
        } else {
            if (empleadoEdicion != null && empleadoEdicion.getUsuario() != null) {
                empleado.setUsuario(empleadoEdicion.getUsuario());
            }
        }

        // Validar
        List<String> errores = Validador.validarEmpleado(empleado);
        errores.addAll(Validador.validarDireccion(dir));

        if (!errores.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Datos inválidos",
                    Validador.formatearErrores(errores),
                    Alert.AlertType.WARNING);
            return;
        }

        String accionConfirmacion = registro ? "registrar" : "actualizar";
        boolean confirmar = UtilidadesFX.mostrarAlertaConfirmacion(
                "Confirmar acción",
                "¿Estás seguro de " + accionConfirmacion + " a este empleado?",
                "Revisa que todos los datos capturados sean correctos."
        );

        if (!confirmar) {
            return;
        }

        try {
            boolean exito;
            if (registro) {
                exito = empleadoDAO.registrar(empleado);
            } else {
                exito = empleadoDAO.editar(empleado);
            }

            if (exito) {
                String accion = registro ? "registrado" : "actualizado";
                UtilidadesFX.mostrarAlertaSimple("Éxito",
                        "El empleado fue " + accion + " correctamente.",
                        Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        }  catch (Exception e) {
            // Este horita lo modificamos, queda pendiente por el push
        e.printStackTrace();

        String mensajeOriginal = e.getMessage();

        UtilidadesFX.mostrarAlertaSimple("MySQL",
                "MySQL rechazó la edición con este mensaje exacto:\n\n" + mensajeOriginal,
                Alert.AlertType.ERROR);
    }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    private void clicMostrarContrasena(ActionEvent event) {
        // El CheckBox chkBtn_mostrar ya tiene su listener en initialize()
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txt_ciudad.getScene().getWindow();
        stage.close();
    }

    private byte[] hashearContrasenia(String contrasenia) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(contrasenia.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}