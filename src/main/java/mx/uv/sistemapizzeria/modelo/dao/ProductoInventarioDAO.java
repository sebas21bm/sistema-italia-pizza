package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoInventarioDAO implements Operaciones<String, ProductoInventarioDTO> {
    @Override
    public boolean registrar(ProductoInventarioDTO insumo) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "INSERT INTO producto_inventario (codigo, nombre, estatus, existencias, fecha_caducidad, foto) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, insumo.getCodigo());
            sentencia.setString(2, insumo.getNombre());
            sentencia.setInt(3, 1);
            sentencia.setInt(4, insumo.getExistencias());
            LocalDate fecha = insumo.getFechaCaducidad();
            sentencia.setDate(5, Date.valueOf(fecha));
            sentencia.setString(6, insumo.getFoto());
            return (sentencia.executeUpdate() != 0);
        }
    }

    @Override
    public boolean editar(ProductoInventarioDTO insumo) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "UPDATE producto_inventario SET nombre=?, existencias=?, fecha_caducidad=?, foto=? " +
                    "WHERE codigo=?";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, insumo.getNombre());
            sentencia.setInt(2, insumo.getExistencias());
            LocalDate fecha = insumo.getFechaCaducidad();
            sentencia.setDate(3, Date.valueOf(fecha));
            sentencia.setString(4, insumo.getFoto());
            sentencia.setString(5, insumo.getCodigo());
            return (sentencia.executeUpdate() != 0);
        }
    }

    @Override
    public boolean eliminar(String codigo) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "CALL eliminar_producto_inventario(?);";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, codigo);
            return (sentencia.executeUpdate() != 0);
        }
    }

    @Override
    public List<ProductoInventarioDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoInventarioDTO> lista = new ArrayList<>();
        try(Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())){
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION + "MOSTRAR");
            }
            String consulta = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto FROM producto_inventario WHERE estatus = 1";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                ProductoInventarioDTO productoInventario = new ProductoInventarioDTO();
                productoInventario.setCodigo(resultado.getString("codigo"));
                productoInventario.setNombre(resultado.getString("nombre"));
                productoInventario.setEstatus(resultado.getInt("estatus"));
                productoInventario.setExistencias(resultado.getInt("existencias"));
                Date fecha = resultado.getDate("fecha_caducidad");
                productoInventario.setFechaCaducidad(fecha.toLocalDate());
                productoInventario.setFoto(resultado.getString("foto"));
                lista.add(productoInventario);
            }
            return lista;
        }
    }

    @Override
    public ProductoInventarioDTO buscar(String codigo) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        ProductoInventarioDTO productoInventario = new ProductoInventarioDTO();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto "
                    + "FROM producto_inventario WHERE codigo = ? AND estatus = 1";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, codigo);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                productoInventario.setCodigo(resultado.getString("codigo"));
                productoInventario.setNombre(resultado.getString("nombre"));
                productoInventario.setEstatus(resultado.getInt("estatus"));
                productoInventario.setExistencias(resultado.getInt("existencias"));
                Date fecha = resultado.getDate("fecha_caducidad");
                productoInventario.setFechaCaducidad(fecha.toLocalDate());
                productoInventario.setFoto(resultado.getString("foto"));
            }
            return productoInventario;
        }
    }


    public List<ProductoInventarioDTO> buscarPorNombre(String nombre) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoInventarioDTO> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT codigo, nombre, estatus, existencias, fecha_caducidad, foto "
                        + "FROM producto_inventario WHERE nombre LIKE ? AND estatus = 1";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, "%" + nombre + "%");
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                ProductoInventarioDTO productoInventario = new ProductoInventarioDTO();
                productoInventario.setCodigo(resultado.getString("codigo"));
                productoInventario.setNombre(resultado.getString("nombre"));
                productoInventario.setEstatus(resultado.getInt("estatus"));
                productoInventario.setExistencias(resultado.getInt("existencias"));
                Date fecha = resultado.getDate("fecha_caducidad");
                productoInventario.setFechaCaducidad(fecha.toLocalDate());
                productoInventario.setFoto(resultado.getString("foto"));
                lista.add(productoInventario);
            }
            return lista;
        }
    }
}
