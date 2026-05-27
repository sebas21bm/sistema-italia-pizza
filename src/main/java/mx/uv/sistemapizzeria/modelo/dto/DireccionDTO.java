package mx.uv.sistemapizzeria.modelo.dto;

public class DireccionDTO {

    private int idDireccion;
    private String calle;
    private String numero;
    private String codigoPostal;
    private String ciudad;

    public DireccionDTO() {}

    public DireccionDTO(int idDireccion, String calle, String numero, String codigoPostal, String ciudad) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.numero = numero;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return calle + " #" + numero + ", " + ciudad + " CP:" + codigoPostal;
    }
}
