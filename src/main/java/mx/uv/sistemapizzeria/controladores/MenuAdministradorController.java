package mx.uv.sistemapizzeria.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import mx.uv.sistemapizzeria.SistemaPizzeria;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;

public class MenuAdministradorController implements Initializable {

    @FXML private Label lbl_nombreUsuario;
    @FXML private Label lbl_rolUsuario;
    @FXML private AnchorPane pnl_menuLateral;
    @FXML private ImageView img_logo;
    @FXML private Accordion ac_menu;
    @FXML private TitledPane tp_administracion;
    @FXML private Button btn_menuUsuarios;
    @FXML private TitledPane tp_inventarios;
    @FXML private Button btn_menuProductos;
    @FXML private Button btn_menuValidacionInventarios;
    @FXML private TitledPane tp_pedidos;
    @FXML private Button btn_menuPedidos;
    @FXML private Button btn_cerrarSesion;
    @FXML private Button btn_ayudaAcercaDe;
    @FXML private AnchorPane pnl_contenido;
    @FXML private AnchorPane pnl_encabezado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar nombre y rol del empleado en sesión
        EmpleadoDTO empleado = (EmpleadoDTO) SistemaPizzeria.getMetadatos("empleado");
        if (empleado != null) {
            lbl_nombreUsuario.setText(empleado.getNombreCompleto());
            lbl_rolUsuario.setText(empleado.getTipoEmpleado().name());
        }
    }

    @FXML
    private void clicUsuarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("UsuariosGestion", "Usuarios");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosGestion", "Productos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicProductosInventario(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ProductosInventarioGestion", "Productos de Inventario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicValidacionInventarios(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("ValidacionInventarios", "Validación de Inventarios");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicPedidos(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("PedidosGestion", "Pedidos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        try {
            SistemaPizzeria.setRoot("InicioSesion", "Sistema Pizzeria - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clicAyudaAcercaDe(ActionEvent event) {
        // TODO: abrir ventana de ayuda
    }
}