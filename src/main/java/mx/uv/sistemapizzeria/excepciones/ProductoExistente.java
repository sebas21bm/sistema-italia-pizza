package mx.uv.sistemapizzeria.excepciones;

public class ProductoExistente extends Exception {
    public ProductoExistente(String msg) {
        super(msg);
    }
}
