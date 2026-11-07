package com.grabadores.model;

/**
 * Clase POJO que representa un accesorio asociado a un grabador externo.
 * Incluye baterías, SSDs, cables, fundas y otros complementos.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Accesorio {

    private int id;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private String estado;
    private int grabadorId;

    // Objeto relacionado para visualización
    private GrabadorExterno grabador;

    /** Constructor vacío requerido por el patrón DAO */
    public Accesorio() {}

    /**
     * Constructor con los campos principales.
     *
     * @param nombre      nombre del accesorio (ej: Batería NP-F970)
     * @param descripcion descripción detallada
     * @param cantidad    cantidad disponible
     * @param estado      estado actual del accesorio
     * @param grabadorId  ID del grabador asociado
     */
    public Accesorio(String nombre, String descripcion, int cantidad,
                     String estado, int grabadorId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.estado = estado;
        this.grabadorId = grabadorId;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public String getEstado() { return estado; }
    public int getGrabadorId() { return grabadorId; }
    public GrabadorExterno getGrabador() { return grabador; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setGrabadorId(int grabadorId) { this.grabadorId = grabadorId; }
    public void setGrabador(GrabadorExterno grabador) { this.grabador = grabador; }

    @Override
    public String toString() {
        return nombre + " (x" + cantidad + ")";
    }
}
