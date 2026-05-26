package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController implements Initializable {

    @FXML
    private Label lbl_nombreUsuario;
    @FXML
    private Label lbl_rolUsuario;
    @FXML
    private Label lbl_hora;
    @FXML
    private Label lbl_fecha;
    @FXML
    private StackPane panelContenido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicMenuUsuarios(ActionEvent event) {
    }

    @FXML
    private void clicMenuCerrarSesion(ActionEvent event) {
    }

    @FXML
    private void clicMenuProductos(ActionEvent event) {
    }

    @FXML
    private void clicMenuInsumos(ActionEvent event) {
    }

    @FXML
    private void clicMenuValidacion(ActionEvent event) {
    }

    @FXML
    private void clicMenuPedidos(ActionEvent event) {
    }

    @FXML
    private void clicMenuAyuda(ActionEvent event) {
    }

}