package com.grabadores.controller;

import com.grabadores.dao.AccesorioDAO;
import com.grabadores.model.Accesorio;
import com.grabadores.util.ValidatorUtil;
import java.util.List;

/**
 * Controlador para la lógica de negocio de accesorios (RF13).
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class AccesorioController {
    private final AccesorioDAO dao;

    public AccesorioController() { this.dao = new AccesorioDAO(); }

    public String registrar(Accesorio a) {
        if (!ValidatorUtil.esTextoValido(a.getNombre())) return "El nombre es obligatorio.";
        if (a.getCantidad() < 0) return "La cantidad no puede ser negativa.";
        if (dao.insertar(a)) return "OK";
        return "Error al registrar el accesorio.";
    }

    public String actualizar(Accesorio a) {
        if (!ValidatorUtil.esTextoValido(a.getNombre())) return "El nombre es obligatorio.";
        if (dao.actualizar(a)) return "OK";
        return "Error al actualizar el accesorio.";
    }

    public List<Accesorio> listarTodos() { return dao.listarTodos(); }
    public List<Accesorio> listarPorGrabador(int grabadorId) { return dao.listarPorGrabador(grabadorId); }
    public Accesorio buscarPorId(int id) { return dao.buscarPorId(id); }
    public boolean eliminar(int id) { return dao.eliminar(id); }
}
