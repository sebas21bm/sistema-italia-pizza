/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemapizzeria.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dao.PedidosDAO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.utilidades.ExportadorPDF;
import mx.uv.sistemapizzeria.utilidades.UtilidadesFX;

/**
 * FXML Controller class
 *
 * @author macol
 */
public class PedidosGestionController implements Initializable {

    @FXML
    private AnchorPane pnl_menuLateral;
    @FXML
    private ImageView img_logo;
    @FXML
    private Accordion ac_menu;
    @FXML
    private TitledPane tp_administracion;
    @FXML
    private TitledPane tp_inventarios;
    @FXML
    private TitledPane tp_pedidos;
    @FXML
    private AnchorPane pnl_contenido;
    @FXML
    private HBox hbox_busqueda;
    @FXML
    private TextField txt_buscar;
    @FXML
    private TitledPane tp_filtroEstatus;
    @FXML
    private RadioButton rb_enProceso;
    @FXML
    private ToggleGroup tg_estatus;
    @FXML
    private RadioButton rb_entregado;
    @FXML
    private RadioButton rb_cancelado;
    @FXML
    private TableView<?> tbl_pedidos;
    @FXML
    private TableColumn<?, ?> col_folio;
    @FXML
    private TableColumn<?, ?> col_cliente;
    @FXML
    private TableColumn<?, ?> col_fecha;
    @FXML
    private TableColumn<?, ?> col_total;
    @FXML
    private TableColumn<?, ?> col_estatus;
    @FXML
    private TableColumn<?, ?> col_atiende;
    @FXML
    private Button btn_buscar;
    @FXML
    private Button btn_nuevoPedido;
    @FXML
    private Button btn_editar;
    @FXML
    private Button btn_cancelarPedido;
    @FXML
    private Button btn_exportarPDF;
    @FXML
    private Button btn_exportarCSV;
    @FXML
    private Button btn_menuUsuarios;
    @FXML
    private Button btn_menuProductos;
    @FXML
    private Button btn_menuProductosInventario;
    @FXML
    private Button btn_menuValidacionInventarios;
    @FXML
    private Button btn_menuPedidos;
    @FXML
    private Button btn_cerrarSesion;
    @FXML
    private Button btn_ayudaAcercaDe;

    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    @FXML
    private void clicNuevoPedido(ActionEvent event) {
    }

    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
    }

    @FXML
    private void clicExportarPDF(ActionEvent event) {
        // 1. Obtener la lista de pedidos actualmente visible en la tabla
        List<PedidoDTO> pedidos = obtenerPedidosParaExportar();
        if (pedidos == null || pedidos.isEmpty()) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Sin datos",
                    "No hay pedidos para exportar. Realiza una búsqueda primero.",
                    Alert.AlertType.WARNING);
            return;
        }

        // 2. Pedir al usuario dónde guardar el archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de pedidos");
        fileChooser.setInitialFileName("reporte_pedidos.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));

        Stage stage = (Stage) btn_exportarPDF.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) return; // el usuario canceló

        // 3. Generar el PDF
        try {
            ExportadorPDF.exportar(pedidos, archivo.getAbsolutePath());
            UtilidadesFX.mostrarAlertaSimple(
                    "Exportación exitosa",
                    "El reporte se guardó en:\n" + archivo.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error al exportar",
                    "No fue posible generar el PDF:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Devuelve los pedidos actualmente cargados en la tabla.
     * Si la tabla está vacía o aún no se ha buscado, carga todos los pedidos.
     */
    @SuppressWarnings("unchecked")
    private List<PedidoDTO> obtenerPedidosParaExportar() {
        // Si la tabla ya tiene datos (ej. resultado de una búsqueda), los usa
        if (tbl_pedidos.getItems() != null && !tbl_pedidos.getItems().isEmpty()) {
            return (List<PedidoDTO>) tbl_pedidos.getItems();
        }
        // Si no hay nada en tabla, carga todos los pedidos de la BD
        try {
            PedidosDAO dao = new PedidosDAO();
            List<PedidoDTO> todos = dao.mostrarTodos();
            // Cargar detalles de cada pedido para el PDF
            for (PedidoDTO p : todos) {
                PedidoDTO completo = dao.buscar(p.getIdPedido());
                if (completo != null) {
                    p.setDetalles(completo.getDetalles());
                }
            }
            return todos;
        } catch (Exception e) {
            UtilidadesFX.mostrarAlertaSimple(
                    "Error",
                    "No se pudieron obtener los pedidos:\n" + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void clicExportarCSV(ActionEvent event) {
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
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
