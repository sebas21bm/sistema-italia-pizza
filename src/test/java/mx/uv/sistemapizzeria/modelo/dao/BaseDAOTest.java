package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;

import static org.junit.jupiter.api.Assertions.*;

//Esta clase dentro de las pruebas se utiliza debido a que
// Necesitamos establecer una conexión con la BD

public class BaseDAOTest {

    protected void prepararSesionAdministrador() {
        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setNoEmpleado("E0001");
        empleado.setTipoEmpleado(TipoEmpleado.Administrador);
        Sesion.empleadoSesion = empleado;
    }

    protected String generarCodigoProductoVenta() {
        int numero = (int) (System.currentTimeMillis() % 9000) + 1000;
        return "P" + numero;
    }

    protected String generarCodigoProductoInventario() {
        int numero = (int) (System.currentTimeMillis() % 9000) + 1000;
        return "I" + numero;
    }

    protected String generarNoEmpleado() {
        int numero = (int) (System.currentTimeMillis() % 9000) + 1000;
        return "E" + numero;
    }

    protected String generarTelefono() {
        long numero = System.currentTimeMillis() % 10000000000L;
        return String.format("%010d", numero);
    }
    
    protected void assertMensaje(Exception excepcion, String mensajeEsperado) {
    assertNotNull(excepcion.getMessage());
    assertTrue(
            excepcion.getMessage().contains(mensajeEsperado),
            "Mensaje esperado: " + mensajeEsperado + "\nMensaje recibido: " + excepcion.getMessage()
    );
}
}
