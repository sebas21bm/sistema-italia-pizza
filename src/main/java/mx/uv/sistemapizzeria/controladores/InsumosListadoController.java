package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class InsumosListadoController implements Initializable {

    @FXML
    private TextField txt_buscarInsumo;
    @FXML
    private TableView<?> tbl_insumos;
    @FXML
    private TableColumn<?, ?> col_codigo;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_medida;
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
    private void clicEliminarInsumo(ActionEvent event) {
    }

    @FXML
    private void clicEditarInsumo(ActionEvent event) {
    }

    @FXML
    private void clicNuevoInsumo(ActionEvent event) {
    }

}