package mx.uv.sistemapizzeria.logica;

import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;

import java.io.IOException;
import java.sql.SQLException;

public class CargadorEscenas {

    public static String cargarEscenarSegunRol(TipoEmpleado tipoEmpleado) {
        String rutaMenu = null;

        if (tipoEmpleado == TipoEmpleado.ADMINISTRADOR) {
            rutaMenu = "MenuAdministrador";
        }
        if (tipoEmpleado == TipoEmpleado.CAJERO) {
            rutaMenu = "MenuCajero";
        }

        return rutaMenu;
    }

}
