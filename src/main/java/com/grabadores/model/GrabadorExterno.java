package com.grabadores.model;

/**
 * Clase POJO que representa un grabador externo de video profesional.
 * Contiene todas las especificaciones técnicas del dispositivo.
 * Soporta eliminación lógica mediante el campo 'activo'.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class GrabadorExterno {

    private int id;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private int anio;
    private String formatos;
    private String resolucion;
    private String conexiones;
    private String almacenamiento;
    private double precio;
    private String imagenRuta;
    private boolean activo;

    /** Constructor vacío requerido por el patrón DAO */
    public GrabadorExterno() {
        this.activo = true;
    }

    /**
     * Constructor con los campos principales del grabador.
     *
     * @param marca          marca del fabricante (ej: Atomos, Blackmagic)
     * @param modelo         nombre del modelo (ej: Ninja V)
     * @param numeroSerie    número de serie único
     * @param anio           año de fabricación
     * @param formatos       formatos de grabación soportados
     * @param resolucion     resolución máxima de grabación
     * @param conexiones     tipos de conexión disponibles
     * @param almacenamiento tipo de almacenamiento soportado
     * @param precio         precio de referencia en USD
     */
    public GrabadorExterno(String marca, String modelo, String numeroSerie, int anio,
                           String formatos, String resolucion, String conexiones,
                           String almacenamiento, double precio) {
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.anio = anio;
        this.formatos = formatos;
        this.resolucion = resolucion;
        this.conexiones = conexiones;
        this.almacenamiento = almacenamiento;
        this.precio = precio;
        this.activo = true;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public int getAnio() { return anio; }
    public String getFormatos() { return formatos; }
    public String getResolucion() { return resolucion; }
    public String getConexiones() { return conexiones; }
    public String getAlmacenamiento() { return almacenamiento; }
    public double getPrecio() { return precio; }
    public String getImagenRuta() { return imagenRuta; }
    public boolean isActivo() { return activo; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setFormatos(String formatos) { this.formatos = formatos; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    public void setConexiones(String conexiones) { this.conexiones = conexiones; }
    public void setAlmacenamiento(String almacenamiento) { this.almacenamiento = almacenamiento; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + anio + ")";
    }
}
