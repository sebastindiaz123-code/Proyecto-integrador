package com.grabadores.model;

public class MovimientoInventario {

    private int id;
    private int grabadorId;
    private String grabador;
    private int usuarioId;
    private String usuario;
    private String tipo;
    private int cantidad;
    private int stockAnterior;
    private int stockNuevo;
    private String estadoAnterior;
    private String estadoNuevo;
    private String fecha;
    private String observacion;

    public int getId() { return id; }
    public int getGrabadorId() { return grabadorId; }
    public String getGrabador() { return grabador; }
    public int getUsuarioId() { return usuarioId; }
    public String getUsuario() { return usuario; }
    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public int getStockAnterior() { return stockAnterior; }
    public int getStockNuevo() { return stockNuevo; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public String getFecha() { return fecha; }
    public String getObservacion() { return observacion; }

    public void setId(int id) { this.id = id; }
    public void setGrabadorId(int grabadorId) { this.grabadorId = grabadorId; }
    public void setGrabador(String grabador) { this.grabador = grabador; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setStockAnterior(int stockAnterior) { this.stockAnterior = stockAnterior; }
    public void setStockNuevo(int stockNuevo) { this.stockNuevo = stockNuevo; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
