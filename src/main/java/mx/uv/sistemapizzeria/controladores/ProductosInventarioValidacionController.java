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
    private TableColumn<DetalleReporteDTO, String> col_codigo;
    @FXML
    private TableColumn<DetalleReporteDTO, Integer> col_existencias;
    @FXML
    private TableColumn<DetalleReporteDTO, Integer> col_conteoFisicoReal;
    @FXML
    private TableColumn<DetalleReporteDTO, Double> col_diferencia;
    @FXML
    private TableColumn<DetalleReporteDTO, String> col_productoInventario;
    @FXML
    private TableColumn<DetalleReporteDTO, String> col_justificacion;

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
        col_conteoFisicoReal.setCellValueFactory(new PropertyValueFactory<>("conteoFisico"));
        col_diferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));
        col_justificacion.setCellValueFactory(new PropertyValueFactory<>("justificacion"));

        // CORRECCIÓN: Extraemos de forma segura las existencias del insumo vinculado
        col_existencias.setCellValueFactory(cellData -> {
            DetalleReporteDTO fila = cellData.getValue();
            if (fila != null && fila.getInsumo() != null) {
                return new javafx.beans.property.SimpleIntegerProperty(fila.getInsumo().getExistencias()).asObject();
            }
            return new javafx.beans.property.SimpleObjectProperty<>(0);
        });

        col_justificacion.setCellFactory(col -> new TableCell<DetalleReporteDTO, String>() {
            private final TextArea txt_justificacion = new TextArea();

            @Override
            protected void updateItem(String justificacion, boolean empty){
                super.updateItem(justificacion, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    txt_justificacion.setText(justificacion);
                    setGraphic(txt_justificacion);
                }
            }
        });
    }

    private void cargarInformacionDetalleProductosInventario(){
        try {
            detallesReporte = FXCollections.observableArrayList();

            // 1. Instanciamos el DAO de inventario para obtener los insumos físicos actuales
            mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO inventarioDAO = new mx.uv.sistemapizzeria.modelo.dao.ProductoInventarioDAO();
            List<ProductoInventarioDTO> insumosBD = inventarioDAO.mostrarTodos();

            // 2. Convertimos cada producto del inventario en un renglón para la hoja de validación
            for (ProductoInventarioDTO insumo : insumosBD) {
                DetalleReporteDTO detalleFila = new DetalleReporteDTO();
                detalleFila.setCodigo(insumo.getCodigo());
                detalleFila.setDescripcionProductoInventario(insumo.getNombre());
                detalleFila.setInsumo(insumo); // Vinculamos el DTO de inventario que agregamos previamente

                // Inicializamos los valores por defecto para que el usuario empiece a auditar
                detalleFila.setConteoFisico(insumo.getExistencias()); // Sugerimos la misma cantidad inicial
                detalleFila.setDiferencia(0.0);
                detalleFila.setJustificacion("");

                detallesReporte.add(detalleFila);
            }

            tbl_validacionInsumos.setItems(detallesReporte);

        } catch(SQLException e) {
            UtilidadesFX.mostrarAlertaSimple("Error al consultar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch(NullPointerException | ClassNotFoundException | IOException n) {
            UtilidadesFX.mostrarAlertaSimple("Error al cargar productos al inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
        }
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
