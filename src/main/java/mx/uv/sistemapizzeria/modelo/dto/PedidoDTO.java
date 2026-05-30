package mx.uv.sistemapizzeria.modelo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {

    private int idPedido;
    private LocalDateTime fecha;
    private double totalPagar;
    private String estatus;

    // FK + objeto anidado del cliente
    private int noCliente;
    private DireccionDTO direccion;
    private ClienteDTO cliente;

    private List<DetallePedidoDTO> detalles = new ArrayList<>();


    public PedidoDTO() {}


    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getNoCliente() {
        return noCliente;
    }

    public void setNoCliente(int noCliente) {
        this.noCliente = noCliente;
    }

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }

    /** Agrega un detalle a la lista del pedido */
    public void agregarDetalle(DetallePedidoDTO detalle) {
        this.detalles.add(detalle);
    }

    @Override
    public String toString() {
        return "Pedido#" + idPedido + " | " + estatus + " | $" + totalPagar;
    }
}
