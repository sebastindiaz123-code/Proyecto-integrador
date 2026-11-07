package com.grabadores.controller;

import com.grabadores.dao.GrabadorExternoDAO;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.util.ValidatorUtil;

import java.util.List;

/**
 * Controlador para la lógica de negocio de grabadores externos.
 * Valida datos antes de persistir y gestiona búsquedas (RF03-RF06).
 *
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class GrabadorController {

    private final GrabadorExternoDAO dao;

    public GrabadorController() {
        this.dao = new GrabadorExternoDAO();
    }

    /**
     * Registra un nuevo grabador externo con validaciones.
     *
     * @return "OK" si fue exitoso, o descripción del error
     */
    public String registrar(GrabadorExterno g) {
        if (!ValidatorUtil.esTextoValido(g.getMarca())) return "La marca es obligatoria.";
        if (!ValidatorUtil.esTextoValido(g.getModelo())) return "El modelo es obligatorio.";
        if (!ValidatorUtil.esTextoValido(g.getNumeroSerie())) return "El número de serie es obligatorio.";
        if (!ValidatorUtil.esAnioValido(g.getAnio())) return "El año debe estar entre 1990 y 2030.";
        if (g.getPrecio() < 0) return "El precio no puede ser negativo.";

        if (dao.insertar(g)) return "OK";
        return "Error al registrar. El número de serie podría estar duplicado.";
    }

    public String registrar(GrabadorExterno g, int stockInicial, String estadoInicial, int usuarioId) {
        if (!ValidatorUtil.esTextoValido(g.getMarca())) return "La marca es obligatoria.";
        if (!ValidatorUtil.esTextoValido(g.getModelo())) return "El modelo es obligatorio.";
        if (!ValidatorUtil.esTextoValido(g.getNumeroSerie())) return "El número de serie es obligatorio.";
        if (!ValidatorUtil.esAnioValido(g.getAnio())) return "El año debe estar entre 1990 y 2030.";
        if (g.getPrecio() < 0) return "El precio no puede ser negativo.";
        if (stockInicial < 0) return "El stock inicial no puede ser negativo.";
        if (!List.of("disponible", "en_uso", "mantenimiento", "danado", "baja").contains(estadoInicial)) {
            return "Seleccione un estado inicial válido.";
        }
        if (usuarioId <= 0) return "No se pudo identificar al usuario que registra el inventario.";

        return dao.insertarConInventarioInicial(g, stockInicial, estadoInicial, usuarioId);
    }

    /**
     * Actualiza un grabador existente con validaciones.
     */
    public String actualizar(GrabadorExterno g) {
        if (!ValidatorUtil.esTextoValido(g.getMarca())) return "La marca es obligatoria.";
        if (!ValidatorUtil.esTextoValido(g.getModelo())) return "El modelo es obligatorio.";

        if (dao.actualizar(g)) return "OK";
        return "Error al actualizar el grabador.";
    }

    public List<GrabadorExterno> listarTodos() {
        return dao.listarTodos();
    }

    public List<GrabadorExterno> listarTodosIncluyendoInactivos() {
        return dao.listarTodosIncluyendoInactivos();
    }

    public List<GrabadorExterno> buscar(String termino) {
        if (!ValidatorUtil.esTextoValido(termino)) return listarTodos();
        return dao.buscarGeneral(termino.trim());
    }

    public List<GrabadorExterno> buscarFiltrado(String termino, String marca, String modelo,
                                                 String formato, String resolucion,
                                                 String disponibilidad) {
        return dao.buscarFiltrado(termino, marca, modelo, formato, resolucion, disponibilidad);
    }

    public GrabadorExterno buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public boolean desactivar(int id) {
        return dao.desactivar(id);
    }
}
