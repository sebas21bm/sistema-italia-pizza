module mx.uv.sistemapizzeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires jdk.jshell;
    requires java.desktop;
    requires com.opencsv;
    requires kernel;
    requires layout;
    requires io;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires mysql.connector.j;

    opens mx.uv.sistemapizzeria to javafx.fxml;
    opens mx.uv.sistemapizzeria.controladores to javafx.fxml;
    opens mx.uv.sistemapizzeria.modelo.dao to javafx.fxml;
    opens mx.uv.sistemapizzeria.modelo.dto to javafx.fxml, javafx.base;

    exports mx.uv.sistemapizzeria;
}