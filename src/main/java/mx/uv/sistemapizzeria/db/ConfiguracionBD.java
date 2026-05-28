package mx.uv.sistemapizzeria.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionBD {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfiguracionBD.class.getResourceAsStream("/config/database.properties")){

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfiguracionBD() {
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }

    public static String getHost() {
        return properties.getProperty("db.host");
    }

    public static String getPort() {
        return properties.getProperty("db.port");
    }


    public static String getDatabase() {
        return properties.getProperty("db.name");
    }


    public static String getUrl() {
        return "jdbc:mysql://"
                + getHost()
                + ":"
                + getPort()
                + "/"
                + getDatabase()
                + "?useTimezone=true&serverTimezone=UTC";
    }

}
