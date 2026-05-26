package mx.uv.sistemapizzeria.utilidades;

import javafx.fxml.FXMLLoader;
import mx.uv.sistemapizzeria.SistemaPizzeria;

public class UtilidadesFX {

    public static FXMLLoader cargarFXML(String fxml) {
        return new FXMLLoader(SistemaPizzeria.class.getResource(fxml + ".fxml"));
    }
}
