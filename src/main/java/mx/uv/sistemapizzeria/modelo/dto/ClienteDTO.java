package mx.uv.sistemapizzeria.modelo.dto;

import java.util.List;

public class ClienteDTO extends Persona{

    private int noCliente;

    private List<DireccionDTO> direcciones;

    public ClienteDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public int getNoCliente() {
        return noCliente;
    }

    public void setNoCliente(int noCliente) {
        this.noCliente = noCliente;
    }

    public List<DireccionDTO> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<DireccionDTO> direcciones) {
        this.direcciones = direcciones;
    }

    public String getNombreCompleto() {
        return (this.getNombre() != null ? this.getNombre() : "") + " "
                + (this.getPaterno() != null ? this.getPaterno() : "") + " "
                + (this.getMaterno() != null ? this.getMaterno() : "");
    }

    @Override
    public String toString() {
        return "[" + noCliente + "] " + getNombreCompleto();
    }
}
