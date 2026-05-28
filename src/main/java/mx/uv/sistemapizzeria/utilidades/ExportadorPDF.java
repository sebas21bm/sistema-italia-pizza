package mx.uv.sistemapizzeria.utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorPDF {

    // ── Colores corporativos ─────────────────────────────────────────────
    private static final DeviceRgb COLOR_ENCABEZADO   = new DeviceRgb(180, 30,  30);
    private static final DeviceRgb COLOR_FILA_PAR     = new DeviceRgb(255, 240, 240);
    private static final DeviceRgb COLOR_TEXTO_BLANCO = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb COLOR_GRIS         = new DeviceRgb(169, 169, 169);
    private static final DeviceRgb COLOR_NARANJA      = new DeviceRgb(180, 100,  0);
    private static final DeviceRgb COLOR_VERDE        = new DeviceRgb(0,   130,  0);
    private static final DeviceRgb COLOR_ROJO         = new DeviceRgb(180,   0,  0);

    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera el archivo PDF en la ruta indicada.
     *
     * @param pedidos      lista de PedidoDTO a incluir (con detalles cargados)
     * @param rutaArchivo  ruta absoluta del archivo destino (.pdf)
     * @throws FileNotFoundException si no se puede escribir en la ruta indicada
     */
    public static void exportar(List<PedidoDTO> pedidos, String rutaArchivo)
            throws FileNotFoundException {

        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdf  = new PdfDocument(writer);
        // A4 horizontal
        Document doc = new Document(pdf, PageSize.A4.rotate());
        doc.setMargins(40, 30, 30, 30);

        agregarEncabezadoDocumento(doc, pedidos.size());
        doc.add(crearTablaPedidos(pedidos));
        agregarPiePagina(doc);

        doc.close();
    }

    // ── Encabezado del documento ─────────────────────────────────────
    private static void agregarEncabezadoDocumento(Document doc, int totalPedidos) {
        // Tabla de dos columnas: [logo] | [título + subtítulo]
        Table tablaEncabezado = new Table(UnitValue.createPercentArray(new float[]{8, 92}))
                .useAllAvailableWidth()
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

        // ── Celda izquierda: logo mini ──────────────────────────────────
        Cell celdaLogo = new Cell()
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        try {
            InputStream isLogo = ExportadorPDF.class
                    .getResourceAsStream("/imagenes/logo mini.png");
            if (isLogo != null) {
                byte[] logoBytes = isLogo.readAllBytes();
                isLogo.close();
                Image logo = new Image(ImageDataFactory.create(logoBytes))
                        .setHeight(45f)
                        .setAutoScale(false);
                celdaLogo.add(logo);
            }
        } catch (Exception ignored) {
            // Si no se puede cargar el logo, la celda queda vacía sin romper el PDF
        }
        tablaEncabezado.addCell(celdaLogo);

        // ── Celda derecha: título y subtítulo ─────────────────────────
        String ahora = java.time.LocalDateTime.now().format(FMT_FECHA);

        Cell celdaTexto = new Cell()
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph("Italia Pizza")
                        .setFontSize(18)
                        .setBold()
                        .setFontColor(COLOR_ENCABEZADO)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(2f))
                .add(new Paragraph(
                        "Reporte de Pedidos  |  Generado: " + ahora + "  |  Total registros: " + totalPedidos)
                        .setFontSize(10)
                        .setFontColor(new DeviceRgb(64, 64, 64))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(0f));
        tablaEncabezado.addCell(celdaTexto);

        doc.add(tablaEncabezado);

        // Línea separadora
        doc.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f))
                .setStrokeColor(COLOR_ENCABEZADO)
                .setMarginTop(4f)
                .setMarginBottom(8f));
    }
    // ── Tabla con todos los pedidos ──────────────────────────────────────
    private static Table crearTablaPedidos(List<PedidoDTO> pedidos) {
        // Anchos relativos: Folio | Cliente | Fecha | Productos | Total | Estatus
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{6, 18, 14, 40, 10, 12}))
                .useAllAvailableWidth()
                .setMarginTop(4f)
                .setBorderCollapse(BorderCollapsePropertyValue.SEPARATE)
                .setHorizontalBorderSpacing(0f)
                .setVerticalBorderSpacing(0f);

        // Fila de encabezados
        for (String col : new String[]{"Folio", "Cliente", "Fecha", "Productos", "Total", "Estatus"}) {
            tabla.addHeaderCell(celdaEncabezado(col));
        }

        // Filas de datos
        double granTotal = 0;
        for (int i = 0; i < pedidos.size(); i++) {
            PedidoDTO p = pedidos.get(i);
            DeviceRgb fondo = (i % 2 == 0) ? null : COLOR_FILA_PAR;
            granTotal += p.getTotalPagar();

            tabla.addCell(celda(String.valueOf(p.getIdPedido()),   fondo, TextAlignment.CENTER));

            String cliente = (p.getCliente() != null)
                    ? p.getCliente().getNombreCompleto() : "N/A";
            tabla.addCell(celda(cliente, fondo, TextAlignment.LEFT));

            String fecha = (p.getFecha() != null) ? p.getFecha().format(FMT_FECHA) : "-";
            tabla.addCell(celda(fecha, fondo, TextAlignment.CENTER));

            tabla.addCell(celdaProductos(p, fondo));

            tabla.addCell(celda(String.format("$%.2f", p.getTotalPagar()), fondo, TextAlignment.RIGHT));

            tabla.addCell(celdaEstatus(p.getEstatus(), fondo));
        }

        // Fila de total general
        agregarFilaTotalGeneral(tabla, granTotal);

        return tabla;
    }

    // ── Celda de productos (lista interna) ───────────────────────────────
    private static Cell celdaProductos(PedidoDTO pedido, DeviceRgb fondo) {
        Cell celda = new Cell()
                .setPadding(4f)
                .setBorder(new SolidBorder(COLOR_GRIS, 1f));

        if (fondo != null) celda.setBackgroundColor(fondo);

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            celda.add(new Paragraph("-").setFontSize(9));
        } else {
            for (DetallePedidoDTO det : pedido.getDetalles()) {
                String nombreProducto = (det.getProductoVenta() != null)
                        ? det.getProductoVenta().getNombre()
                        : det.getCodigoMenu();
                String linea = det.getCantidad() + "x " + nombreProducto
                        + "  ($" + String.format("%.2f", det.getSubtotal()) + ")";
                celda.add(new Paragraph(linea).setFontSize(9).setMargin(0));
            }
        }
        return celda;
    }

    // ── Celda de estatus con color semáforo ──────────────────────────────
    private static Cell celdaEstatus(String estatus, DeviceRgb fondo) {
        if (estatus == null) estatus = "-";

        DeviceRgb colorTexto;
        if ("En proceso".equals(estatus)) {
            colorTexto = COLOR_NARANJA;
        } else if ("Entregado".equals(estatus)) {
            colorTexto = COLOR_VERDE;
        } else if ("Cancelado".equals(estatus)) {
            colorTexto = COLOR_ROJO;
        } else {
            colorTexto = new DeviceRgb(0, 0, 0);
        }

        Cell celda = new Cell()
                .add(new Paragraph(estatus).setFontSize(9).setBold().setFontColor(colorTexto))
                .setPadding(4f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(COLOR_GRIS, 1f));

        if (fondo != null) celda.setBackgroundColor(fondo);
        return celda;
    }

    // ── Fila de total general ────────────────────────────────────────────
    private static void agregarFilaTotalGeneral(Table tabla, double granTotal) {
        // Celda vacía colspan=4
        tabla.addCell(new Cell(1, 4)
                .setBorderTop(new SolidBorder(COLOR_ENCABEZADO, 1.5f))
                .setBorderBottom(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER));

        tabla.addCell(new Cell()
                .add(new Paragraph("TOTAL").setFontSize(9).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5f)
                .setBorderTop(new SolidBorder(COLOR_ENCABEZADO, 1.5f))
                .setBorderBottom(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER));

        tabla.addCell(new Cell()
                .add(new Paragraph(String.format("$%.2f", granTotal))
                        .setFontSize(11).setBold().setFontColor(COLOR_ENCABEZADO))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5f)
                .setBorderTop(new SolidBorder(COLOR_ENCABEZADO, 1.5f))
                .setBorderBottom(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER));
    }

    // ── Pie de página ────────────────────────────────────────────────────
    private static void agregarPiePagina(Document doc) {
        doc.add(new Paragraph("* Reporte generado automáticamente por Sistema Italia Pizza.")
                .setFontSize(8)
                .setItalic()
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10f));
    }

    // ── Helpers de celda ─────────────────────────────────────────────────
    private static Cell celdaEncabezado(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(10).setBold().setFontColor(COLOR_TEXTO_BLANCO))
                .setBackgroundColor(COLOR_ENCABEZADO)
                .setPadding(6f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1f));
    }

    private static Cell celda(String texto, DeviceRgb fondo, TextAlignment alineacion) {
        Cell celda = new Cell()
                .add(new Paragraph(texto).setFontSize(9))
                .setPadding(4f)
                .setTextAlignment(alineacion)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(COLOR_GRIS, 1f));

        if (fondo != null) celda.setBackgroundColor(fondo);
        return celda;
    }
}