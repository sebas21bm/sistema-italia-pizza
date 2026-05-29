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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clicEmpleado(ActionEvent event) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/mx/uv/sistemapizzeria/UsuarioEmpleadoOperaciones.fxml"));
            Parent raiz = cargador.load();

            Stage escenarioModal = new Stage();
            escenarioModal.setTitle("Registrar / Editar Empleado");
            escenarioModal.setScene(new Scene(raiz));

            escenarioModal.initModality(Modality.APPLICATION_MODAL);

            Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            //escenarioModal.initOwner(escenarioActual);

            escenarioActual.close();

            escenarioModal.showAndWait();

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
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/mx/uv/sistemapizzeria/UsuarioClienteOperaciones.fxml"));
            Parent raiz = cargador.load();

            Stage escenarioModal = new Stage();
            escenarioModal.setTitle("Registrar / Editar Cliente");
            escenarioModal.setScene(new Scene(raiz));

            escenarioModal.initModality(Modality.APPLICATION_MODAL);

            Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            //escenarioModal.initOwner(escenarioActual);

            escenarioActual.close();

            escenarioModal.showAndWait();

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
