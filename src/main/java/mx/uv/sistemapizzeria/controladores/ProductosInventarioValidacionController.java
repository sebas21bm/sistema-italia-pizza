/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ReporteInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.DetalleReporteDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ReporteInventarioDTO;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

import static mx.uv.sistemapizzeria.utilidades.Constantes.MSJ_ERROR_CARGA_DATOS;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class ProductosInventarioValidacionController implements Initializable {

    @FXML
    private TableView<DetalleReporteDTO> tbl_validacionInsumos;
    @FXML
    private TableColumn col_codigo;
    @FXML
    private TableColumn col_existencias;
    @FXML
    private TableColumn col_conteoFisicoReal;
    @FXML
    private TableColumn col_diferencia;
    @FXML
    private TableColumn col_productoInventario;
    @FXML
    private TableColumn col_justificacion;

    private ObservableList<DetalleReporteDTO> detallesReporte;
    private ReporteInventarioDAO reporteInventarioDAO = new ReporteInventarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionDetalleProductosInventario();
    }

    private void configurarTabla(){
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_productoInventario.setCellValueFactory(new PropertyValueFactory<>("descripcionProductoInventario"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        col_conteoFisicoReal.setCellValueFactory(new PropertyValueFactory<>("conteoFisico"));
        col_diferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));
        col_justificacion.setCellValueFactory(new PropertyValueFactory<>("justificacion"));

        //col_conteoFisicoReal;

        col_justificacion.setCellFactory(col -> new TableCell<ReporteInventarioDTO, String>() {
            private final TextArea txt_justificacion= new TextArea();

            @Override
            protected void updateItem(String justificacion, boolean empty){
                super.updateItem(justificacion, empty);


            }
        });
    }

    private void cargarInformacionDetalleProductosInventario(){

        /*try {
            detallesReporte = FXCollections.observableArrayList();
            //List<DetalleReporteDTO> detallesReporteBD = reporteInventarioDAO.mostrarTodos();
            //detallesReporte.addAll(detallesReporteBD);
            tbl_validacionInsumos.setItems(detallesReporte);

        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos al inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
        */

    }

    @FXML
    private void clicGenerarReportePdf(ActionEvent event) {

    }

    @FXML
    private void clicGuardarValidacion(ActionEvent event) {

    }


    //NAVEGACION MENÚ
    @FXML
    private void clicUsuarios(ActionEvent event) {
        try{
            SistemaPizzeria.setRoot("UsuariosGestion","Usuarios");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion","Productos");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion","Productos de Inventario");
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    private void clicValidacionInventarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de Inventario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        SistemaPizzeria.setMetadatos("empleado", null);
        try {
            SistemaPizzeria.setRoot("InicioSesion","Sistema Pizzeria - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
        try {
            FXMLLoader loader = UtilidadesFX.cargarFXML("AcercaDe");
            Parent vista = loader.load();
            Scene escena = new Scene(vista);

            Stage stage = new Stage();
            stage.setTitle("AcercaDe");
            stage.setResizable(false);
            stage.setScene(escena);

            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
