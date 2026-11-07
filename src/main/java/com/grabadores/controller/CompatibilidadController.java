package com.grabadores.controller;

import com.grabadores.dao.CompatibilidadDAO;
import com.grabadores.model.Compatibilidad;
import com.grabadores.util.ValidatorUtil;
import java.util.List;

/**
 * Controlador para la lógica de negocio de compatibilidad (RF08).
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class CompatibilidadController {
    private final CompatibilidadDAO dao;

    public CompatibilidadController() { this.dao = new CompatibilidadDAO(); }

    public String registrar(Compatibilidad c) {
        if (!ValidatorUtil.esTextoValido(c.getTipoConexion())) return "El tipo de conexión es obligatorio.";
        if (!ValidatorUtil.esTextoValido(c.getNivel())) return "El nivel de compatibilidad es obligatorio.";
        if (dao.insertar(c)) return "OK";
        return "Error al registrar. Esa combinación grabador-cámara ya podría existir.";
    }

    public String actualizar(Compatibilidad c) {
        if (!ValidatorUtil.esTextoValido(c.getTipoConexion())) return "El tipo de conexión es obligatorio.";
        if (dao.actualizar(c)) return "OK";
        return "Error al actualizar la compatibilidad.";
    }

    public List<Compatibilidad> listarTodas() { return dao.listarTodas(); }
    public List<Compatibilidad> listarFiltradas(int grabadorId, int camaraId) {
        return dao.listarFiltradas(grabadorId, camaraId);
    }
    public List<Compatibilidad> listarPorGrabador(int grabadorId) { return dao.listarPorGrabador(grabadorId); }
    public List<Compatibilidad> listarPorCamara(int camaraId) { return dao.listarPorCamara(camaraId); }
    public Compatibilidad buscarPorId(int id) { return dao.buscarPorId(id); }
    public boolean eliminar(int id) { return dao.eliminar(id); }
}
