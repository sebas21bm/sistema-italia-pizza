package mx.uv.sistemapizzeria.modelo.dto;

import java.util.List;

public class ClienteDTO extends Persona{

    private int noCliente;

    private List<DireccionDTO> direcciones;

    public ClienteDTO() {}

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

    @Override
    public String toString() {
        return "[" + noCliente + "] " + getNombreCompleto();
    }
}
