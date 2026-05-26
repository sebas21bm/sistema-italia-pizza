package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UsuariosListadoController implements Initializable {

    @FXML
    private TextField txt_buscar;
    @FXML
    private ComboBox<?> cb_filtroEstatus;
    @FXML
    private ComboBox<?> cb_filtroTipo;
    @FXML
    private TableView<?> tbl_usuarios;
    @FXML
    private TableColumn<?, ?> col_nombre;
    @FXML
    private TableColumn<?, ?> col_telefono;
    @FXML
    private TableColumn<?, ?> col_email;
    @FXML
    private TableColumn<?, ?> col_direccion;
    @FXML
    private TableColumn<?, ?> col_estatus;
    @FXML
    private TableColumn<?, ?> col_tipo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
    }

    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicNuevoUsuario(ActionEvent event) {
    }

}