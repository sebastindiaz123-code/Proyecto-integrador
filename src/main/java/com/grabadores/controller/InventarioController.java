package com.grabadores.controller;

import com.grabadores.dao.InventarioDAO;
import com.grabadores.dao.MovimientoInventarioDAO;
import com.grabadores.model.Inventario;
import com.grabadores.model.MovimientoInventario;
import java.util.List;

/**
 * Controlador para la lógica de negocio de inventario (RF09, RF10).
 * @author Equipo PI3 — UNIAJC Grupo 328
 */
public class InventarioController {
    private final InventarioDAO dao;
    private final MovimientoInventarioDAO movimientoDAO;

    public InventarioController() {
        this.dao = new InventarioDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
    }

    public String registrar(Inventario inv, int usuarioId) {
        if (inv.getStock() < 0) return "El stock no puede ser negativo.";
        if (!estadoValido(inv.getEstado())) return "Seleccione un estado válido.";
        if (usuarioId <= 0) return "No se pudo identificar al usuario que registra el inventario.";
        return movimientoDAO.registrarInicial(inv.getGrabadorId(), usuarioId, inv.getStock(), inv.getEstado());
    }

    public String actualizar(Inventario inv, int usuarioId) {
        if (inv.getStock() < 0) return "El stock no puede ser negativo.";
        if (!estadoValido(inv.getEstado())) return "Seleccione un estado válido.";
        if (usuarioId <= 0) return "No se pudo identificar al usuario que actualiza el inventario.";
        return movimientoDAO.actualizarInventario(inv.getId(), usuarioId, inv.getStock(), inv.getEstado());
    }

    public String registrarMovimiento(int grabadorId, int usuarioId, String tipo, int cantidad,
                                      String estadoNuevo, String observacion) {
        if (usuarioId <= 0) return "No se pudo identificar al usuario que registra el movimiento.";
        if (estadoNuevo != null && !estadoValido(estadoNuevo)) return "Seleccione un estado válido.";
        return movimientoDAO.registrar(grabadorId, usuarioId, tipo, cantidad, estadoNuevo, observacion);
    }

    private boolean estadoValido(String estado) {
        return estado != null
                && List.of("disponible", "en_uso", "mantenimiento", "danado", "baja").contains(estado);
    }

    public List<Inventario> listarTodos() { return dao.listarTodos(); }
    public Inventario buscarPorId(int id) { return dao.buscarPorId(id); }
    public Inventario buscarPorGrabadorId(int grabadorId) { return dao.buscarPorGrabadorId(grabadorId); }
    public List<Inventario> listarStockBajo(int minimo) { return dao.listarStockBajo(minimo); }
    public boolean eliminar(int id) { return dao.eliminar(id); }
    public List<MovimientoInventario> listarMovimientos() { return movimientoDAO.listarTodos(); }
    public List<MovimientoInventario> listarMovimientos(String desde, String hasta) {
        return movimientoDAO.listarEntreFechas(desde, hasta);
    }
}
