package com.grabadores.model;

/**
 * Clase POJO que representa el inventario de un grabador externo.
 * Controla el stock disponible y el estado operativo del equipo.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Inventario {

    private int id;
    private int stock;
    private String estado;
    private int grabadorId;

    // Objeto relacionado para visualización
    private GrabadorExterno grabador;

    /** Constructor vacío requerido por el patrón DAO */
    public Inventario() {}

    /**
     * Constructor con los campos principales.
     *
     * @param stock      cantidad disponible en inventario
     * @param estado     estado operativo ("disponible", "en_uso", "mantenimiento", "danado", "baja")
     * @param grabadorId ID del grabador externo asociado
     */
    public Inventario(int stock, String estado, int grabadorId) {
        this.stock = stock;
        this.estado = estado;
        this.grabadorId = grabadorId;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public int getStock() { return stock; }
    public String getEstado() { return estado; }
    public int getGrabadorId() { return grabadorId; }
    public GrabadorExterno getGrabador() { return grabador; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setStock(int stock) { this.stock = stock; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setGrabadorId(int grabadorId) { this.grabadorId = grabadorId; }
    public void setGrabador(GrabadorExterno grabador) { this.grabador = grabador; }

    /**
     * Actualiza el stock sumando o restando unidades.
     *
     * @param cantidad cantidad a sumar (positiva) o restar (negativa)
     * @return true si el stock resultante es válido (>= 0)
     */
    public boolean actualizarStock(int cantidad) {
        int nuevoStock = this.stock + cantidad;
        if (nuevoStock < 0) {
            return false;
        }
        this.stock = nuevoStock;
        return true;
    }

    @Override
    public String toString() {
        return "Stock: " + stock + " — Estado: " + estado;
    }
}
