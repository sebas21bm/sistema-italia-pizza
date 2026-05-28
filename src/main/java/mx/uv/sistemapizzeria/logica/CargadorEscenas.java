package mx.uv.sistemapizzeria.logica;

import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;

public class CargadorEscenas {

    public static String cargarEscenaSegunRol(TipoEmpleado tipoEmpleado) {
        if (tipoEmpleado == null) {
            return null;
        }

        switch (tipoEmpleado) {
            case Admnistrador:
                return "MenuAdministrador";
            case Cajero:
                return "MenuCajero";
            default:
                return null;
        }
    }
}
