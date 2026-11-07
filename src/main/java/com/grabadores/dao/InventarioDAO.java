package com.grabadores.dao;

import com.grabadores.model.Inventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Inventario.
 * Gestiona el stock y el estado operativo de los grabadores (RF09, RF10).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class InventarioDAO {

    private final Connection connection;

    public InventarioDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public boolean insertar(Inventario inv) {
        String sql = "INSERT INTO Inventario (stock, estado, grabador_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inv.getStock());
            pstmt.setString(2, inv.getEstado());
            pstmt.setInt(3, inv.getGrabadorId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) inv.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public Inventario buscarPorId(int id) {
        String sql = "SELECT * FROM Inventario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca el inventario asociado a un grabador específico.
     */
    public Inventario buscarPorGrabadorId(int grabadorId) {
        String sql = "SELECT * FROM Inventario WHERE grabador_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, grabadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al buscar por grabador: " + e.getMessage());
        }
        return null;
    }

    public List<Inventario> listarTodos() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT i.*, g.marca, g.modelo FROM Inventario i "
                   + "JOIN GrabadorExterno g ON i.grabador_id = g.id ORDER BY g.marca, g.modelo";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista grabadores con stock por debajo del mínimo (alerta de stock bajo - RF09).
     *
     * @param minimo umbral mínimo de stock
     */
    public List<Inventario> listarStockBajo(int minimo) {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Inventario WHERE stock <= ? AND stock >= 0 ORDER BY stock ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, minimo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al listar stock bajo: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Inventario inv) {
        String sql = "UPDATE Inventario SET stock = ?, estado = ?, grabador_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inv.getStock());
            pstmt.setString(2, inv.getEstado());
            pstmt.setInt(3, inv.getGrabadorId());
            pstmt.setInt(4, inv.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Inventario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventarioDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    private Inventario mapear(ResultSet rs) throws SQLException {
        Inventario inv = new Inventario();
        inv.setId(rs.getInt("id"));
        inv.setStock(rs.getInt("stock"));
        inv.setEstado(rs.getString("estado"));
        inv.setGrabadorId(rs.getInt("grabador_id"));
        return inv;
    }
}
