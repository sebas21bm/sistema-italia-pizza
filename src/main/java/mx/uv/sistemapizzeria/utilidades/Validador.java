package mx.uv.sistemapizzeria.utilidades;

import mx.uv.sistemapizzeria.modelo.dto.ClienteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DireccionDTO;
import mx.uv.sistemapizzeria.modelo.dto.EmpleadoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoInsumoDTO;
import mx.uv.sistemapizzeria.modelo.dto.ProductoVentaDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para validar entidades del sistema de pizzería.
 *
 * Las reglas aquí reflejan exactamente las restricciones CHECK definidas
 * en la base de datos (ALTER TABLE … ADD CONSTRAINT …), de modo que los
 * errores se detecten en la capa de negocio antes de llegar a la BD.
 *
 * Todas las validaciones son estáticas y sin estado.
 */
public class Validador {

    // ── Patrones (idénticos a los REGEXP de la BD) ─────────────────────────

    /** Correo electrónico: letras/dígitos/símbolos @ dominio .ext(2-4) */
    private static final String PATRON_EMAIL =
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";

    /** Teléfono: exactamente 10 dígitos */
    private static final String PATRON_TELEFONO = "^[0-9]{10}$";

    /** Número de empleado: E seguido de 4 dígitos, ej. E0123 */
    private static final String PATRON_NO_EMPLEADO = "^E[0-9]{4}$";

    /** Código postal: exactamente 5 dígitos */
    private static final String PATRON_CODIGO_POSTAL = "^[0-9]{5}$";

    /** Código de producto de inventario: I seguido de 4 dígitos, ej. I0001 */
    private static final String PATRON_CODIGO_INSUMO = "^I[0-9]{4}$";

    /** Código de producto de venta (menú): P seguido de 4 dígitos, ej. P0001 */
    private static final String PATRON_CODIGO_MENU = "^P[0-9]{4}$";

    // Constructor privado: clase solo de métodos estáticos
    private Validador() {}

    // ══════════════════════════════════════════════════════════════════════
    // VALIDACIONES PRIMITIVAS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Verifica que una cadena no sea nula ni esté en blanco.
     *
     * @param valor cadena a evaluar
     * @return {@code true} si contiene al menos un carácter no espaciado
     */
    public static boolean esNoVacio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    /**
     * Valida el formato del correo electrónico.
     * Regla BD: {@code REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}$'}
     */
    public static boolean esEmailValido(String email) {
        return esNoVacio(email) && email.matches(PATRON_EMAIL);
    }

    /**
     * Valida el formato del teléfono.
     * Regla BD: {@code REGEXP '^[0-9]{10}$'}
     */
    public static boolean esTelefonoValido(String telefono) {
        return esNoVacio(telefono) && telefono.matches(PATRON_TELEFONO);
    }

    /**
     * Valida el formato del número de empleado.
     * Regla BD: {@code REGEXP '^E[0-9]{4}$'}
     */
    public static boolean esNoEmpleadoValido(String noEmpleado) {
        return esNoVacio(noEmpleado) && noEmpleado.matches(PATRON_NO_EMPLEADO);
    }

    /**
     * Valida el formato del código postal.
     * Regla BD: {@code REGEXP '^[0-9]{5}$'}
     */
    public static boolean esCodigoPostalValido(String codigoPostal) {
        return esNoVacio(codigoPostal) && codigoPostal.matches(PATRON_CODIGO_POSTAL);
    }

    /**
     * Valida el formato del código de insumo (inventario).
     * Regla BD: {@code REGEXP '^I[0-9]{4}$'}
     */
    public static boolean esCodigoInsumoValido(String codigo) {
        return esNoVacio(codigo) && codigo.matches(PATRON_CODIGO_INSUMO);
    }

    /**
     * Valida el formato del código de menú (producto de venta).
     * Regla BD: {@code REGEXP '^P[0-9]{4}$'}
     */
    public static boolean esCodigoMenuValido(String codigoMenu) {
        return esNoVacio(codigoMenu) && codigoMenu.matches(PATRON_CODIGO_MENU);
    }

    /**
     * Valida que un precio sea estrictamente positivo.
     * Regla BD: {@code CHECK (precio > 0)}
     */
    public static boolean esPrecioValido(double precio) {
        return precio > 0;
    }

    /**
     * Valida que un total no sea negativo.
     * Regla BD: {@code CHECK (total_pagar >= 0)}
     */
    public static boolean esTotalValido(double total) {
        return total >= 0;
    }

    /**
     * Valida que una cantidad sea estrictamente positiva.
     * Regla BD: {@code CHECK (cantidad > 0)} en detalles_pedido y producto_compuesto_por
     */
    public static boolean esCantidadPositiva(int cantidad) {
        return cantidad > 0;
    }

    /**
     * Valida que las existencias no sean negativas.
     * Regla BD: {@code CHECK (existencias >= 0)}
     */
    public static boolean esExistenciasValida(int existencias) {
        return existencias >= 0;
    }

    /**
     * Valida que un costo no sea negativo.
     * Regla BD: {@code CHECK (costo >= 0)} en detalles_pedido
     */
    public static boolean esCostoValido(double costo) {
        return costo >= 0;
    }

    /**
     * Valida que el límite de un producto de venta sea estrictamente positivo.
     * Regla BD: {@code CHECK (limite > 0)}
     */
    public static boolean esLimiteValido(int limite) {
        return limite > 0;
    }

    // ══════════════════════════════════════════════════════════════════════
    // VALIDACIONES DE DTOs
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Valida todos los campos requeridos de un {@link ClienteDTO}.
     *
     * <ul>
     *   <li>Nombre, apellido paterno y materno no vacíos</li>
     *   <li>Teléfono: exactamente 10 dígitos</li>
     *   <li>Email: formato válido</li>
     * </ul>
     *
     * @param cliente objeto a validar (no nulo)
     * @return lista de mensajes de error; vacía si el objeto es válido
     */
    public static List<String> validarCliente(ClienteDTO cliente) {
        List<String> errores = new ArrayList<>();

        if (!esNoVacio(cliente.getNombre())) {
            errores.add("El nombre del cliente no puede estar vacío.");
        }
        if (!esNoVacio(cliente.getPaterno())) {
            errores.add("El apellido paterno del cliente no puede estar vacío.");
        }
        if (!esNoVacio(cliente.getMaterno())) {
            errores.add("El apellido materno del cliente no puede estar vacío.");
        }
        if (!esTelefonoValido(cliente.getTelefono())) {
            errores.add("El teléfono debe contener exactamente 10 dígitos numéricos.");
        }
        if (!esEmailValido(cliente.getEmail())) {
            errores.add("El correo electrónico no tiene un formato válido.");
        }

        return errores;
    }

    /**
     * Valida todos los campos requeridos de un {@link EmpleadoDTO}.
     *
     * <ul>
     *   <li>Número de empleado: formato E[0-9]{4}</li>
     *   <li>Nombre, apellido paterno y materno no vacíos</li>
     *   <li>Teléfono: exactamente 10 dígitos</li>
     *   <li>Email: formato válido</li>
     *   <li>Tipo de empleado no nulo</li>
     * </ul>
     *
     * @param empleado objeto a validar (no nulo)
     * @return lista de mensajes de error; vacía si el objeto es válido
     */
    public static List<String> validarEmpleado(EmpleadoDTO empleado) {
        List<String> errores = new ArrayList<>();

        if (!esNoEmpleadoValido(empleado.getNoEmpleado())) {
            errores.add("El número de empleado debe tener el formato E seguido de 4 dígitos (ej. E0123).");
        }
        if (!esNoVacio(empleado.getNombre())) {
            errores.add("El nombre del empleado no puede estar vacío.");
        }
        if (!esNoVacio(empleado.getPaterno())) {
            errores.add("El apellido paterno del empleado no puede estar vacío.");
        }
        if (!esNoVacio(empleado.getMaterno())) {
            errores.add("El apellido materno del empleado no puede estar vacío.");
        }
        if (!esTelefonoValido(empleado.getTelefono())) {
            errores.add("El teléfono debe contener exactamente 10 dígitos numéricos.");
        }
        if (!esEmailValido(empleado.getEmail())) {
            errores.add("El correo electrónico no tiene un formato válido.");
        }
        if (empleado.getTipoEmpleado() == null) {
            errores.add("Debe seleccionar un tipo de empleado.");
        }

        return errores;
    }

    /**
     * Valida todos los campos requeridos de una {@link DireccionDTO}.
     *
     * <ul>
     *   <li>Calle y número no vacíos</li>
     *   <li>Código postal: exactamente 5 dígitos</li>
     *   <li>Ciudad no vacía</li>
     * </ul>
     *
     * @param direccion objeto a validar (no nulo)
     * @return lista de mensajes de error; vacía si el objeto es válido
     */
    public static List<String> validarDireccion(DireccionDTO direccion) {
        List<String> errores = new ArrayList<>();

        if (!esNoVacio(direccion.getCalle())) {
            errores.add("La calle no puede estar vacía.");
        }
        if (!esNoVacio(direccion.getNumero())) {
            errores.add("El número exterior no puede estar vacío.");
        }
        if (!esCodigoPostalValido(direccion.getCodigoPostal())) {
            errores.add("El código postal debe contener exactamente 5 dígitos.");
        }
        if (!esNoVacio(direccion.getCiudad())) {
            errores.add("La ciudad no puede estar vacía.");
        }

        return errores;
    }

    /**
     * Valida todos los campos requeridos de un {@link ProductoVentaDTO}.
     *
     * <ul>
     *   <li>Código de menú: formato P[0-9]{4}</li>
     *   <li>Nombre no vacío</li>
     *   <li>Precio > 0</li>
     *   <li>Límite > 0</li>
     * </ul>
     *
     * @param producto objeto a validar (no nulo)
     * @return lista de mensajes de error; vacía si el objeto es válido
     */
    public static List<String> validarProductoVenta(ProductoVentaDTO producto) {
        List<String> errores = new ArrayList<>();

        if (!esCodigoMenuValido(producto.getCodigoMenu())) {
            errores.add("El código de menú debe tener el formato P seguido de 4 dígitos (ej. P0001).");
        }
        if (!esNoVacio(producto.getNombre())) {
            errores.add("El nombre del producto no puede estar vacío.");
        }
        if (!esPrecioValido(producto.getPrecio())) {
            errores.add("El precio debe ser mayor a 0.");
        }
        if (!esLimiteValido(producto.getLimite())) {
            errores.add("El límite de unidades debe ser mayor a 0.");
        }

        return errores;
    }

    /**
     * Valida todos los campos requeridos de un {@link ProductoInsumoDTO}.
     *
     * <ul>
     *   <li>Código: formato I[0-9]{4}</li>
     *   <li>Nombre no vacío</li>
     *   <li>Existencias >= 0</li>
     * </ul>
     *
     * @param insumo objeto a validar (no nulo)
     * @return lista de mensajes de error; vacía si el objeto es válido
     */
    public static List<String> validarProductoInsumo(ProductoInsumoDTO insumo) {
        List<String> errores = new ArrayList<>();

        if (!esCodigoInsumoValido(insumo.getCodigo())) {
            errores.add("El código de insumo debe tener el formato I seguido de 4 dígitos (ej. I0001).");
        }
        if (!esNoVacio(insumo.getNombre())) {
            errores.add("El nombre del insumo no puede estar vacío.");
        }
        if (!esExistenciasValida(insumo.getExistencias())) {
            errores.add("Las existencias no pueden ser negativas.");
        }

        return errores;
    }

    // ══════════════════════════════════════════════════════════════════════
    // UTILIDAD DE PRESENTACIÓN
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Convierte una lista de errores en un único mensaje formateado,
     * listo para mostrarse en un {@code Alert} de JavaFX.
     *
     * @param errores lista devuelta por cualquier método {@code validar*}
     * @return cadena con los errores numerados, o cadena vacía si no hay errores
     */
    public static String formatearErrores(List<String> errores) {
        if (errores == null || errores.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errores.size(); i++) {
            sb.append(i + 1).append(". ").append(errores.get(i));
            if (i < errores.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}