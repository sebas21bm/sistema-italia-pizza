package mx.uv.sistemapizzeria.utilidades;

import com.opencsv.CSVWriter;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporta una lista de pedidos a un archivo CSV utilizando la librería opencsv.
 *
 * El archivo resultante tiene dos secciones:
 *   1. Una fila por pedido con los datos generales.
 *   2. Debajo de cada pedido, las líneas de detalle (productos).
 */
public class ExportadorCSV {

    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera el archivo CSV en la ruta indicada.
     *
     * @param pedidos     lista de PedidoDTO a exportar (con detalles cargados)
     * @param rutaArchivo ruta absoluta del archivo destino (.csv)
     * @throws IOException si no se puede escribir en la ruta indicada
     */
    public static void exportar(List<PedidoDTO> pedidos, String rutaArchivo)
            throws IOException {

        // BOM UTF-8 para que Excel lo abra correctamente con tildes y ñ
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            // BOM
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            // ── Encabezados de pedido ─────────────────────────────────────
            writer.writeNext(new String[]{
                "Folio", "Cliente", "Fecha", "Estatus", "Total"
            });

            for (PedidoDTO pedido : pedidos) {

                // ── Fila del pedido ───────────────────────────────────────
                String folio   = String.valueOf(pedido.getIdPedido());
                String cliente = (pedido.getCliente() != null)
                        ? pedido.getCliente().getNombreCompleto()
                        : "N/A";
                String fecha   = (pedido.getFecha() != null)
                        ? pedido.getFecha().format(FMT_FECHA)
                        : "-";
                String estatus = (pedido.getEstatus() != null)
                        ? pedido.getEstatus()
                        : "-";
                String total   = String.format("%.2f", pedido.getTotalPagar());

                writer.writeNext(new String[]{folio, cliente, fecha, estatus, total});

                // ── Filas de detalle (productos del pedido) ───────────────
                List<DetallePedidoDTO> detalles = pedido.getDetalles();
                if (detalles != null && !detalles.isEmpty()) {

                    // Sub-encabezado de detalle
                    writer.writeNext(new String[]{
                        "", "  >> Código", "  >> Producto", "  >> Cantidad", "  >> Subtotal"
                    });

                    for (DetallePedidoDTO det : detalles) {
                        String codigo   = det.getCodigoMenu() != null ? det.getCodigoMenu() : "-";
                        String nombre   = (det.getProductoVenta() != null)
                                ? det.getProductoVenta().getNombre()
                                : codigo;
                        String cantidad = String.valueOf(det.getCantidad());
                        String subtotal = String.format("%.2f", det.getSubtotal());

                        writer.writeNext(new String[]{
                            "", codigo, nombre, cantidad, subtotal
                        });
                    }
                }

                // Fila en blanco entre pedidos para legibilidad
                writer.writeNext(new String[]{"", "", "", "", ""});
            }

            // ── Fila de total general ─────────────────────────────────────
            double granTotal = pedidos.stream()
                    .mapToDouble(PedidoDTO::getTotalPagar)
                    .sum();
            writer.writeNext(new String[]{"TOTAL GENERAL", "", "", "", String.format("%.2f", granTotal)});
        }
    }
}
