package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InsumoFormularioController implements Initializable {

    @FXML
    private Label lbl_tituloFormulario;
    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_costo;
    @FXML
    private ComboBox<?> cb_medida;
    @FXML
    private ComboBox<?> cb_estatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void clicCancelar(ActionEvent actionEvent) {
    }

    @FXML
    public void clicGuardar(ActionEvent actionEvent) {
    }
}