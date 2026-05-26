package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductosListadoController implements Initializable {

    @FXML
    private TextField txt_buscarProducto;
    @FXML
    private TableView<?> tbl_productos;
    @FXML
    private TableColumn<?, ?> col_codigo;
    @FXML
    private TableColumn<?, ?> col_foto;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_precio;
    @FXML
    private TableColumn<?, ?> col_existencias;
    @FXML
    private TableColumn<?, ?> col_estatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicEliminarProducto(ActionEvent event) {
    }

    @FXML
    private void clicEditarProducto(ActionEvent event) {
    }

    @FXML
    private void clicNuevoProducto(ActionEvent event) {
    }

}