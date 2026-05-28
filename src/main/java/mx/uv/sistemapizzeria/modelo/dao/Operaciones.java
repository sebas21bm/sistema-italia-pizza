package mx.uv.sistemapizzeria.modelo.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface Operaciones<T, E> {
    E buscar(T identificador) throws Exception;
    boolean editar(E elemento) throws Exception;
    boolean eliminar(T identificador) throws Exception;
    List<E> mostrarTodos() throws NullPointerException, ClassNotFoundException, IOException, SQLException;
    boolean registrar(E elemento) throws Exception;
}
