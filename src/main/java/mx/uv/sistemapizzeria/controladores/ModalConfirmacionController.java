package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ModalConfirmacionController implements Initializable {

    @FXML
    private Label lbl_titulo;
    @FXML
    private Label lbl_mensaje;
    @FXML
    private Button btn_confirmar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicConfirmar(ActionEvent event) {
    }

}