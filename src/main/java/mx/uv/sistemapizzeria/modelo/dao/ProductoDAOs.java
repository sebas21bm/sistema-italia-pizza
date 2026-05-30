package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProductoDAOs implements Operaciones<String, ProductoVentaDTO> {

    @Override
    public ProductoVentaDTO buscar(String identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean editar(ProductoVentaDTO elemento) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean eliminar(String identificador) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<ProductoVentaDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean registrar(ProductoVentaDTO elemento) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        return false;
    }
}
