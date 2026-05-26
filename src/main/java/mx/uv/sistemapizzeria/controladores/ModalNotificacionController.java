package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ModalNotificacionController implements Initializable {

    @FXML
    private Label lbl_titulo;
    @FXML
    private Label lbl_mensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
    }

}