package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class EmpleadoDAOTest extends BaseDAOTest {

    @Test
    public void testRegistrarEmpleadoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        EmpleadoDTO empleado = crearEmpleado();

        //WHEN
        boolean resultado = dao.registrar(empleado);

        //THEN
        assertTrue(resultado);
        assertNotNull(dao.buscar(empleado.getNoEmpleado()));
    }

    @Test
    public void testBuscarEmpleadoExiste() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        EmpleadoDTO empleado = crearEmpleado();
        dao.registrar(empleado);

        //WHEN
        EmpleadoDTO resultado = dao.buscar(empleado.getNoEmpleado());

        //THEN
        assertNotNull(resultado);
        assertEquals(empleado.getNoEmpleado(), resultado.getNoEmpleado());
    }

    @Test
    public void testEditarEmpleadoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        EmpleadoDTO empleado = crearEmpleado();
        dao.registrar(empleado);

        empleado.setNombre("Empleado Editado");
        empleado.setTelefono(generarTelefono());

        //WHEN
        boolean resultado = dao.editar(empleado);

        //THEN
        assertTrue(resultado);
        assertEquals("Empleado Editado", dao.buscar(empleado.getNoEmpleado()).getNombre());
    }

    @Test
    public void testEliminarEmpleadoValido() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        EmpleadoDTO empleado = crearEmpleado();
        dao.registrar(empleado);

        //WHEN
        boolean resultado = dao.eliminar(empleado.getNoEmpleado());

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testMostrarTodosEmpleados() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();

        //WHEN
        List<EmpleadoDTO> resultado = dao.mostrarTodos();

        //THEN
        assertNotNull(resultado);
    }
    private EmpleadoDTO crearEmpleado() {
    DireccionDTO direccion = new DireccionDTO();
    direccion.setCalle("Calle prueba");
    direccion.setNumero("10");
    direccion.setCodigoPostal("91000");
    direccion.setCiudad("Xalapa");

    EmpleadoDTO empleado = new EmpleadoDTO();
    empleado.setNoEmpleado(generarNoEmpleado());
    empleado.setUsuario("u" + (System.currentTimeMillis() % 10000));
    empleado.setContrasenia("123456".getBytes());
    empleado.setNombre("Empleado");
    empleado.setPaterno("Prueba");
    empleado.setMaterno("JUnit");
    empleado.setTelefono(generarTelefono());
    empleado.setEmail("empleado" + System.currentTimeMillis() + "@test.com");
    empleado.setTipoEmpleado(TipoEmpleado.Cajero);
    empleado.setDireccion(direccion);

    return empleado;
    }
    
    @Test
    public void testRegistrarEmpleadoSinDireccionLanzaNullPointerException() {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado(generarNoEmpleado());
        empleado.setUsuario("u" + (System.currentTimeMillis() % 10000));
        empleado.setContrasenia("123456".getBytes());
        empleado.setNombre("Empleado");
        empleado.setPaterno("Error");
        empleado.setMaterno("JUnit");
        empleado.setTelefono(generarTelefono());
        empleado.setEmail("empleadoerror" + System.currentTimeMillis() + "@test.com");
        empleado.setTipoEmpleado(TipoEmpleado.Cajero);
        empleado.setDireccion(null);

        //WHEN / THEN
        assertThrows(NullPointerException.class, () -> dao.registrar(empleado));
    }

    @Test
    public void testRegistrarEmpleadoSinTipoLanzaNullPointerException() {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();

        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Calle error");
        direccion.setNumero("10");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado(generarNoEmpleado());
        empleado.setUsuario("u" + (System.currentTimeMillis() % 10000));
        empleado.setContrasenia("123456".getBytes());
        empleado.setNombre("Empleado");
        empleado.setPaterno("Error");
        empleado.setMaterno("JUnit");
        empleado.setTelefono(generarTelefono());
        empleado.setEmail("empleadoerror" + System.currentTimeMillis() + "@test.com");
        empleado.setTipoEmpleado(null);
        empleado.setDireccion(direccion);

        //WHEN / THEN
        assertThrows(NullPointerException.class, () -> dao.registrar(empleado));
    }

    @Test
    public void testBuscarEmpleadoInexistenteRegresaNull() throws Exception {
        prepararSesionAdministrador();

        //GIVEN
        EmpleadoDAO dao = new EmpleadoDAO();
        String noEmpleadoInexistente = "E9999";

        //WHEN
        EmpleadoDTO resultado = dao.buscar(noEmpleadoInexistente);

        //THEN
        assertNull(resultado);
    }
}
