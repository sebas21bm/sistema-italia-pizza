package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductoFormularioController implements Initializable {

    @FXML
    private Label lbl_tituloFormulario;
    @FXML
    private TextField txt_codigo;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_descripcion;
    @FXML
    private TextField txt_precio;
    @FXML
    private TextField txt_restricciones;
    @FXML
    private TextField txt_cantidad;
    @FXML
    private TextField txt_rutaFoto;
    @FXML
    private ComboBox<?> cb_insumo;
    @FXML
    private TextField txt_cantidadInsumo;
    @FXML
    private TableView<?> tbl_receta;
    @FXML
    private TableColumn<?, ?> col_recetaInsumo;
    @FXML
    private TableColumn<?, ?> col_recetaCantidad;
    @FXML
    private TableColumn<?, ?> col_recetaMedida;
    @FXML
    private TableColumn<?, ?> col_recetaAccion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void clicExaminarFoto(ActionEvent event) {
    }

    @FXML
    private void clicAgregarInsumo(ActionEvent event) {
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
    }

}