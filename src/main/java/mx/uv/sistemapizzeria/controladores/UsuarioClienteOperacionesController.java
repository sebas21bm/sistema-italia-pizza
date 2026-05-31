package mx.uv.sistemapizzeria.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ClienteDAO;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;
import mx.uv.sistemapizzeria.utilidades.Validador;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioClienteOperacionesController implements Initializable {

    @FXML
    private TextField txt_nombres1;
    @FXML
    private TextField txt_apellidoPaterno1;
    @FXML
    private TextField txt_apellidoMaterno1;
    @FXML
    private TextField txt_telefono1;
    @FXML
    private TextField txt_correoElectronico1;
    @FXML
    private TextField txt_calle1;
    @FXML
    private TextField txt_codigoPostal1;
    @FXML
    private TextField txt_numero1;
    @FXML
    private TextField txt_ciudad1;
    @FXML
    private Button btn_agregarDireccion1;
    @FXML
    private Button btn_cancelar1;
    @FXML
    private Button btn_guardar1;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    private List<DireccionDTO> direccionesAcumuladas = new ArrayList<>();

    private Boolean registro;
    private ClienteDTO clienteEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.registro = Boolean.TRUE.equals(SistemaPizzeria.getMetadatos("registrar-cliente"));
    }

    public void mostrarCliente(ClienteDTO cliente) {
        this.clienteEdicion = cliente;

        txt_nombres1.setText(cliente.getNombre());
        txt_apellidoPaterno1.setText(cliente.getPaterno());
        txt_apellidoMaterno1.setText(cliente.getMaterno());
        txt_telefono1.setText(cliente.getTelefono());
        txt_correoElectronico1.setText(cliente.getEmail());

        if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
            DireccionDTO primera = cliente.getDirecciones().get(0);
            txt_calle1.setText(primera.getCalle());
            txt_numero1.setText(primera.getNumero());
            txt_codigoPostal1.setText(primera.getCodigoPostal());
            txt_ciudad1.setText(primera.getCiudad());
            direccionesAcumuladas.addAll(cliente.getDirecciones());
        }
        actualizarLabelDirecciones();
    }

    @FXML
    private void clicAgregarDireccion(ActionEvent event) {
        String calle = txt_calle1.getText().trim();
        String numero = txt_numero1.getText().trim();
        String cp = txt_codigoPostal1.getText().trim();
        String ciudad = txt_ciudad1.getText().trim();

        DireccionDTO dir = new DireccionDTO(0, calle, numero, cp, ciudad);
        List<String> errores = Validador.validarDireccion(dir);

        if (!errores.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Dirección inválida",
                    Validador.formatearErrores(errores),
                    Alert.AlertType.WARNING);
            return;
        }

        if (!registro && !direccionesAcumuladas.isEmpty()) {
            DireccionDTO primera = direccionesAcumuladas.get(0);
            primera.setCalle(calle);
            primera.setNumero(numero);
            primera.setCodigoPostal(cp);
            primera.setCiudad(ciudad);
        } else {
            direccionesAcumuladas.add(dir);
        }

        txt_calle1.clear();
        txt_numero1.clear();
        txt_codigoPostal1.clear();
        txt_ciudad1.clear();
        actualizarLabelDirecciones();

        UtilidadesFX.mostrarAlertaSimple("Dirección agregada",
                "Dirección guardada. Total: " + direccionesAcumuladas.size(),
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String calle = txt_calle1.getText().trim();
        String numero = txt_numero1.getText().trim();
        String cp = txt_codigoPostal1.getText().trim();
        String ciudad = txt_ciudad1.getText().trim();

        if (!calle.isEmpty() || !numero.isEmpty() || !cp.isEmpty() || !ciudad.isEmpty()) {
            DireccionDTO dirPendiente = new DireccionDTO(0, calle, numero, cp, ciudad);
            List<String> errDir = Validador.validarDireccion(dirPendiente);
            if (!errDir.isEmpty()) {
                UtilidadesFX.mostrarAlertaSimple("Dirección inválida",
                        "Completa o corrige la dirección antes de guardar:\n" +
                                Validador.formatearErrores(errDir),
                        Alert.AlertType.WARNING);
                return;
            }
            if (!registro && !direccionesAcumuladas.isEmpty()) {
                DireccionDTO primera = direccionesAcumuladas.get(0);
                primera.setCalle(calle);
                primera.setNumero(numero);
                primera.setCodigoPostal(cp);
                primera.setCiudad(ciudad);
            } else {
                direccionesAcumuladas.add(dirPendiente);
            }
        }

        if (direccionesAcumuladas.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Dirección requerida",
                    "Debes agregar al menos una dirección.",
                    Alert.AlertType.WARNING);
            return;
        }

        ClienteDTO cliente = (!registro && clienteEdicion != null) ? clienteEdicion : new ClienteDTO();
        cliente.setNombre(txt_nombres1.getText().trim());
        cliente.setPaterno(txt_apellidoPaterno1.getText().trim());
        cliente.setMaterno(txt_apellidoMaterno1.getText().trim());
        cliente.setTelefono(txt_telefono1.getText().trim());
        cliente.setEmail(txt_correoElectronico1.getText().trim());
        cliente.setEstatus(true);
        cliente.setDirecciones(direccionesAcumuladas);

        List<String> errores = Validador.validarCliente(cliente);
        if (!errores.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple("Datos inválidos",
                    Validador.formatearErrores(errores),
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean exito;
            if (registro) {
                exito = clienteDAO.registrar(cliente);
            } else {
                exito = clienteDAO.editar(cliente);
            }

            if (exito) {
                String accion = registro ? "registrado" : "actualizado";
                UtilidadesFX.mostrarAlertaSimple("Éxito",
                        "El cliente fue " + accion + " correctamente.",
                        Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple("Error al guardar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar1.getScene().getWindow();
        stage.close();
    }

    // TODO añadir tabla para que muestre el acumulado
    private void actualizarLabelDirecciones() {
        int total = direccionesAcumuladas.size();
        btn_agregarDireccion1.setText(total == 0
                ? "+   Agregar Dirección"
                : "+   Agregar Dirección (" + total + ")");
    }
}