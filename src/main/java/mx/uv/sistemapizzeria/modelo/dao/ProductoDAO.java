package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean registrarConReceta(ProductoVentaDTO producto, List<ProductoCompuestoPorDTO> receta)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "CREATE TEMPORARY TABLE IF NOT EXISTS temp_receta(" +
                    "codigo VARCHAR(5) NOT NULL, " +
                    "cantidad DOUBLE NOT NULL)";

            try (PreparedStatement sentenciaTabla = conn.prepareStatement(consulta)) {
                sentenciaTabla.execute();
            }

            try (PreparedStatement sentenciaLimpiarTabla = conn.prepareStatement("TRUNCATE temp_receta")) {
                sentenciaLimpiarTabla.execute();
            }

            String sqlReceta = "{CALL registrar_receta(?, ?)}";

            try (PreparedStatement psReceta = conn.prepareCall(sqlReceta)) {
                for (ProductoCompuestoPorDTO item : receta) {
                    psReceta.setString(1, item.getCodigoInsumo());
                    psReceta.setDouble(2, item.getCantidad());
                    psReceta.execute();
                }
            }

            String sqlPrincipal = "{CALL registrar_producto_con_receta(?, ?, ?, ?, ?, ?)}";

            try (PreparedStatement ps = conn.prepareCall(sqlPrincipal)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setString(3, producto.getDescripcion());
                ps.setString(4, producto.getFoto());
                ps.setDouble(5, producto.getPrecio());
                ps.setInt(6, producto.getLimite());
                ps.execute();
            }

            return true;
        }
    }

    public boolean registrarSinReceta(ProductoVentaDTO producto, int existencias, LocalDate fechaCaducidad)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String sql = "{CALL registrar_producto_sin_receta(?, ?, ?, ?, ?, ?, ?, ?)}";

            try (PreparedStatement ps = conn.prepareCall(sql)) {
                ps.setString(1, producto.getCodigoMenu());
                ps.setString(2, producto.getNombre());
                ps.setString(3, producto.getDescripcion());
                ps.setString(4, producto.getFoto());
                ps.setDouble(5, producto.getPrecio());
                ps.setInt(6, producto.getLimite());
                ps.setInt(7, existencias);
                ps.setDate(8, java.sql.Date.valueOf(fechaCaducidad));
                ps.execute();
            }

            return true;
        }
    }

    public boolean editarConReceta(ProductoVentaDTO producto, List<ProductoCompuestoPorDTO> receta)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "CREATE TEMPORARY TABLE IF NOT EXISTS temp_receta(" +
                    "codigo VARCHAR(5) NOT NULL, " +
                    "cantidad DOUBLE NOT NULL)";

            try (PreparedStatement sentenciaTabla = conn.prepareStatement(consulta)) {
                sentenciaTabla.execute();
            }

            try (PreparedStatement sentenciaLimpiarTabla = conn.prepareStatement("TRUNCATE temp_receta")) {
                sentenciaLimpiarTabla.execute();
            }

            String consultaReceta = "{CALL registrar_receta(?, ?)}";

            try (PreparedStatement sentenciaReceta = conn.prepareCall(consultaReceta)) {
                for (ProductoCompuestoPorDTO item : receta) {
                    sentenciaReceta.setString(1, item.getCodigoInsumo());
                    sentenciaReceta.setDouble(2, item.getCantidad());
                    sentenciaReceta.execute();
                }
            }

            String consultaEdicion = "{CALL editar_producto_venta(?, ?, ?, ?, ?, ?)}";

            try (PreparedStatement sentenciaEdicion = conn.prepareCall(consultaEdicion)) {
                sentenciaEdicion.setString(1, producto.getCodigoMenu());
                sentenciaEdicion.setString(2, producto.getNombre());
                sentenciaEdicion.setDouble(3, producto.getPrecio());
                sentenciaEdicion.setInt(4, producto.getLimite());
                sentenciaEdicion.setString(5, producto.getDescripcion());
                sentenciaEdicion.setString(6, producto.getFoto());
                sentenciaEdicion.execute();
            }

            return true;
        }
    }

    public boolean editarSinReceta(ProductoVentaDTO producto)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "UPDATE producto_venta " +
                    "SET nombre = ?, precio = ?, limite = ?, descripcion = ?, foto = ? " +
                    "WHERE codigo_menu = ?";

            try (PreparedStatement sentencia = conn.prepareStatement(consulta)) {
                sentencia.setString(1, producto.getNombre());
                sentencia.setDouble(2, producto.getPrecio());
                sentencia.setInt(3, producto.getLimite());
                sentencia.setString(4, producto.getDescripcion());
                sentencia.setString(5, producto.getFoto());
                sentencia.setString(6, producto.getCodigoMenu());

                return sentencia.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(String codigoMenu)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "CALL eliminar_producto_venta(?)";

            try (PreparedStatement sentencia = conn.prepareStatement(consulta)) {
                sentencia.setString(1, codigoMenu);
                sentencia.execute();
            }

            return true;
        }
    }

    public List<ProductoVentaDTO> mostrarTodos()
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        List<ProductoVentaDTO> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo_menu, nombre, estatus, precio, limite, descripcion, foto " +
                    "FROM producto_venta WHERE estatus = 1";

            try (PreparedStatement sentencia = conn.prepareStatement(consulta);
                 ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {
                    ProductoVentaDTO productoVenta = new ProductoVentaDTO();

                    productoVenta.setCodigoMenu(resultado.getString("codigo_menu"));
                    productoVenta.setNombre(resultado.getString("nombre"));
                    productoVenta.setEstatus(resultado.getInt("estatus"));
                    productoVenta.setPrecio(resultado.getDouble("precio"));
                    productoVenta.setLimite(resultado.getInt("limite"));
                    productoVenta.setDescripcion(resultado.getString("descripcion"));
                    productoVenta.setFoto(resultado.getString("foto"));

                    lista.add(productoVenta);
                }
            }
        }

        return lista;
    }

    public ProductoVentaDTO buscar(String codigoMenu)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo_menu, nombre, estatus, precio, limite, descripcion, foto " +
                    "FROM producto_venta WHERE codigo_menu = ? AND estatus = 1";

            try (PreparedStatement sentencia = conn.prepareStatement(consulta)) {
                sentencia.setString(1, codigoMenu);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next()) {
                        ProductoVentaDTO producto = new ProductoVentaDTO();

                        producto.setCodigoMenu(resultado.getString("codigo_menu"));
                        producto.setNombre(resultado.getString("nombre"));
                        producto.setEstatus(resultado.getInt("estatus"));
                        producto.setPrecio(resultado.getDouble("precio"));
                        producto.setLimite(resultado.getInt("limite"));
                        producto.setDescripcion(resultado.getString("descripcion"));
                        producto.setFoto(resultado.getString("foto"));

                        return producto;
                    }
                }
            }
        }

        return null;
    }

    public List<ProductoVentaDTO> buscarPorNombre(String nombre)
            throws NullPointerException, IOException, SQLException, ClassNotFoundException {

        List<ProductoVentaDTO> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "SELECT codigo_menu, nombre, estatus, precio, limite, descripcion, foto " +
                    "FROM producto_venta WHERE nombre LIKE ? AND estatus = 1";

            try (PreparedStatement sentencia = conn.prepareStatement(consulta)) {
                sentencia.setString(1, "%" + nombre + "%");

                try (ResultSet resultado = sentencia.executeQuery()) {
                    while (resultado.next()) {
                        ProductoVentaDTO productoVenta = new ProductoVentaDTO();

                        productoVenta.setCodigoMenu(resultado.getString("codigo_menu"));
                        productoVenta.setNombre(resultado.getString("nombre"));
                        productoVenta.setEstatus(resultado.getInt("estatus"));
                        productoVenta.setPrecio(resultado.getDouble("precio"));
                        productoVenta.setLimite(resultado.getInt("limite"));
                        productoVenta.setDescripcion(resultado.getString("descripcion"));
                        productoVenta.setFoto(resultado.getString("foto"));

                        lista.add(productoVenta);
                    }
                }
            }
        }

        return lista;
    }
}