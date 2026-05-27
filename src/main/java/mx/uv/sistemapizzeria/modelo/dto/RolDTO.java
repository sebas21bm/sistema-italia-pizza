package mx.uv.sistemapizzeria.modelo.dto;

/**
 * DTO que mapea la tabla: rol
 * Campos: id_rol, nombre_rol
 */
public class RolDTO {

    private int idRol;
    private String nombreRol;

    public RolDTO() {}

    public RolDTO(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}
