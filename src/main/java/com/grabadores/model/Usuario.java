package com.grabadores.model;

/**
 * Clase POJO que representa un usuario del sistema.
 * Soporta dos roles: administrador y consultor.
 * La contraseña se almacena como hash SHA-256.
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private boolean activo;

    /** Constructor vacío requerido por el patrón DAO */
    public Usuario() {
        this.activo = true;
    }

    /**
     * Constructor completo para crear un nuevo usuario.
     *
     * @param nombre     nombre completo del usuario
     * @param correo     correo electrónico (único)
     * @param contrasena contraseña hasheada con SHA-256
     * @param rol        rol del usuario ("administrador" o "consultor")
     */
    public Usuario(String nombre, String correo, String contrasena, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = true;
    }

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }

    // ==================== SETTERS ====================

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(String rol) { this.rol = rol; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /**
     * Verifica si el usuario tiene rol de administrador.
     *
     * @return true si es administrador
     */
    public boolean esAdministrador() {
        return "administrador".equalsIgnoreCase(rol);
    }

    @Override
    public String toString() {
        return nombre + " (" + rol + ")";
    }
}
