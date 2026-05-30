/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.ReporteInventarioDAO;
import mx.uv.sistemapizzeria.modelo.dto.DetalleReporteDTO;
import mx.uv.sistemapizzeria.modelo.dto.ReporteInventarioDTO;
import mx.uv.sistemapizzeria.utilidades.ExportadorPDFGeneral;
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

    private ObservableList<DetalleReporteDTO> detallesReporte;
    private ReporteInventarioDAO reporteInventarioDAO = new ReporteInventarioDAO();
    private boolean validacionCalculada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionDetalleProductosInventario();
    }

    private void configurarTabla() {
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        col_productoInventario.setCellValueFactory(new PropertyValueFactory<>("descripcionProductoInventario"));
        col_existencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));
        col_conteoFisicoReal.setCellValueFactory(new PropertyValueFactory<>("conteoFisico"));
        col_diferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));

        col_conteoFisicoReal.setCellFactory(col -> new TableCell<DetalleReporteDTO, Double>() {
            private final TextField txt_conteo = new TextField();
            private boolean actualizandoTexto = false;

            {
                txt_conteo.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
                    if (actualizandoTexto || validacionCalculada) {
                        return;
                    }

                    DetalleReporteDTO detalle = getTableRow() != null ? getTableRow().getItem() : null;

                    if (detalle == null) {
                        return;
                    }

                    if (!valorNuevo.matches("\\d*(\\.\\d*)?")) {
                        colocarTexto(valorAnterior);
                        return;
                    }

                    if (valorNuevo.isBlank() || ".".equals(valorNuevo)) {
                        detalle.setConteoFisico(0.0);
                        return;
                    }

                    detalle.setConteoFisico(Double.parseDouble(valorNuevo));
                });
            }

            @Override
            protected void updateItem(Double conteo, boolean empty) {
                super.updateItem(conteo, empty);

                DetalleReporteDTO detalle = getTableRow() != null ? getTableRow().getItem() : null;

                if (empty || detalle == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                colocarTexto(formatearNumero(detalle.getConteoFisico()));
                txt_conteo.setEditable(!validacionCalculada);
                txt_conteo.setFocusTraversable(!validacionCalculada);

                setGraphic(txt_conteo);
                setText(null);
            }

            private void colocarTexto(String texto) {
                actualizandoTexto = true;
                txt_conteo.setText(texto);
                actualizandoTexto = false;
            }
        });
    }

    private void configurarColumnaJustificacion() {
        boolean existeColumna = tbl_validacionInsumos.getColumns()
                .stream()
                .anyMatch(col -> "Justificación".equals(col.getText()));

        if (existeColumna) {
            return;
        }

        TableColumn<DetalleReporteDTO, String> col_justificacion = new TableColumn<>("Justificación");

        col_justificacion.setCellValueFactory(new PropertyValueFactory<>("justificacion"));
        col_justificacion.setPrefWidth(220);

        col_justificacion.setCellFactory(col -> new TableCell<DetalleReporteDTO, String>() {
            private final TextArea txt_justificacion = new TextArea();
            private boolean actualizandoTexto = false;

            {
                txt_justificacion.setWrapText(true);

                txt_justificacion.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
                    if (actualizandoTexto) {
                        return;
                    }

                    DetalleReporteDTO detalle = getTableRow() != null ? getTableRow().getItem() : null;

                    if (detalle == null || !requiereJustificacion(detalle)) {
                        return;
                    }

                    detalle.setJustificacion(valorNuevo);
                });
            }

            @Override
            protected void updateItem(String justificacion, boolean empty) {
                super.updateItem(justificacion, empty);

                DetalleReporteDTO detalle = getTableRow() != null ? getTableRow().getItem() : null;

                if (empty || detalle == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                boolean requiereJustificacion = requiereJustificacion(detalle);

                if (!requiereJustificacion) {
                    detalle.setJustificacion("");
                }

                colocarTexto(requiereJustificacion && detalle.getJustificacion() != null
                        ? detalle.getJustificacion()
                        : "");

                txt_justificacion.setEditable(requiereJustificacion);
                txt_justificacion.setDisable(!requiereJustificacion);

                setGraphic(txt_justificacion);
                setText(null);
            }

            private void colocarTexto(String texto) {
                actualizandoTexto = true;
                txt_justificacion.setText(texto);
                actualizandoTexto = false;
            }
        });

        tbl_validacionInsumos.getColumns().add(col_justificacion);
        tbl_validacionInsumos.setPrefWidth(750);

    }

    private boolean requiereJustificacion(DetalleReporteDTO detalle) {
        Double diferencia = detalle.getDiferencia();
        return diferencia != null && Double.compare(diferencia, 0.0) != 0;
    }

    private String formatearNumero(Double valor) {
        if (valor == null) {
            return "";
        }

        if (valor % 1 == 0) {
            return String.valueOf(valor.intValue());
        }

        return String.valueOf(valor);
    }

    private void cargarInformacionDetalleProductosInventario(){
        try {
            detallesReporte = FXCollections.observableArrayList();
            List<DetalleReporteDTO> detallesReporteBD = reporteInventarioDAO.mostrarTodos();
            detallesReporte.addAll(detallesReporteBD);
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
    }

    private List<DetalleReporteDTO> recuperarDatos() {
        return List.copyOf(tbl_validacionInsumos.getItems());
    }


    @FXML
    private void clicCalcularDiferencia(ActionEvent event) {
        for (DetalleReporteDTO detalle : tbl_validacionInsumos.getItems()) {
            Double conteoFisico = detalle.getConteoFisico();
            Double existencias = Double.valueOf(detalle.getExistencias());

            conteoFisico = conteoFisico != null ? conteoFisico : 0.0;
            existencias = existencias != null ? existencias : 0.0;

            Double diferencia = conteoFisico - existencias;
            detalle.setDiferencia(diferencia);

            if (Double.compare(diferencia, 0.0) == 0) {
                detalle.setJustificacion("");
            }
        }

        validacionCalculada = true;
        configurarColumnaJustificacion();
        tbl_validacionInsumos.refresh();
    }

    @FXML
    private void clicGenerarReportePdf(ActionEvent event) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar a PDF");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );


        selector.setInitialFileName("ValidacionInventario.pdf");
        Stage stageactual = (Stage) tbl_validacionInsumos.getScene().getWindow();

        File archivo = selector.showSaveDialog(stageactual);

        if (archivo == null) {
            return;
        }
        try {
            ExportadorPDFGeneral.generarReporteAValidar(archivo.getAbsolutePath(),recuperarDatos());

            //Verificación
            File pdf = new File(archivo.getAbsolutePath());
            if(pdf.exists()){
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdf);
            }
        } catch (IOException ex) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Exportar PDF",
                    "No se pueden exportar los datos a PDF",
                    Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void clicGuardarValidacion(ActionEvent event) {
        try {
            if (reporteInventarioDAO.registrarDetalle(recuperarDatos())){
                UtilidadesFX.mostrarAlertaSimple("Registro exitoso",
                        "Se ha registrado la validación del inventario correctamente",
                        Alert.AlertType.INFORMATION);
                SistemaPizzeria.setRoot("ProductosInventarioValidacion", "Validación de inventario");
            } else {
                UtilidadesFX.mostrarAlertaSimple("Falló registro",
                        "El registro de la validación del inventario no pudo realizarse," +
                                "intente de nuevo",
                        Alert.AlertType.WARNING);
            }
        }catch(SQLException e){
            UtilidadesFX.mostrarAlertaSimple("Error al registrar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }catch(NullPointerException | ClassNotFoundException | IOException n){
            UtilidadesFX.mostrarAlertaSimple("Error al cargar los datos de la validación del inventario",
                    MSJ_ERROR_CARGA_DATOS,
                    Alert.AlertType.ERROR);
    }
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
