package mx.uv.sistemapizzeria.modelo.dto;

public class EmpleadoDTO extends Persona{

    private String noEmpleado;
    private String usuario;
    private byte[] contrasenia;

    private TipoEmpleado tipoEmpleado;
    private DireccionDTO direccion;

    public EmpleadoDTO() {}

    public String getNoEmpleado() {
        return noEmpleado;
    }
    public void setNoEmpleado(String noEmpleado) {
        this.noEmpleado = noEmpleado;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public byte[] getContrasenia() { return contrasenia; }
    public void setContrasenia(byte[] contrasenia) {
        this.contrasenia = contrasenia;
    }

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(TipoEmpleado tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

}