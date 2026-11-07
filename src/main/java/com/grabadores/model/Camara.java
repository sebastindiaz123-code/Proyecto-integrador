package com.grabadores.model;

/**
 * Clase POJO que representa una cámara de video.
 * Se usa para verificar la compatibilidad con grabadores externos.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Camara {

    private int id;
    private String marca;
    private String modelo;
    private String tipoConexion;
    private boolean activo = true;

    /** Constructor vacío requerido por el patrón DAO */
    public Camara() {}

    /**
     * Constructor con todos los campos.
     *
     * @param marca         marca del fabricante (ej: Sony, Canon)
     * @param modelo        nombre del modelo (ej: A7S III)
     * @param tipoConexion  tipo de conexión de salida (ej: HDMI 2.0, SDI)
     */
    public Camara(String marca, String modelo, String tipoConexion) {
        this.marca = marca;
        this.modelo = modelo;
        this.tipoConexion = tipoConexion;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getTipoConexion() { return tipoConexion; }
    public boolean isActivo() { return activo; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setTipoConexion(String tipoConexion) { this.tipoConexion = tipoConexion; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return marca + " " + modelo;
    }
}
