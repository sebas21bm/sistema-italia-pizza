package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class PedidosDAOTest extends BaseDAOTest {

    @Test
    public void testRegistrarPedidoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();
        PedidoDTO pedido = crearPedidoCompleto();

        //WHEN
        boolean resultado = dao.registrar(pedido);

        //THEN
        assertTrue(resultado);
        assertTrue(pedido.getIdPedido() > 0);
    }

    @Test
    public void testBuscarPedidoExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();
        PedidoDTO pedido = crearPedidoCompleto();
        dao.registrar(pedido);

        //WHEN
        PedidoDTO resultado = dao.buscar(pedido.getIdPedido());

        //THEN
        assertNotNull(resultado);
        assertEquals(pedido.getIdPedido(), resultado.getIdPedido());
    }

    @Test
    public void testCambiarEstatusPedidoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();
        PedidoDTO pedido = crearPedidoCompleto();
        dao.registrar(pedido);

        //WHEN
        boolean resultado = dao.cambiarEstatus(pedido.getIdPedido(), "Entregado");

        //THEN
        assertTrue(resultado);
        assertEquals("Entregado", dao.buscar(pedido.getIdPedido()).getEstatus());
    }

    @Test
    public void testEditarPedidoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();
        PedidoDTO pedido = crearPedidoCompleto();
        dao.registrar(pedido);

        DetallePedidoDTO detalle = pedido.getDetalles().get(0);
        detalle.setCantidad(2);

        //WHEN
        boolean resultado = dao.editar(pedido);

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testMostrarTodosPedidos() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();

        //WHEN
        List<PedidoDTO> resultado = dao.mostrarTodos();

        //THEN
        assertNotNull(resultado);
    }

    private PedidoDTO crearPedidoCompleto() throws Exception {
        ClienteDAO clienteDAO = new ClienteDAO();
        ProductoDAO productoDAO = new ProductoDAO();

        ClienteDTO cliente = crearCliente();
        clienteDAO.registrar(cliente);

        ClienteDTO clienteRegistrado = clienteDAO.buscarPorTelefono(cliente.getTelefono()).get(0);
        DireccionDTO direccion = clienteRegistrado.getDirecciones().get(0);

        ProductoVentaDTO producto = crearProductoVenta();
        productoDAO.registrarSinReceta(producto, 20, LocalDate.now().plusDays(20));

        DetallePedidoDTO detalle = new DetallePedidoDTO();
        detalle.setCodigoMenu(producto.getCodigoMenu());
        detalle.setCantidad(1);
        detalle.setCosto(producto.getPrecio());
        detalle.setProductoVenta(producto);

        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setNoCliente(clienteRegistrado.getNoCliente());
        pedido.setCliente(clienteRegistrado);
        pedido.setDireccion(direccion);

        List<DetallePedidoDTO> detalles = new ArrayList<>();
        detalles.add(detalle);
        pedido.setDetalles(detalles);

        return pedido;
    }

    private ClienteDTO crearCliente() {
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Calle Pedido");
        direccion.setNumero("30");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        List<DireccionDTO> direcciones = new ArrayList<>();
        direcciones.add(direccion);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Cliente Pedido");
        cliente.setPaterno("Prueba");
        cliente.setMaterno("JUnit");
        cliente.setTelefono(generarTelefono());
        cliente.setEmail("pedido" + System.currentTimeMillis() + "@test.com");
        cliente.setDirecciones(direcciones);

        return cliente;
    }

    private ProductoVentaDTO crearProductoVenta() {
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu(generarCodigoProductoVenta());
        producto.setNombre("Producto Pedido");
        producto.setDescripcion("Producto para prueba de pedido");
        producto.setPrecio(100.0);
        producto.setLimite(10);
        producto.setFoto("C:\\imagenes\\pedido.png");
        return producto;
    }
    
    @Test
    public void testRegistrarPedidoSinDetallesLanzaMensajeEsperado() {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();

        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setNoCliente(1);

        DireccionDTO direccion = new DireccionDTO();
        direccion.setIdDireccion(1);
        pedido.setDireccion(direccion);

        pedido.setDetalles(new ArrayList<>());

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> dao.registrar(pedido));

        //THEN
        assertMensaje(excepcion, "No se seleccionaron items para el pedido");
    }

    @Test
    public void testEditarPedidoSinDetallesLanzaMensajeEsperado() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        PedidosDAO dao = new PedidosDAO();
        PedidoDTO pedido = crearPedidoCompleto();

        dao.registrar(pedido);
        pedido.setDetalles(new ArrayList<>());

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> dao.editar(pedido));

        //THEN
        assertMensaje(excepcion, "No se seleccionaron items para el pedido");
    }
}