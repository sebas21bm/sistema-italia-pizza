package mx.uv.sistemapizzeria.modelo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteInventarioDTO {

    private int idInventario;
    private LocalDateTime fecha;

    private List<DetalleReporteDTO> detalles = new ArrayList<>();

    public ReporteInventarioDTO() {

    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<DetalleReporteDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReporteDTO> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetalleReporteDTO detalle) {
        this.detalles.add(detalle);
    }

    @Override
    public String toString() {
        return "Reporte#" + idInventario + " - " + fecha;
    }
}
