package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class UsuarioFormularioController implements Initializable {

    @FXML
    private Label lbl_tituloFormulario;
    @FXML
    private RadioButton rb_empleado;
    @FXML
    private ToggleGroup grupoTipoUsuario;
    @FXML
    private RadioButton rb_cliente;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_apellidos;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_correo;
    @FXML
    private TextField txt_calle;
    @FXML
    private TextField txt_numero;
    @FXML
    private TextField txt_codigoPostal;
    @FXML
    private VBox vbox_tipoEmpleado;
    @FXML
    private ComboBox<?> cb_tipoEmpleado;
    @FXML
    private VBox vbox_contrasenia;
    @FXML
    private PasswordField txt_contrasenia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

}