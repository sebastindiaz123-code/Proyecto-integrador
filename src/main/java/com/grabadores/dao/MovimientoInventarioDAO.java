package com.grabadores.dao;

import com.grabadores.model.MovimientoInventario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    private final Connection connection;

    public MovimientoInventarioDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public String registrar(int grabadorId, int usuarioId, String tipo, int cantidad,
                            String estadoNuevo, String observacion) {
        if (cantidad < 0 || (("ENTRADA".equals(tipo) || "SALIDA".equals(tipo)) && cantidad == 0)) {
            return "La cantidad debe ser mayor que cero.";
        }
        if (!List.of("ENTRADA", "SALIDA", "AJUSTE", "CAMBIO_ESTADO").contains(tipo)) {
            return "Tipo de movimiento inválido.";
        }

        return enTransaccion(() -> {
            int stockAnterior;
            String estadoAnterior;
            int inventarioId;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, stock, estado FROM Inventario WHERE grabador_id = ? ORDER BY id LIMIT 1")) {
                stmt.setInt(1, grabadorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return "El grabador no tiene un registro de inventario.";
                    inventarioId = rs.getInt("id");
                    stockAnterior = rs.getInt("stock");
                    estadoAnterior = rs.getString("estado");
                }
            }

            int stockNuevo = switch (tipo) {
                case "ENTRADA" -> stockAnterior + cantidad;
                case "SALIDA" -> stockAnterior - cantidad;
                default -> stockAnterior;
            };
            if (stockNuevo < 0) return "No hay unidades suficientes para registrar esta salida.";
            if ("AJUSTE".equals(tipo)) stockNuevo = cantidad;
            String estadoFinal = estadoNuevo != null && !estadoNuevo.isBlank() ? estadoNuevo : estadoAnterior;

            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Inventario SET stock = ?, estado = ? WHERE id = ?")) {
                stmt.setInt(1, stockNuevo);
                stmt.setString(2, estadoFinal);
                stmt.setInt(3, inventarioId);
                stmt.executeUpdate();
            }

            insertarMovimiento(grabadorId, usuarioId, tipo, cantidad, stockAnterior, stockNuevo,
                    estadoAnterior, estadoFinal, observacion);
            return "OK";
        });
    }

    public String registrarInicial(int grabadorId, int usuarioId, int stock, String estado) {
        return enTransaccion(() -> registrarInicialEnTransaccion(grabadorId, usuarioId, stock, estado));
    }

    String registrarInicialEnTransaccion(int grabadorId, int usuarioId, int stock, String estado)
            throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT 1 FROM Inventario WHERE grabador_id = ? LIMIT 1")) {
            stmt.setInt(1, grabadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return "Este grabador ya tiene un registro de inventario.";
            }
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Inventario (stock, estado, grabador_id) VALUES (?, ?, ?)")) {
            stmt.setInt(1, stock);
            stmt.setString(2, estado);
            stmt.setInt(3, grabadorId);
            stmt.executeUpdate();
        }
        insertarMovimiento(grabadorId, usuarioId, "AJUSTE", stock, 0, stock,
                null, estado, "Registro inicial de inventario");
        return "OK";
    }

    public String actualizarInventario(int inventarioId, int usuarioId, int stockNuevo, String estadoNuevo) {
        return enTransaccion(() -> {
            int grabadorId;
            int stockAnterior;
            String estadoAnterior;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT grabador_id, stock, estado FROM Inventario WHERE id = ?")) {
                stmt.setInt(1, inventarioId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return "No se encontró el inventario seleccionado.";
                    grabadorId = rs.getInt("grabador_id");
                    stockAnterior = rs.getInt("stock");
                    estadoAnterior = rs.getString("estado");
                }
            }

            if (stockAnterior == stockNuevo && estadoAnterior.equals(estadoNuevo)) return "OK";
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Inventario SET stock = ?, estado = ? WHERE id = ?")) {
                stmt.setInt(1, stockNuevo);
                stmt.setString(2, estadoNuevo);
                stmt.setInt(3, inventarioId);
                stmt.executeUpdate();
            }
            String tipo = stockAnterior == stockNuevo ? "CAMBIO_ESTADO" : "AJUSTE";
            insertarMovimiento(grabadorId, usuarioId, tipo,
                    tipo.equals("AJUSTE") ? stockNuevo : 0, stockAnterior, stockNuevo,
                    estadoAnterior, estadoNuevo, "Actualización del inventario");
            return "OK";
        });
    }

    private void insertarMovimiento(int grabadorId, int usuarioId, String tipo, int cantidad,
                                    int stockAnterior, int stockNuevo, String estadoAnterior,
                                    String estadoNuevo, String observacion) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO MovimientoInventario "
                + "(grabador_id, usuario_id, tipo, cantidad, stock_anterior, stock_nuevo, "
                + "estado_anterior, estado_nuevo, observacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, grabadorId);
            stmt.setInt(2, usuarioId);
            stmt.setString(3, tipo);
            stmt.setInt(4, cantidad);
            stmt.setInt(5, stockAnterior);
            stmt.setInt(6, stockNuevo);
            stmt.setString(7, estadoAnterior);
            stmt.setString(8, estadoNuevo);
            stmt.setString(9, observacion == null || observacion.isBlank() ? null : observacion.trim());
            stmt.executeUpdate();
        }
    }

    private String enTransaccion(SqlOperation operation) {
        boolean autoCommit = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String result = operation.execute();
            if ("OK".equals(result)) connection.commit();
            else connection.rollback();
            return result;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            System.err.println("[MovimientoInventarioDAO] Error al registrar movimiento: " + e.getMessage());
            return "Error al registrar el movimiento de inventario.";
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                System.err.println("[MovimientoInventarioDAO] Error al restaurar la conexión: " + e.getMessage());
            }
        }
    }

    public List<MovimientoInventario> listarTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.*, g.marca || ' ' || g.modelo AS grabador, u.nombre AS usuario "
                   + "FROM MovimientoInventario m "
                   + "JOIN GrabadorExterno g ON g.id = m.grabador_id "
                   + "JOIN Usuario u ON u.id = m.usuario_id ORDER BY m.fecha DESC, m.id DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) movimientos.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("[MovimientoInventarioDAO] Error al consultar historial: " + e.getMessage());
        }
        return movimientos;
    }

    public List<MovimientoInventario> listarEntreFechas(String desde, String hasta) {
        return listarEntreFechas(desde, hasta, "Todos", 0);
    }

    public List<MovimientoInventario> listarEntreFechas(String desde, String hasta,
                                                        String tipo, int usuarioId) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.*, g.marca || ' ' || g.modelo AS grabador, u.nombre AS usuario "
                   + "FROM MovimientoInventario m "
                   + "JOIN GrabadorExterno g ON g.id = m.grabador_id "
                   + "JOIN Usuario u ON u.id = m.usuario_id "
                   + "WHERE date(m.fecha) BETWEEN date(?) AND date(?) "
                   + "AND (? = 'Todos' OR m.tipo = ?) AND (? = 0 OR m.usuario_id = ?) "
                   + "ORDER BY m.fecha DESC, m.id DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, desde);
            stmt.setString(2, hasta);
            stmt.setString(3, tipo);
            stmt.setString(4, tipo);
            stmt.setInt(5, usuarioId);
            stmt.setInt(6, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) movimientos.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MovimientoInventarioDAO] Error al filtrar historial: " + e.getMessage());
        }
        return movimientos;
    }

    private MovimientoInventario mapear(ResultSet rs) throws SQLException {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(rs.getInt("id"));
        movimiento.setGrabadorId(rs.getInt("grabador_id"));
        movimiento.setGrabador(rs.getString("grabador"));
        movimiento.setUsuarioId(rs.getInt("usuario_id"));
        movimiento.setUsuario(rs.getString("usuario"));
        movimiento.setTipo(rs.getString("tipo"));
        movimiento.setCantidad(rs.getInt("cantidad"));
        movimiento.setStockAnterior(rs.getInt("stock_anterior"));
        movimiento.setStockNuevo(rs.getInt("stock_nuevo"));
        movimiento.setEstadoAnterior(rs.getString("estado_anterior"));
        movimiento.setEstadoNuevo(rs.getString("estado_nuevo"));
        movimiento.setFecha(rs.getString("fecha"));
        movimiento.setObservacion(rs.getString("observacion"));
        return movimiento;
    }

    @FunctionalInterface
    private interface SqlOperation {
        String execute() throws SQLException;
    }
}
