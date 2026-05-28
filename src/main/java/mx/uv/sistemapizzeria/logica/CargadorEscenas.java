package mx.uv.sistemapizzeria.logica;

import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;

public class CargadorEscenas {

    public static String cargarEscenaSegunRol(TipoEmpleado tipoEmpleado) {
        if (tipoEmpleado == null) {
            return null;
        }

        switch (tipoEmpleado) {
            case ADMINISTRADOR:
                return "MenuAdministrador";
            case CAJERO:
                return "MenuCajero";
            default:
                return null;
        }
    }
}
