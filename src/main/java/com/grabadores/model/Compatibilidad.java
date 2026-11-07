package com.grabadores.model;

/**
 * Clase POJO que representa la compatibilidad entre un grabador externo
 * y una cámara de video. Define el tipo de conexión y el nivel de compatibilidad.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Compatibilidad {

    private int id;
    private String tipoConexion;
    private String nivel;
    private int grabadorId;
    private int camaraId;

    // Objetos relacionados para visualización
    private GrabadorExterno grabador;
    private Camara camara;

    /** Constructor vacío requerido por el patrón DAO */
    public Compatibilidad() {}

    /**
     * Constructor con los campos principales.
     *
     * @param tipoConexion tipo de conexión utilizada (ej: HDMI 2.0, SDI)
     * @param nivel        nivel de compatibilidad ("total", "parcial", "no_compatible")
     * @param grabadorId   ID del grabador externo
     * @param camaraId     ID de la cámara
     */
    public Compatibilidad(String tipoConexion, String nivel, int grabadorId, int camaraId) {
        this.tipoConexion = tipoConexion;
        this.nivel = nivel;
        this.grabadorId = grabadorId;
        this.camaraId = camaraId;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getTipoConexion() { return tipoConexion; }
    public String getNivel() { return nivel; }
    public int getGrabadorId() { return grabadorId; }
    public int getCamaraId() { return camaraId; }
    public GrabadorExterno getGrabador() { return grabador; }
    public Camara getCamara() { return camara; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setTipoConexion(String tipoConexion) { this.tipoConexion = tipoConexion; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public void setGrabadorId(int grabadorId) { this.grabadorId = grabadorId; }
    public void setCamaraId(int camaraId) { this.camaraId = camaraId; }
    public void setGrabador(GrabadorExterno grabador) { this.grabador = grabador; }
    public void setCamara(Camara camara) { this.camara = camara; }

    @Override
    public String toString() {
        return tipoConexion + " — " + nivel;
    }
}
