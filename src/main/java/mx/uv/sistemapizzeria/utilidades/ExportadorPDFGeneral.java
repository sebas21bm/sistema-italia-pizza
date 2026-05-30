/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.sistemapizzeria.utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import mx.uv.sistemapizzeria.modelo.dto.DetalleReporteDTO;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

/**
 *
 * @author macol
 */
public class ExportadorPDFGeneral {
        public static void generarReporteAValidar(String rutaPdf, List<DetalleReporteDTO> reporteInventario) throws FileNotFoundException, MalformedURLException{
        PdfDocument pdf = new PdfDocument(new PdfWriter(rutaPdf));
        Document documento = new Document(pdf);

        // Logo del sistema
        Image logo = new Image(ImageDataFactory.create(
                "src/main/resources/imagenes/logo pizza pae.png"));
        logo.setWidth(120);
        logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        documento.add(logo);

        //Encabezado
        Paragraph titulo = new Paragraph("LISTA PARA VALIDAR EL INVENTARIO");
        titulo.setBold();
        titulo.setFontSize(20);
        titulo.setTextAlignment(TextAlignment.CENTER);
        titulo.setMarginBottom(20);
        documento.add(titulo);

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
