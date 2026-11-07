package com.grabadores.model;

public class ImagenGrabador {

    private int id;
    private int grabadorId;
    private String ruta;
    private String tipoVista;

    public int getId() { return id; }
    public int getGrabadorId() { return grabadorId; }
    public String getRuta() { return ruta; }
    public String getTipoVista() { return tipoVista; }

    public void setId(int id) { this.id = id; }
    public void setGrabadorId(int grabadorId) { this.grabadorId = grabadorId; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public void setTipoVista(String tipoVista) { this.tipoVista = tipoVista; }
}
