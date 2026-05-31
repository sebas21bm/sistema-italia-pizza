package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;


public class ProductoDAOTest extends BaseDAOTest {

    @Test
    public void testRegistrarProductoSinRecetaValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();
        ProductoVentaDTO producto = crearProductoVenta();

        //WHEN
        boolean resultado = dao.registrarSinReceta(producto, 10, LocalDate.now().plusDays(10));

        //THEN
        assertTrue(resultado);
        assertNotNull(dao.buscar(producto.getCodigoMenu()));
    }

    @Test
    public void testBuscarProductoVentaExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();
        ProductoVentaDTO producto = crearProductoVenta();
        dao.registrarSinReceta(producto, 8, LocalDate.now().plusDays(8));

        //WHEN
        ProductoVentaDTO resultado = dao.buscar(producto.getCodigoMenu());

        //THEN
        assertNotNull(resultado);
        assertEquals(producto.getCodigoMenu(), resultado.getCodigoMenu());
    }

    @Test
    public void testEditarProductoSinRecetaValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();
        ProductoVentaDTO producto = crearProductoVenta();
        dao.registrarSinReceta(producto, 12, LocalDate.now().plusDays(12));

        producto.setNombre("Producto venta editado");
        producto.setPrecio(150.0);
        producto.setLimite(5);

        //WHEN
        boolean resultado = dao.editarSinReceta(producto);

        //THEN
        assertTrue(resultado);
        assertEquals("Producto venta editado", dao.buscar(producto.getCodigoMenu()).getNombre());
    }

    @Test
    public void testEliminarProductoVentaValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();
        ProductoVentaDTO producto = crearProductoVenta();
        dao.registrarSinReceta(producto, 5, LocalDate.now().plusDays(5));

        //WHEN
        boolean resultado = dao.eliminar(producto.getCodigoMenu());

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testMostrarTodosProductosVenta() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();

        //WHEN
        List<ProductoVentaDTO> resultado = dao.mostrarTodos();

        //THEN
        assertNotNull(resultado);
    }

    @Test
    public void testRegistrarProductoConRecetaValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO productoDAO = new ProductoDAO();
        ProductoInventarioDAO inventarioDAO = new ProductoInventarioDAO();

        ProductoInventarioDTO insumo = new ProductoInventarioDTO();
        insumo.setCodigo(generarCodigoProductoInventario());
        insumo.setNombre("Insumo receta prueba");
        insumo.setExistencias(50);
        insumo.setFechaCaducidad(LocalDate.now().plusDays(30));
        insumo.setFoto("C:\\imagenes\\insumo.png");

        inventarioDAO.registrar(insumo);

        ProductoCompuestoPorDTO detalleReceta = new ProductoCompuestoPorDTO();
        detalleReceta.setCodigoInsumo(insumo.getCodigo());
        detalleReceta.setNombreProductoInventario(insumo.getNombre());
        detalleReceta.setCantidad(2.0);

        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();
        receta.add(detalleReceta);

        ProductoVentaDTO producto = crearProductoVenta();

        //WHEN
        boolean resultado = productoDAO.registrarConReceta(producto, receta);

        //THEN
        assertTrue(resultado);
        assertNotNull(productoDAO.buscar(producto.getCodigoMenu()));
    }

    private ProductoVentaDTO crearProductoVenta() {
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu(generarCodigoProductoVenta());
        producto.setNombre("Producto venta prueba");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecio(99.0);
        producto.setLimite(10);
        producto.setFoto("C:\\imagenes\\producto-venta.png");
        return producto;
    }
    
    @Test
    public void testRegistrarProductoConRecetaVaciaLanzaMensajeEsperado() {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO dao = new ProductoDAO();
        ProductoVentaDTO producto = crearProductoVenta();
        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> dao.registrarConReceta(producto, receta));

        //THEN
        assertMensaje(excepcion, "No hay ingredientes seleccionados");
    }

    @Test
    public void testEditarProductoConRecetaVaciaLanzaMensajeEsperado() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ProductoDAO productoDAO = new ProductoDAO();
        ProductoInventarioDAO inventarioDAO = new ProductoInventarioDAO();

        ProductoInventarioDTO insumo = new ProductoInventarioDTO();
        insumo.setCodigo(generarCodigoProductoInventario());
        insumo.setNombre("Insumo prueba receta");
        insumo.setExistencias(50);
        insumo.setFechaCaducidad(LocalDate.now().plusDays(30));
        insumo.setFoto("C:\\imagenes\\insumo.png");

        inventarioDAO.registrar(insumo);

        ProductoCompuestoPorDTO detalleReceta = new ProductoCompuestoPorDTO();
        detalleReceta.setCodigoInsumo(insumo.getCodigo());
        detalleReceta.setNombreProductoInventario(insumo.getNombre());
        detalleReceta.setCantidad(2.0);

        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();
        receta.add(detalleReceta);

        ProductoVentaDTO producto = crearProductoVenta();
        productoDAO.registrarConReceta(producto, receta);

        producto.setNombre("Producto editado con error");

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class,
                () -> productoDAO.editarConReceta(producto, new ArrayList<>()));

        //THEN
        assertMensaje(excepcion, "La receta no puede estar vacia");
    }
    
    @Test
public void testEliminarProductoVentaUsadoEnPedidoLanzaMensajeEsperado() throws Exception {
    prepararSesionAdministrador();

    //GIVEN
    ProductoDAO productoDAO = new ProductoDAO();
    ClienteDAO clienteDAO = new ClienteDAO();
    PedidosDAO pedidosDAO = new PedidosDAO();

    ProductoVentaDTO producto = crearProductoVenta();
    productoDAO.registrarSinReceta(producto, 20, LocalDate.now().plusDays(20));

    ClienteDTO cliente = crearClienteParaProductoDAOTest();
    clienteDAO.registrar(cliente);

    ClienteDTO clienteRegistrado = clienteDAO.buscarPorTelefono(cliente.getTelefono()).get(0);

    DetallePedidoDTO detalle = new DetallePedidoDTO();
    detalle.setCodigoMenu(producto.getCodigoMenu());
    detalle.setCantidad(1);
    detalle.setCosto(producto.getPrecio());

    PedidoDTO pedido = new PedidoDTO();
    pedido.setFecha(LocalDateTime.now());
    pedido.setEstatus("En proceso");
    pedido.setNoCliente(clienteRegistrado.getNoCliente());
    pedido.setCliente(clienteRegistrado);
    pedido.setDireccion(clienteRegistrado.getDirecciones().get(0));

    List<DetallePedidoDTO> detalles = new ArrayList<>();
    detalles.add(detalle);
    pedido.setDetalles(detalles);

    pedidosDAO.registrar(pedido);

    //WHEN
    SQLException excepcion = assertThrows(SQLException.class, () -> productoDAO.eliminar(producto.getCodigoMenu()));

    //THEN
    assertMensaje(excepcion, "No se puede eliminar el producto del menu porque ha sido registrado en un pedido");
}

    private ClienteDTO crearClienteParaProductoDAOTest() {
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Calle Producto");
        direccion.setNumero("50");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        List<DireccionDTO> direcciones = new ArrayList<>();
        direcciones.add(direccion);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Cliente Producto");
        cliente.setPaterno("Prueba");
        cliente.setMaterno("JUnit");
        cliente.setTelefono(generarTelefono());
        cliente.setEmail("producto" + System.currentTimeMillis() + "@test.com");
        cliente.setDirecciones(direcciones);

        return cliente;
    }
}