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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void clicEmpleado(ActionEvent event) {
        try {
            // TODO falta agregar metadatos para que se cargue con la operacion de registrar en empleado
            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioEmpleadoOperaciones");
            Parent raiz = loader.load();
            Scene escena = new Scene(raiz);
            /*
            Stage stageConf = new Stage();
            stageConf.initOwner(stageCreacion);
            stageConf.initModality(Modality.WINDOW_MODAL);
            stageConf.setTitle("Confirmar pedido");
            stageConf.setResizable(false);
            stageConf.setScene(new Scene(vista));
            stageConf.centerOnScreen();

            stageCreacion.hide();
            stageConf.showAndWait();
             */

            Stage escenarioModal = new Stage();
            Stage  escenarioActual = (Stage) lb_titulo.getScene().getWindow();


            escenarioModal.initOwner(escenarioActual);
            escenarioModal.initModality(Modality.APPLICATION_MODAL);

            escenarioModal.setTitle("Registrar empleado");
            escenarioModal.setResizable(false);
            escenarioModal.setScene(escena);

            escenarioModal.centerOnScreen();
            escenarioActual.hide();
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
            // TODO falta agregar metadatos para que se cargue con la operacion de registrar en cliente
            FXMLLoader loader = UtilidadesFX.cargarFXML("UsuarioClienteOperaciones");
            Parent raiz = loader.load();
            Scene escena = new Scene(raiz);

            Stage escenarioModal = new Stage();
            escenarioModal.setTitle("Registrar cliente");
            escenarioModal.setResizable(false);
            escenarioModal.setScene(escena);

            Stage  escenarioActual = (Stage) lb_titulo.getScene().getWindow();

            escenarioModal.centerOnScreen();
            escenarioModal.initModality(Modality.APPLICATION_MODAL);
            escenarioModal.showAndWait();

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
    private void clicCancelar(ActionEvent event) {
        Stage escenario = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenario.close();
    }
    
}
