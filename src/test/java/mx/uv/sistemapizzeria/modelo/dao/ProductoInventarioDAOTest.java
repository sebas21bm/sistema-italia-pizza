package mx.uv.sistemapizzeria.modelo.dao;

import java.sql.SQLException;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoInventarioDAOTest extends BaseDAOTest {

    @Test
    public void testRegistrarProductoInventarioValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO dao = new ProductoInventarioDAO();
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo(generarCodigoProductoInventario());
        producto.setNombre("Producto inventario prueba");
        producto.setExistencias(10);
        producto.setFechaCaducidad(LocalDate.now().plusDays(10));
        producto.setFoto("C:\\imagenes\\producto.png");

        //WHEN
        boolean resultado = dao.registrar(producto);

        //THEN
        assertTrue(resultado);
        assertNotNull(dao.buscar(producto.getCodigo()));
    }

    @Test
    public void testBuscarProductoInventarioExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO dao = new ProductoInventarioDAO();
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo(generarCodigoProductoInventario());
        producto.setNombre("Buscar inventario prueba");
        producto.setExistencias(15);
        producto.setFechaCaducidad(LocalDate.now().plusDays(15));
        producto.setFoto("C:\\imagenes\\buscar.png");

        dao.registrar(producto);

        //WHEN
        ProductoInventarioDTO resultado = dao.buscar(producto.getCodigo());

        //THEN
        assertNotNull(resultado);
        assertEquals(producto.getCodigo(), resultado.getCodigo());
    }

    @Test
    public void testEditarProductoInventarioValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO dao = new ProductoInventarioDAO();
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo(generarCodigoProductoInventario());
        producto.setNombre("Inventario antes");
        producto.setExistencias(5);
        producto.setFechaCaducidad(LocalDate.now().plusDays(5));
        producto.setFoto("C:\\imagenes\\antes.png");

        dao.registrar(producto);

        producto.setNombre("Inventario editado");
        producto.setExistencias(20);
        producto.setFoto("C:\\imagenes\\despues.png");

        //WHEN
        boolean resultado = dao.editar(producto);

        //THEN
        assertTrue(resultado);
        assertEquals("Inventario editado", dao.buscar(producto.getCodigo()).getNombre());
    }

    @Test
    public void testEliminarProductoInventarioValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO dao = new ProductoInventarioDAO();
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo(generarCodigoProductoInventario());
        producto.setNombre("Inventario eliminar");
        producto.setExistencias(7);
        producto.setFechaCaducidad(LocalDate.now().plusDays(7));
        producto.setFoto("C:\\imagenes\\eliminar.png");

        dao.registrar(producto);

        //WHEN
        boolean resultado = dao.eliminar(producto.getCodigo());

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testMostrarTodosProductosInventario() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO dao = new ProductoInventarioDAO();

        //WHEN
        List<ProductoInventarioDTO> resultado = dao.mostrarTodos();

        //THEN
        assertNotNull(resultado);
    }
    
    @Test
    public void testEliminarProductoInventarioUsadoEnPedidoLanzaMensajeEsperado() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoInventarioDAO inventarioDAO = new ProductoInventarioDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        PedidosDAO pedidosDAO = new PedidosDAO();

        ProductoInventarioDTO insumo = new ProductoInventarioDTO();
        insumo.setCodigo(generarCodigoProductoInventario());
        insumo.setNombre("Insumo usado pedido");
        insumo.setExistencias(100);
        insumo.setFechaCaducidad(LocalDate.now().plusDays(30));
        insumo.setFoto("C:\\imagenes\\insumo-usado.png");

        inventarioDAO.registrar(insumo);

        ProductoCompuestoPorDTO recetaDetalle = new ProductoCompuestoPorDTO();
        recetaDetalle.setCodigoInsumo(insumo.getCodigo());
        recetaDetalle.setNombreProductoInventario(insumo.getNombre());
        recetaDetalle.setCantidad(1.0);

        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();
        receta.add(recetaDetalle);

        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu(generarCodigoProductoVenta());
        producto.setNombre("Producto con insumo usado");
        producto.setDescripcion("Producto de prueba");
        producto.setPrecio(100.0);
        producto.setLimite(10);
        producto.setFoto("C:\\imagenes\\producto-usado.png");

        productoDAO.registrarConReceta(producto, receta);

        ClienteDTO cliente = crearClienteParaProductoInventarioTest();
        clienteDAO.registrar(cliente);

        ClienteDTO clienteRegistrado = clienteDAO.buscarPorTelefono(cliente.getTelefono()).get(0);

        DetallePedidoDTO detallePedido = new DetallePedidoDTO();
        detallePedido.setCodigoMenu(producto.getCodigoMenu());
        detallePedido.setCantidad(1);
        detallePedido.setCosto(producto.getPrecio());

        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstatus("En proceso");
        pedido.setNoCliente(clienteRegistrado.getNoCliente());
        pedido.setCliente(clienteRegistrado);
        pedido.setDireccion(clienteRegistrado.getDirecciones().get(0));

        List<DetallePedidoDTO> detalles = new ArrayList<>();
        detalles.add(detallePedido);
        pedido.setDetalles(detalles);

        pedidosDAO.registrar(pedido);

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> inventarioDAO.eliminar(insumo.getCodigo()));

        //THEN
        assertMensaje(excepcion, "No se puede eliminar el producto del inventario porque ha sido utilizado en un pedido");
    }

    private ClienteDTO crearClienteParaProductoInventarioTest() {
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Calle Inventario");
        direccion.setNumero("40");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        List<DireccionDTO> direcciones = new ArrayList<>();
        direcciones.add(direccion);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Cliente Inventario");
        cliente.setPaterno("Prueba");
        cliente.setMaterno("JUnit");
        cliente.setTelefono(generarTelefono());
        cliente.setEmail("inventario" + System.currentTimeMillis() + "@test.com");
        cliente.setDirecciones(direcciones);

        return cliente;
    }
}