package mx.uv.sistemapizzeria.utilidades;

import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorTest {

    @Test
    public void testValidarEmailValido() {
        //GIVEN
        String email = "usuario@test.com";

        //WHEN
        boolean resultado = Validador.esEmailValido(email);

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testValidarTelefonoValido() {
        //GIVEN
        String telefono = "2281234567";

        //WHEN
        boolean resultado = Validador.esTelefonoValido(telefono);

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testValidarCodigoProductoInventarioValido() {
        //GIVEN
        String codigo = "I0001";

        //WHEN
        boolean resultado = Validador.esCodigoInsumoValido(codigo);

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testValidarCodigoProductoVentaValido() {
        //GIVEN
        String codigo = "P0001";

        //WHEN
        boolean resultado = Validador.esCodigoMenuValido(codigo);

        //THEN
        assertTrue(resultado);
    }

    @Test
    public void testValidarClienteValido() {
        //GIVEN
        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("Sebastian");
        cliente.setPaterno("Bautista");
        cliente.setTelefono("2281234567");
        cliente.setEmail("cliente@test.com");

        //WHEN
        List<String> errores = Validador.validarCliente(cliente);

        //THEN
        assertTrue(errores.isEmpty());
    }

    @Test
    public void testValidarEmpleadoValido() {
        //GIVEN
        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado("E0001");
        empleado.setNombre("Empleado");
        empleado.setPaterno("Prueba");
        empleado.setTelefono("2281234567");
        empleado.setEmail("empleado@test.com");
        empleado.setTipoEmpleado(TipoEmpleado.Administrador);

        //WHEN
        List<String> errores = Validador.validarEmpleado(empleado);

        //THEN
        assertTrue(errores.isEmpty());
    }

    @Test
    public void testValidarDireccionValida() {
        //GIVEN
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Av Principal");
        direccion.setNumero("10");
        direccion.setCodigoPostal("91000");
        direccion.setCiudad("Xalapa");

        //WHEN
        List<String> errores = Validador.validarDireccion(direccion);

        //THEN
        assertTrue(errores.isEmpty());
    }

    @Test
    public void testValidarProductoVentaValido() {
        //GIVEN
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu("P0001");
        producto.setNombre("Pizza");
        producto.setPrecio(100.0);
        producto.setLimite(10);

        //WHEN
        List<String> errores = Validador.validarProductoVenta(producto);

        //THEN
        assertTrue(errores.isEmpty());
    }

    @Test
    public void testValidarProductoInventarioValido() {
        //GIVEN
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo("I0001");
        producto.setNombre("Queso");
        producto.setExistencias(20);

        //WHEN
        List<String> errores = Validador.validarProductoInsumo(producto);

        //THEN
        assertTrue(errores.isEmpty());
    }
    
    @Test
    public void testValidarEmailInvalido() {
        //GIVEN
        String email = "correo_invalido";

        //WHEN
        boolean resultado = Validador.esEmailValido(email);

        //THEN
        assertFalse(resultado);
    }

    @Test
    public void testValidarTelefonoInvalido() {
        //GIVEN
        String telefono = "123";

        //WHEN
        boolean resultado = Validador.esTelefonoValido(telefono);

        //THEN
        assertFalse(resultado);
    }

    @Test
    public void testValidarCodigoProductoInventarioInvalido() {
        //GIVEN
        String codigo = "X0001";

        //WHEN
        boolean resultado = Validador.esCodigoInsumoValido(codigo);

        //THEN
        assertFalse(resultado);
    }

    @Test
    public void testValidarCodigoProductoVentaInvalido() {
        //GIVEN
        String codigo = "A0001";

        //WHEN
        boolean resultado = Validador.esCodigoMenuValido(codigo);

        //THEN
        assertFalse(resultado);
    }

    @Test
    public void testValidarClienteInvalidoRegresaErrores() {
        //GIVEN
        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("");
        cliente.setPaterno("");
        cliente.setTelefono("123");
        cliente.setEmail("correo");

        //WHEN
        List<String> errores = Validador.validarCliente(cliente);

        //THEN
        assertFalse(errores.isEmpty());
    }

    @Test
    public void testValidarProductoVentaInvalidoRegresaErrores() {
        //GIVEN
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu("X0001");
        producto.setNombre("");
        producto.setPrecio(0);
        producto.setLimite(0);

        //WHEN
        List<String> errores = Validador.validarProductoVenta(producto);

        //THEN
        assertFalse(errores.isEmpty());
    }

    @Test
    public void testValidarProductoInventarioInvalidoRegresaErrores() {
        //GIVEN
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo("P0001");
        producto.setNombre("");
        producto.setExistencias(-5);

        //WHEN
        List<String> errores = Validador.validarProductoInsumo(producto);

        //THEN
        assertFalse(errores.isEmpty());
    }
    
    @Test
    public void testValidarClienteInvalidoRegresaMensajesEsperados() {
        //GIVEN
        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombre("");
        cliente.setPaterno("");
        cliente.setTelefono("123");
        cliente.setEmail("correo");

        //WHEN
        List<String> errores = Validador.validarCliente(cliente);

        //THEN
        assertTrue(errores.contains("El nombre del cliente no puede estar vacío."));
        assertTrue(errores.contains("El apellido paterno del cliente no puede estar vacío."));
        assertTrue(errores.contains("El teléfono debe contener exactamente 10 dígitos numéricos."));
        assertTrue(errores.contains("El correo electrónico no tiene un formato válido."));
    }

    @Test
    public void testValidarEmpleadoInvalidoRegresaMensajesEsperados() {
        //GIVEN
        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado("X0001");
        empleado.setNombre("");
        empleado.setPaterno("");
        empleado.setTelefono("123");
        empleado.setEmail("correo");
        empleado.setTipoEmpleado(null);

        //WHEN
        List<String> errores = Validador.validarEmpleado(empleado);

        //THEN
        assertTrue(errores.contains("El número de empleado debe tener el formato E seguido de 4 dígitos (ej. E0123)."));
        assertTrue(errores.contains("El nombre del empleado no puede estar vacío."));
        assertTrue(errores.contains("El apellido paterno del empleado no puede estar vacío."));
        assertTrue(errores.contains("El teléfono debe contener exactamente 10 dígitos numéricos."));
        assertTrue(errores.contains("El correo electrónico no tiene un formato válido."));
        assertTrue(errores.contains("Debe seleccionar un tipo de empleado."));
    }

    @Test
    public void testValidarDireccionInvalidaRegresaMensajesEsperados() {
        //GIVEN
        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("");
        direccion.setNumero("");
        direccion.setCodigoPostal("123");
        direccion.setCiudad("");

        //WHEN
        List<String> errores = Validador.validarDireccion(direccion);

        //THEN
        assertTrue(errores.contains("La calle no puede estar vacía."));
        assertTrue(errores.contains("El número exterior no puede estar vacío."));
        assertTrue(errores.contains("El código postal debe contener exactamente 5 dígitos."));
        assertTrue(errores.contains("La ciudad no puede estar vacía."));
    }

    @Test
    public void testValidarProductoVentaInvalidoRegresaMensajesEsperados() {
        //GIVEN
        ProductoVentaDTO producto = new ProductoVentaDTO();
        producto.setCodigoMenu("X0001");
        producto.setNombre("");
        producto.setPrecio(0);
        producto.setLimite(0);

        //WHEN
        List<String> errores = Validador.validarProductoVenta(producto);

        //THEN
        assertTrue(errores.contains("El código de menú debe tener el formato P seguido de 4 dígitos (ej. P0001)."));
        assertTrue(errores.contains("El nombre del producto no puede estar vacío."));
        assertTrue(errores.contains("El precio debe ser mayor a 0."));
        assertTrue(errores.contains("El límite de unidades debe ser mayor a 0."));
    }

    @Test
    public void testValidarProductoInventarioInvalidoRegresaMensajesEsperados() {
        //GIVEN
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setCodigo("P0001");
        producto.setNombre("");
        producto.setExistencias(-5);

        //WHEN
        List<String> errores = Validador.validarProductoInsumo(producto);

        //THEN
        assertTrue(errores.contains("El código de insumo debe tener el formato I seguido de 4 dígitos (ej. I0001)."));
        assertTrue(errores.contains("El nombre del insumo no puede estar vacío."));
        assertTrue(errores.contains("Las existencias no pueden ser negativas."));
    }
}