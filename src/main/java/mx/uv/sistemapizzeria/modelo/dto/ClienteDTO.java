package mx.uv.sistemapizzeria.modelo.dto;

public class ClienteDTO {

    private int noCliente;
    private String nombre;
    private String paterno;
    private String materno;
    private String telefono;
    private String email;
    private String estatus;

    private DireccionDTO direccion;

    public ClienteDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public int getNoCliente() {
        return noCliente;
    }

    public void setNoCliente(int noCliente) {
        this.noCliente = noCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public String getNombreCompleto() {
        return nombre + " " + paterno + " " + materno;
    }

    @Override
    public String toString() {
        return "[" + noCliente + "] " + getNombreCompleto();
    }
}
