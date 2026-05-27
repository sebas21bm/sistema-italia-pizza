package mx.uv.sistemapizzeria.db;

import mx.uv.sistemapizzeria.modelo.dto.TipoEmpleado;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    public static Connection crearParaAutenticacion() throws SQLException, IOException, ClassNotFoundException {
        Properties prop = CargadorCredenciales.cargarCredenciales("/config/usuario_autenticacion.properties");

        Class.forName(ConfiguracionBD.getDriver());

        String user = prop.getProperty("db.user");
        String  password = prop.getProperty("db.password");
        String url = ConfiguracionBD.getUrl();

        return DriverManager.getConnection(url, user, password);
    }

    public static Connection crearParaRol(TipoEmpleado tipoEmpleado) throws IOException, SQLException, ClassNotFoundException{
        String rutaProperties = determinarRutaProperties(tipoEmpleado);

        Properties prop = CargadorCredenciales.cargarCredenciales(rutaProperties);

        Class.forName(ConfiguracionBD.getDriver());

        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");
        String url = ConfiguracionBD.getUrl();

        return DriverManager.getConnection(url, user, password);
    }

    private static String determinarRutaProperties(TipoEmpleado tipoEmpleado) {
        String rutaProperties = null;

        if (tipoEmpleado == TipoEmpleado.ADMINISTRADOR) {
            rutaProperties = "/config/usuario_administrador.properties";
        }
        if (tipoEmpleado == TipoEmpleado.CAJERO) {
            rutaProperties = "/config/usuario_cajero.properties";
        }

        return rutaProperties;
    }
}
