package mx.uv.sistemapizzeria.modelo.dto;

public class EmpleadoDTO {

    private String noEmpleado;
    private String usuario;
    private byte[] contrasenia;
    private String nombre;
    private String paterno;
    private String materno;
    private String telefono;
    private String email;
    private String tipoEmpleado;
    private boolean estatus;

    private DireccionDTO direccion;
    private RolDTO rol;

    // Constructor
    public EmpleadoDTO() {}

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getNoEmpleado() { return noEmpleado; }
    public void setNoEmpleado(String noEmpleado) { this.noEmpleado = noEmpleado; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public byte[] getContrasenia() { return contrasenia; }
    public void setContrasenia(byte[] contrasenia) { this.contrasenia = contrasenia; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPaterno() { return paterno; }
    public void setPaterno(String paterno) { this.paterno = paterno; }

    public String getMaterno() { return materno; }
    public void setMaterno(String materno) { this.materno = materno; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipoEmpleado() { return tipoEmpleado; }
    public void setTipoEmpleado(String tipoEmpleado) { this.tipoEmpleado = tipoEmpleado; }

    public boolean isEstatus() { return estatus; }
    public void setEstatus(boolean estatus) { this.estatus = estatus; }

    public DireccionDTO getDireccion() { return direccion; }
    public void setDireccion(DireccionDTO direccion) { this.direccion = direccion; }

    public RolDTO getRol() { return rol; }
    public void setRol(RolDTO rol) { this.rol = rol; }

    // ── Utilidad ─────────────────────────────────────────────────────────────

    public String getNombreCompleto() {
        return nombre + " " + paterno + " " + materno;
    }
}