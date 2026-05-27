package mx.uv.sistemapizzeria.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CargadorCredenciales {

    public static Properties cargarCredenciales(String ruta) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = CargadorCredenciales.class.getResourceAsStream(ruta)){
            prop.load(input);
        }
        return prop;
    }
}
