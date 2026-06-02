package mx.uv.sistemapizzeria.modelo.dao;

import mx.uv.sistemapizzeria.db.ConnectionFactory;
import mx.uv.sistemapizzeria.modelo.dto.*;
import mx.uv.sistemapizzeria.utilidades.Constantes;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleReporteInventarioDAO {

    public List<DetalleReporteDTO> mostrarTodos() throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        List<DetalleReporteDTO> lista = new ArrayList<>();

        try(Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())){
            if (conn == null){
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }
            String consulta = "SELECT codigo, nombre, existencias FROM producto_inventario WHERE estatus = 1";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                DetalleReporteDTO productoInventario = new DetalleReporteDTO();
                productoInventario.setCodigo(resultado.getString("codigo"));
                productoInventario.setDescripcionProductoInventario(resultado.getString("nombre"));
                productoInventario.setExistencias(resultado.getDouble("existencias"));
                lista.add(productoInventario);
            }
            return lista;
        }
    }


    public boolean registrarDetalle(List<DetalleReporteDTO> detallesReporte) throws NullPointerException, IOException, SQLException, ClassNotFoundException {
        try (Connection conn = ConnectionFactory.crearParaRol(Sesion.empleadoSesion.getTipoEmpleado())) {
            if (conn == null) {
                throw new SQLException(Constantes.MSJ_SIN_CONEXION);
            }

            String consulta = "CREATE TEMPORARY TABLE temp_detalles_reporte( " +
                    "codigo VARCHAR(5) NOT NULL, " +
                    "diferencia DOUBLE NOT NULL, " +
                    "justificacion VARCHAR(255) NOT NULL" +
                    ");";
            PreparedStatement sentenciaTabla = conn.prepareStatement(consulta);
            sentenciaTabla.executeUpdate();


            PreparedStatement sentenciaLimpiarTabla = conn.prepareStatement("TRUNCATE temp_detalles_reporte;");
            sentenciaLimpiarTabla.executeUpdate();

            for(DetalleReporteDTO detalle : detallesReporte){
                    String consultaDetalle = "CALL registrar_detalle_reporte( ?, ?, ?);";
                    PreparedStatement sentencia = conn.prepareStatement(consultaDetalle);
                    sentencia.setString(1, detalle.getCodigo());
                    sentencia.setDouble(2, detalle.getDiferencia());
                    sentencia.setString(3, detalle.getJustificacion());
                    sentencia.execute();
            }
            PreparedStatement sentenciaReporte = conn.prepareStatement("CALL registrar_reporte();");
            sentenciaReporte.execute();

            return true;
        }
    }

}
