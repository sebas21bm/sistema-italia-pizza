module mx.uv.sistemapizzeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens mx.uv.sistemapizzeria to javafx.fxml;
    opens mx.uv.sistemapizzeria.controladores to javafx.fxml;
    opens mx.uv.sistemapizzeria.modelo.dao to javafx.fxml;
    opens mx.uv.sistemapizzeria.modelo.dto to javafx.fxml;
    exports mx.uv.sistemapizzeria;
}
