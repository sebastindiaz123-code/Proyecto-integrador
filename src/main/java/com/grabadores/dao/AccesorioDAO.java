package com.grabadores.dao;

import com.grabadores.model.Accesorio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Accesorio.
 * Gestiona accesorios asociados a grabadores externos (RF13).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class AccesorioDAO {

    private final Connection connection;

    public AccesorioDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public boolean insertar(Accesorio a) {
        String sql = "INSERT INTO Accesorio (nombre, descripcion, cantidad, estado, grabador_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, a.getNombre());
            pstmt.setString(2, a.getDescripcion());
            pstmt.setInt(3, a.getCantidad());
            pstmt.setString(4, a.getEstado());
            pstmt.setInt(5, a.getGrabadorId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) a.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public Accesorio buscarPorId(int id) {
        String sql = "SELECT * FROM Accesorio WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Accesorio> listarTodos() {
        List<Accesorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Accesorio ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los accesorios de un grabador específico (para Ficha Técnica - RF14).
     */
    public List<Accesorio> listarPorGrabador(int grabadorId) {
        List<Accesorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Accesorio WHERE grabador_id = ? ORDER BY nombre";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, grabadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al listar por grabador: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Accesorio a) {
        String sql = "UPDATE Accesorio SET nombre = ?, descripcion = ?, cantidad = ?, estado = ?, grabador_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, a.getNombre());
            pstmt.setString(2, a.getDescripcion());
            pstmt.setInt(3, a.getCantidad());
            pstmt.setString(4, a.getEstado());
            pstmt.setInt(5, a.getGrabadorId());
            pstmt.setInt(6, a.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Accesorio WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AccesorioDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    private Accesorio mapear(ResultSet rs) throws SQLException {
        Accesorio a = new Accesorio();
        a.setId(rs.getInt("id"));
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setCantidad(rs.getInt("cantidad"));
        a.setEstado(rs.getString("estado"));
        a.setGrabadorId(rs.getInt("grabador_id"));
        return a;
    }
}
