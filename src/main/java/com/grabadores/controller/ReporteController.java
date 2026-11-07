package com.grabadores.controller;

import com.grabadores.dao.*;
import com.grabadores.model.*;
import com.grabadores.util.PDFExporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la generación de reportes (RF11, RF12).
 * Genera reportes de inventario, formatos, compatibilidad y movimientos por periodo:
 * a) Inventario general de grabadores con estado actual
 * b) Grabadores por formato de grabación soportado
 * c) Grabadores compatibles con una cámara específica
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class ReporteController {

    private final ReporteDAO reporteDAO;
    private final GrabadorExternoDAO grabadorDAO;
    private final InventarioDAO inventarioDAO;
    private final CompatibilidadDAO compatibilidadDAO;
    private final CamaraDAO camaraDAO;
    private final MovimientoInventarioDAO movimientoDAO;

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
        this.grabadorDAO = new GrabadorExternoDAO();
        this.inventarioDAO = new InventarioDAO();
        this.compatibilidadDAO = new CompatibilidadDAO();
        this.camaraDAO = new CamaraDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
    }

    /**
     * Genera el reporte de inventario general (tipo a).
     * Muestra todos los grabadores con su stock y estado actual.
     */
    public List<String[]> generarReporteInventarioGeneral() {
        List<String[]> datos = new ArrayList<>();
        List<GrabadorExterno> grabadores = grabadorDAO.listarTodos();
        for (GrabadorExterno g : grabadores) {
            Inventario inv = inventarioDAO.buscarPorGrabadorId(g.getId());
            String stock = inv != null ? String.valueOf(inv.getStock()) : "Sin registro";
            String estado = inv != null ? inv.getEstado() : "N/A";
            datos.add(new String[]{
                g.getMarca(), g.getModelo(), g.getNumeroSerie(),
                String.valueOf(g.getAnio()), stock, estado,
                String.format("$%.2f", g.getPrecio())
            });
        }
        return datos;
    }

    /**
     * Genera reporte de grabadores por formato de grabación (tipo b).
     *
     * @param formato formato a filtrar (ej: "ProRes RAW")
     */
    public List<GrabadorExterno> generarReportePorFormato(String formato) {
        return grabadorDAO.buscarPorFormato(formato);
    }

    /**
     * Genera reporte de grabadores compatibles con una cámara específica (tipo c).
     *
     * @param camaraId ID de la cámara seleccionada
     * @return lista de grabadores compatibles con info de compatibilidad
     */
    public List<String[]> generarReporteCompatibilidad(int camaraId) {
        List<String[]> datos = new ArrayList<>();
        Camara camara = camaraDAO.buscarPorId(camaraId);
        if (camara == null) return datos;

        List<Compatibilidad> comps = compatibilidadDAO.listarPorCamara(camaraId);
        for (Compatibilidad c : comps) {
            GrabadorExterno g = grabadorDAO.buscarPorId(c.getGrabadorId());
            if (g != null) {
                datos.add(new String[]{
                    g.getMarca(), g.getModelo(), c.getTipoConexion(),
                    c.getNivel(), g.getResolucion(), g.getFormatos()
                });
            }
        }
        return datos;
    }

    public List<MovimientoInventario> generarReporteMovimientos(String desde, String hasta) {
        return movimientoDAO.listarEntreFechas(desde, hasta);
    }

    public List<MovimientoInventario> generarReporteMovimientos(String desde, String hasta,
                                                                 String tipo, int usuarioId) {
        return movimientoDAO.listarEntreFechas(desde, hasta, tipo, usuarioId);
    }

    /**
     * Exporta un reporte a PDF y lo registra en la base de datos.
     */
    public boolean exportarReportePDF(List<GrabadorExterno> grabadores, String titulo,
                                       String usuario, String rutaArchivo, int usuarioId) {
        boolean exito = PDFExporter.exportarGrabadores(grabadores, titulo, usuario, rutaArchivo);
        if (exito) {
            // Registrar el reporte generado en la BD
            Reporte r = new Reporte("inventario_general", usuarioId, titulo);
            reporteDAO.insertar(r);
        }
        return exito;
    }

    public boolean exportarTablaPDF(String[] encabezados, List<String[]> filas, String titulo,
                                    String usuario, String rutaArchivo, int usuarioId) {
        boolean exito = PDFExporter.exportarTabla(encabezados, filas, titulo, usuario, rutaArchivo);
        if (exito && !reporteDAO.insertar(new Reporte(titulo, usuarioId, serializarReporte(titulo, encabezados, filas)))) {
            System.err.println("[ReporteController] El PDF se exportó, pero no se pudo registrar en el historial.");
        }
        return exito;
    }

    private String serializarReporte(String titulo, String[] encabezados, List<String[]> filas) {
        StringBuilder contenido = new StringBuilder(titulo).append("\n\n");
        contenido.append(String.join(" | ", encabezados)).append('\n');
        for (String[] fila : filas) {
            contenido.append(String.join(" | ", fila)).append('\n');
        }
        return contenido.toString();
    }

    public List<Reporte> listarHistorial() {
        return reporteDAO.listarTodos();
    }

    public boolean eliminarReporte(int reporteId) {
        return reporteDAO.eliminar(reporteId);
    }
}
