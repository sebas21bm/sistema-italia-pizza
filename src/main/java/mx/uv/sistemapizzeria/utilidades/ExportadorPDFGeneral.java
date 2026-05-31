package mx.uv.sistemapizzeria.utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import mx.uv.sistemapizzeria.modelo.dto.DetalleReporteDTO;
import mx.uv.sistemapizzeria.modelo.dto.DetallePedidoDTO;
import mx.uv.sistemapizzeria.modelo.dto.PedidoDTO;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorPDFGeneral {

    private static final DeviceRgb COLOR_ENCABEZADO = new DeviceRgb(204, 0, 0);
    private static final DeviceRgb COLOR_NARANJA    = new DeviceRgb(180, 100, 0);
    private static final DeviceRgb COLOR_VERDE      = new DeviceRgb(0, 130, 0);
    private static final DeviceRgb COLOR_ROJO       = new DeviceRgb(180, 0, 0);
    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void generarReportePedidos(String rutaPdf, List<PedidoDTO> pedidos)
            throws FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf, PageSize.A4.rotate());
        documento.setMargins(40, 40, 40, 40);

        agregarLogoYTitulo(documento, "REPORTE DE PEDIDOS");

        // Tabla: FOLIO | CLIENTE | FECHA | PRODUCTOS | TOTAL | ESTATUS
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{6, 16, 13, 40, 10, 12}))
                .useAllAvailableWidth();

        for (String col : new String[]{"FOLIO", "CLIENTE", "FECHA", "PRODUCTOS", "TOTAL", "ESTATUS"}) {
            tabla.addHeaderCell(crearHeaderTablaIText(col));
        }

        double granTotal = 0;
        for (PedidoDTO p : pedidos) {
            granTotal += p.getTotalPagar();

            tabla.addCell(crearCeldaTablaIText(String.valueOf(p.getIdPedido())));

            String cliente = (p.getCliente() != null) ? p.getCliente().getNombreCompleto() : "N/A";
            tabla.addCell(crearCeldaTablaIText(cliente));

            String fecha = (p.getFecha() != null) ? p.getFecha().format(FMT_FECHA) : "-";
            tabla.addCell(crearCeldaTablaIText(fecha));

            // Celda de productos
            Cell celdaProductos = new Cell().setPadding(6f).setVerticalAlignment(VerticalAlignment.MIDDLE);
            if (p.getDetalles() == null || p.getDetalles().isEmpty()) {
                celdaProductos.add(new Paragraph("-").setFontSize(9));
            } else {
                for (DetallePedidoDTO det : p.getDetalles()) {
                    String nombre = (det.getProductoVenta() != null)
                            ? det.getProductoVenta().getNombre() : det.getCodigoMenu();
                    celdaProductos.add(new Paragraph(det.getCantidad() + "x " + nombre
                            + "  ($" + String.format("%.2f", det.getSubtotal()) + ")")
                            .setFontSize(9).setMargin(0));
                }
            }
            tabla.addCell(celdaProductos);

            tabla.addCell(crearCeldaTablaIText(String.format("$%.2f", p.getTotalPagar())));

            // Celda de estatus con color
            String estatus = p.getEstatus() != null ? p.getEstatus() : "-";
            DeviceRgb colorEstatus = switch (estatus) {
                case "En proceso" -> COLOR_NARANJA;
                case "Entregado"  -> COLOR_VERDE;
                case "Cancelado"  -> COLOR_ROJO;
                default           -> new DeviceRgb(0, 0, 0);
            };
            tabla.addCell(new Cell()
                    .add(new Paragraph(estatus).setFontSize(9).setBold().setFontColor(colorEstatus))
                    .setPadding(6f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        // Fila de total general
        tabla.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER));
        tabla.addCell(new Cell()
                .add(new Paragraph("TOTAL").setFontSize(10).setBold())
                .setTextAlignment(TextAlignment.RIGHT).setPadding(6f)
                .setBorderTop(new SolidBorder(COLOR_ENCABEZADO, 1.5f))
                .setBorderBottom(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));
        tabla.addCell(new Cell()
                .add(new Paragraph(String.format("$%.2f", granTotal))
                        .setFontSize(11).setBold().setFontColor(COLOR_ENCABEZADO))
                .setTextAlignment(TextAlignment.RIGHT).setPadding(6f)
                .setBorderTop(new SolidBorder(COLOR_ENCABEZADO, 1.5f))
                .setBorderBottom(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));

        documento.add(tabla);

        documento.add(new Paragraph("* Reporte generado automáticamente por Sistema Italia Pizza.")
                .setFontSize(8).setItalic().setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f));

        documento.close();
    }
    private static void agregarLogoYTitulo(Document documento, String tituloTexto) {
        try {
            Image logo = new Image(ImageDataFactory.create(
                    "src/main/resources/imagenes/logo pizza pae.png"));
            logo.setWidth(120);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            documento.add(logo);
        } catch (Exception ignored) {}

        documento.add(new Paragraph(tituloTexto)
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10)
                .setMarginBottom(20));
    }

    public static void generarReporteAValidar(String rutaPdf, List<DetalleReporteDTO> reporteInventario) throws FileNotFoundException, MalformedURLException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf);

        agregarLogoYTitulo(documento, "LISTA PARA VALIDAR EL INVENTARIO");

        // Tabla para los productos de inventario que se van a validar
        Table detallesReporte = new Table(4);
        detallesReporte.useAllAvailableWidth();

        detallesReporte.addHeaderCell(crearHeaderTablaIText("CÓDIGO"));
        detallesReporte.addHeaderCell(crearHeaderTablaIText("PRODUCTO DE INVENTARIO"));
        detallesReporte.addHeaderCell(crearHeaderTablaIText("EXISTENCIAS"));
        detallesReporte.addHeaderCell(crearHeaderTablaIText("CONTEO FÍSICO"));

        for (DetalleReporteDTO detalle : reporteInventario) {
            String codigo = detalle.getCodigo();
            String productoInventario = detalle.getDescripcionProductoInventario();
            String existecias = String.format("%.2f", detalle.getExistencias());
            detallesReporte.addCell(crearCeldaTablaIText(codigo));
            detallesReporte.addCell(crearCeldaTablaIText(productoInventario));
            detallesReporte.addCell(crearCeldaTablaIText(existecias));
            detallesReporte.addCell(crearCeldaTablaIText(""));
        }
        documento.add(detallesReporte);
        documento.close();
    }
    
    private static Cell crearCeldaSinBordesIText(String texto) {
        Paragraph parrafo = new Paragraph(texto);
        Cell celda = new Cell();
        celda.add(parrafo);
        celda.setBorder(Border.NO_BORDER);
        return celda;
    }
    
    private static Cell crearCeldaTablaIText(String texto) {
        Paragraph parrafo = new Paragraph(texto);
        Cell celda = new Cell();
        celda.add(parrafo);
        return celda;
    }
    
    private static Cell crearHeaderTablaIText(String texto) {
        Paragraph parrafo = new Paragraph(texto);
        parrafo.setBold();
        parrafo.setFontColor(ColorConstants.WHITE);
        Cell celda = new Cell();
        celda.add(parrafo);
        celda.setBackgroundColor(ColorConstants.RED);
        celda.setTextAlignment(TextAlignment.CENTER);
        celda.setPadding(8);
        return celda;
    }
}
