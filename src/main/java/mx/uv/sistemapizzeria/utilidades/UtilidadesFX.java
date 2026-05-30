package mx.uv.sistemapizzeria.utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.uv.sistemapizzeria.SistemaPizzeria;

import java.util.Optional;

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

    public static boolean mostrarAlertaConfirmacion(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);

        Optional<ButtonType> resultado = alert.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
