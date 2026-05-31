/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class UsuarioTipoController implements Initializable {

    @FXML
    private Label lb_titulo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void clicEmpleado(ActionEvent event) {
        try {
            SistemaPizzeria.setMetadatos("registrar-empleado", true);

            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioEmpleadoOperaciones");
            Parent raiz = loader.load();

            Stage escenarioActual = (Stage) lb_titulo.getScene().getWindow();

            Stage escenarioModal = new Stage();
            escenarioModal.initModality(Modality.APPLICATION_MODAL);
            escenarioModal.setTitle("Registrar Empleado");
            escenarioModal.setResizable(false);
            escenarioModal.setScene(new Scene(raiz));
            escenarioModal.centerOnScreen();
            escenarioModal.show();

            escenarioActual.close();

        } catch (IOException e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error del Sistema",
                    "No se pudo cargar la interfaz de operaciones del empleado.",
                    Alert.AlertType.ERROR
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCliente(ActionEvent event) {
        try {
            SistemaPizzeria.setMetadatos("registrar-cliente", true);

            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioClienteOperaciones");
            Parent raiz = loader.load();

            Stage escenarioActual = (Stage) lb_titulo.getScene().getWindow();

            Stage escenarioModal = new Stage();
            escenarioModal.initModality(Modality.APPLICATION_MODAL);
            escenarioModal.setTitle("Registrar Cliente");
            escenarioModal.setResizable(false);
            escenarioModal.setScene(new Scene(raiz));
            escenarioModal.centerOnScreen();
            escenarioModal.show();

            escenarioActual.close();

        } catch (IOException e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error del Sistema",
                    "No se pudo cargar la interfaz de operaciones del cliente.",
                    Alert.AlertType.ERROR
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Stage escenario = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenario.close();
    }

}