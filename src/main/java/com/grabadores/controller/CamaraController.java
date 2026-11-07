package com.grabadores.controller;

import com.grabadores.dao.CamaraDAO;
import com.grabadores.model.Camara;
import com.grabadores.util.ValidatorUtil;
import java.util.List;

/**
 * Controlador para la lógica de negocio de cámaras (RF08).
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class CamaraController {
    private final CamaraDAO dao;

    public CamaraController() { this.dao = new CamaraDAO(); }

    public String registrar(Camara c) {
        if (!ValidatorUtil.esTextoValido(c.getMarca())) return "La marca es obligatoria.";
        if (!ValidatorUtil.esTextoValido(c.getModelo())) return "El modelo es obligatorio.";
        if (dao.insertar(c)) return "OK";
        return "Error al registrar la cámara.";
    }

    public String actualizar(Camara c) {
        if (!ValidatorUtil.esTextoValido(c.getMarca())) return "La marca es obligatoria.";
        if (!ValidatorUtil.esTextoValido(c.getModelo())) return "El modelo es obligatorio.";
        if (dao.actualizar(c)) return "OK";
        return "Error al actualizar la cámara.";
    }

    public List<Camara> listarTodas() { return dao.listarTodas(); }
    public Camara buscarPorId(int id) { return dao.buscarPorId(id); }
    public boolean eliminar(int id) { return dao.eliminar(id); }
}
