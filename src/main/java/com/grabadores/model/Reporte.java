package com.grabadores.model;

/**
 * Clase POJO que representa un reporte generado por el sistema.
 * Soporta tres tipos: inventario_general, por_formato, compatibilidad.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Reporte {

    private int id;
    private String fecha;
    private String tipo;
    private int usuarioId;
    private String contenido;

    // Objeto relacionado para visualización
    private Usuario usuario;

    /** Constructor vacío requerido por el patrón DAO */
    public Reporte() {}

    /**
     * Constructor con los campos principales.
     *
     * @param tipo      tipo de reporte ("inventario_general", "por_formato", "compatibilidad")
     * @param usuarioId ID del usuario que generó el reporte
     * @param contenido descripción o contenido del reporte
     */
    public Reporte(String tipo, int usuarioId, String contenido) {
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.contenido = contenido;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public int getUsuarioId() { return usuarioId; }
    public String getContenido() { return contenido; }
    public Usuario getUsuario() { return usuario; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    /**
     * Genera el reporte según su tipo.
     * La lógica real se implementa en ReporteController.
     */
    public void generarReporte() {
        System.out.println("[Reporte] Generando reporte tipo: " + tipo);
    }

    /**
     * Exporta el reporte a formato PDF.
     * La lógica real se implementa en PDFExporter.
     */
    public void exportarPDF() {
        System.out.println("[Reporte] Exportando a PDF...");
    }

    @Override
    public String toString() {
        return tipo + " — " + fecha;
    }
}
