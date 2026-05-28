package mx.uv.sistemapizzeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import java.io.IOException;
import java.util.HashMap;

/**
 * JavaFX App
 */
public class SistemaPizzeria extends Application {

    private static Stage primaryStage;
    private static Scene scene;
    
    private static HashMap<String,Object> metadatos; 

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

    public static void setMetadatos(String nombre, Object valor){
        if(metadatos == null){
            metadatos = new HashMap<>();
        }
        metadatos.put(nombre, valor);
    }

    public static Object getMetadatos(String nombre){
        if(metadatos == null){
            return null;
        }
        return metadatos.get(nombre);
    }
    
    public static void setRoot(String fxml, String titulo) throws IOException {
        scene.setRoot(UtilidadesFX.cargarFXML(fxml).load());
        primaryStage.setTitle(titulo);
        //primaryStage.setMaximized(true);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();

    }

    public static void main(String[] args) {
        launch();
    }

}