package mx.uv.sistemapizzeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import java.io.IOException;

/**
 * JavaFX App
 */
public class SistemaPizzeria extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Sistema Pizzeria - Login");
        primaryStage.setResizable(false);

        FXMLLoader loader = UtilidadesFX.cargarFXML("InicioSesion");
        Parent vista = loader.load();
        scene = new Scene(vista);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        //scene.setRoot(loadFXML(fxml));
        scene.setRoot(UtilidadesFX.cargarFXML(fxml).load());
    }

//    private static Parent loadFXML(String fxml) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(SistemaPizzeria.class.getResource(fxml + ".fxml"));
//        return fxmlLoader.load();
//    }

    public static void main(String[] args) {
        launch();
    }

}