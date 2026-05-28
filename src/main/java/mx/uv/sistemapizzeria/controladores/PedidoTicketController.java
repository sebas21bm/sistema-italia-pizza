/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class PedidoTicketController implements Initializable {

    @FXML
    private Label lbl_fechaHora;
    @FXML
    private Label lbl_empleado;
    @FXML
    private Label lbl_cliente;
    @FXML
    private Label lbl_direccion;
    @FXML
    private Label lbl_telefono;
    @FXML
    private TableView<?> tbl_productosTicket;
    @FXML
    private TableColumn<?, ?> col_cantidad;
    @FXML
    private TableColumn<?, ?> col_descripcion;
    @FXML
    private TableColumn<?, ?> col_precioUnitario;
    @FXML
    private TableColumn<?, ?> col_subtotal;
    @FXML
    private Label lbl_totalPagar;
    @FXML
    private Label lbl_numeroPedido;
    @FXML
    private Label lbl_estadoPedido;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clicAceptar(ActionEvent event) {
    }
    
}
