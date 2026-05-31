package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.ProductoCompuestoPorDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInventarioDTO;
import mx.uv.sistemapizzeria.modelo.dto.Sesion;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoCompuestoPorDAO{

    public List<ProductoCompuestoPorDTO> obtenerReceta(String codigoMenu) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<ProductoCompuestoPorDTO> receta = new ArrayList<>();

        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) throw new SQLException(Constantes.MSJ_SIN_CONEXION);

            String sql = "SELECT codigo_menu, codigo, cantidad, nombre FROM vista_producto_detalles WHERE codigo_menu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigoMenu);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        ProductoCompuestoPorDTO pc = new ProductoCompuestoPorDTO();
                        pc.setCodigoInsumo(rs.getString("codigo"));
                        pc.setCodigoMenu(rs.getString("codigo_menu"));
                        pc.setCantidad(rs.getDouble("cantidad"));
                        pc.setNombreProductoInventario(rs.getString("nombre"));
                        receta.add(pc);
                    }
                }
            }
        }
        return receta;
    }
}
