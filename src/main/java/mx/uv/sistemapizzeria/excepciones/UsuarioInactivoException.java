package mx.uv.sistemapizzeria.excepciones;

public class UsuarioInactivoException extends RuntimeException {
    public UsuarioInactivoException(String message) {
        super(message);
    }
}
