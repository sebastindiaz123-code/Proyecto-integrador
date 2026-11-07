package com.grabadores.util;

import com.grabadores.model.GrabadorExterno;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilidad para exportar reportes a formato PDF usando iText.
 * Cumple con el requerimiento RF12 de exportación de reportes.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class PDFExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);

    /**
     * Exporta una lista de grabadores a un archivo PDF.
     *
     * @param grabadores  lista de grabadores a incluir en el reporte
     * @param titulo      título del reporte
     * @param usuario     nombre del usuario que genera el reporte
     * @param rutaArchivo ruta completa del archivo PDF de salida
     * @return true si la exportación fue exitosa
     */
    public static boolean exportarGrabadores(List<GrabadorExterno> grabadores,
                                              String titulo, String usuario,
                                              String rutaArchivo) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            // Título
            Paragraph tituloP = new Paragraph(titulo, TITLE_FONT);
            tituloP.setAlignment(Element.ALIGN_CENTER);
            tituloP.setSpacingAfter(5);
            document.add(tituloP);

            // Subtítulo con fecha y usuario
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph subtitulo = new Paragraph(
                    "Generado por: " + usuario + " — Fecha: " + fecha, SUBTITLE_FONT);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            // Línea separadora
            document.add(new Paragraph(" "));

            // Tabla
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 1.5f, 2f, 1f, 2f, 1.5f, 1.5f, 1f});

            // Encabezados
            String[] headers = {"Marca", "Modelo", "Nº Serie", "Año", "Formatos", "Resolución", "Conexiones", "Precio"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, HEADER_FONT));
                cell.setBackgroundColor(new BaseColor(44, 62, 80));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Filas de datos
            for (GrabadorExterno g : grabadores) {
                table.addCell(createCell(g.getMarca()));
                table.addCell(createCell(g.getModelo()));
                table.addCell(createCell(g.getNumeroSerie()));
                table.addCell(createCell(String.valueOf(g.getAnio())));
                table.addCell(createCell(g.getFormatos()));
                table.addCell(createCell(g.getResolucion()));
                table.addCell(createCell(g.getConexiones()));
                table.addCell(createCell(String.format("$%.2f", g.getPrecio())));
            }

            document.add(table);

            // Pie de página
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph(
                    "Total de registros: " + grabadores.size()
                    + " — Sistema de Gestión de Grabadores Externos © 2026",
                    SUBTITLE_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return true;

        } catch (Exception e) {
            System.err.println("[PDFExporter] Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportarTabla(String[] headers, List<String[]> rows,
                                        String titulo, String usuario, String rutaArchivo) {
        if (headers == null || headers.length == 0) return false;
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            Paragraph tituloP = new Paragraph(titulo, TITLE_FONT);
            tituloP.setAlignment(Element.ALIGN_CENTER);
            tituloP.setSpacingAfter(5);
            document.add(tituloP);
            Paragraph subtitulo = new Paragraph(
                    "Generado por: " + usuario + " — Fecha: "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    SUBTITLE_FONT);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
                cell.setBackgroundColor(new BaseColor(44, 62, 80));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }
            for (String[] row : rows) {
                for (int i = 0; i < headers.length; i++) {
                    table.addCell(createCell(i < row.length ? row[i] : null));
                }
            }
            document.add(table);
            Paragraph footer = new Paragraph("Total de registros: " + rows.size(), SUBTITLE_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(12);
            document.add(footer);
            document.close();
            return true;
        } catch (Exception e) {
            System.err.println("[PDFExporter] Error al exportar tabla: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea una celda con estilo uniforme para la tabla del reporte.
     */
    private static PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "—", CELL_FONT));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
}
