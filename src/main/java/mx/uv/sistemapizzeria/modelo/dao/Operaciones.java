package mx.uv.sistemapizzeria.modelo.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface Operaciones<T, E> {
    E buscar(T identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException;
    boolean editar(E elemento) throws NullPointerException, IOException, SQLException, ClassNotFoundException;
    boolean eliminar(T identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException;
    List<E> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException;
    boolean registrar(E elemento) throws NullPointerException, IOException, SQLException, ClassNotFoundException;
}
