package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;


public class ClienteDAOTest extends BaseDAOTest {

    @Test
    public void testRegistrarClienteValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();

        //WHEN
        boolean resultado = dao.registrar(cliente);

        //THEN
        assertTrue(resultado);
        assertFalse(dao.buscarPorTelefono(cliente.getTelefono()).isEmpty());
    }

    @Test
    public void testBuscarClientePorTelefonoExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();
        dao.registrar(cliente);

        //WHEN
        List<ClienteDTO> resultado = dao.buscarPorTelefono(cliente.getTelefono());

        //THEN
        assertEquals(1, resultado.size());
    }

    @Test
    public void testBuscarClientePorNombreExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();
        dao.registrar(cliente);

        //WHEN
        List<ClienteDTO> resultado = dao.buscarPorNombre(cliente.getNombre());

        //THEN
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testEditarClienteValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();
        dao.registrar(cliente);

        ClienteDTO registrado = dao.buscarPorTelefono(cliente.getTelefono()).get(0);
        registrado.setNombre("Cliente Editado");

        //WHEN
        boolean resultado = dao.editar(registrado);

        //THEN
        assertTrue(resultado);
        assertEquals("Cliente Editado", dao.buscar(registrado.getNoCliente()).getNombre());
    }

    @Test
    public void testEliminarClienteValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();
        dao.registrar(cliente);

        ClienteDTO registrado = dao.buscarPorTelefono(cliente.getTelefono()).get(0);

        //WHEN
        boolean resultado = dao.eliminar(registrado.getNoCliente());

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testMostrarTodosClientes() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();

        //WHEN
        List<ClienteDTO> resultado = dao.mostrarTodos();

        //THEN
        assertNotNull(resultado);
    }

    private ClienteDTO crearCliente() {
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Calle Cliente");
        direccion.setNumero("20");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        List<DireccionDTO> direcciones = new ArrayList<>();
        direcciones.add(direccion);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Cliente");
        cliente.setPaterno("Prueba");
        cliente.setMaterno("JUnit");
        cliente.setTelefono(generarTelefono());
        cliente.setEmail("cliente" + System.currentTimeMillis() + "@test.com");
        cliente.setDirecciones(direcciones);

        return cliente;
    }
    
    @Test
    public void testBuscarClienteInexistenteRegresaNull() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        int noClienteInexistente = -1;

        //WHEN
        ClienteDTO resultado = dao.buscar(noClienteInexistente);

        //THEN
        assertNull(resultado);
    }

    @Test
    public void testEliminarClienteInexistenteNoLanzaExcepcion() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        int noClienteInexistente = -1;

        //WHEN / THEN
        assertDoesNotThrow(() -> dao.eliminar(noClienteInexistente));
    }
    
    @Test
    public void testRegistrarClienteSinDireccionesLanzaMensajeEsperado() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Cliente Error");
        cliente.setPaterno("Prueba");
        cliente.setMaterno("JUnit");
        cliente.setTelefono(generarTelefono());
        cliente.setEmail("clienteerror" + System.currentTimeMillis() + "@test.com");
        cliente.setDirecciones(new ArrayList<>());

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> dao.registrar(cliente));

        //THEN
        assertMensaje(excepcion, "No se ingresaron direcciones");
    }

    @Test
    public void testEditarClienteSinDireccionesLanzaMensajeEsperado() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        ClienteDAO dao = new ClienteDAO();
        ClienteDTO cliente = crearCliente();

        dao.registrar(cliente);

        ClienteDTO registrado = dao.buscarPorTelefono(cliente.getTelefono()).get(0);
        registrado.setNombre("Cliente Error Editado");
        registrado.setDirecciones(new ArrayList<>());

        //WHEN
        SQLException excepcion = assertThrows(SQLException.class, () -> dao.editar(registrado));

        //THEN
        assertMensaje(excepcion, "No se ingresaron direcciones");
    }
}

