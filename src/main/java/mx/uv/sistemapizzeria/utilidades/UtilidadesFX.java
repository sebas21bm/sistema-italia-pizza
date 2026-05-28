package mx.uv.sistemapizzeria.utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import mx.uv.sistemapizzeria.SistemaPizzeria;

public class UtilidadesFX {

    public static FXMLLoader cargarFXML(String fxml) {
        return new FXMLLoader(SistemaPizzeria.class.getResource(fxml + ".fxml"));
    }

    public static void mostrarAlertaSimple(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
