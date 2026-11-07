package com.grabadores.dao;

import com.grabadores.model.Compatibilidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Compatibilidad.
 * Gestiona las relaciones de compatibilidad entre grabadores y cámaras (RF08).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class CompatibilidadDAO {

    private final Connection connection;

    public CompatibilidadDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public boolean insertar(Compatibilidad comp) {
        String sql = "INSERT INTO Compatibilidad (tipoConexion, nivel, grabador_id, camara_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, comp.getTipoConexion());
            pstmt.setString(2, comp.getNivel());
            pstmt.setInt(3, comp.getGrabadorId());
            pstmt.setInt(4, comp.getCamaraId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) comp.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public Compatibilidad buscarPorId(int id) {
        String sql = "SELECT * FROM Compatibilidad WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Compatibilidad> listarTodas() {
        return listarFiltradas(0, 0);
    }

    public List<Compatibilidad> listarFiltradas(int grabadorId, int camaraId) {
        List<Compatibilidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM Compatibilidad "
                   + "WHERE (? = 0 OR grabador_id = ?) AND (? = 0 OR camara_id = ?) "
                   + "ORDER BY grabador_id, camara_id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, grabadorId);
            stmt.setInt(2, grabadorId);
            stmt.setInt(3, camaraId);
            stmt.setInt(4, camaraId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al filtrar compatibilidades: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista las compatibilidades de un grabador específico (para Ficha Técnica).
     */
    public List<Compatibilidad> listarPorGrabador(int grabadorId) {
        List<Compatibilidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM Compatibilidad WHERE grabador_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, grabadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al listar por grabador: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los grabadores compatibles con una cámara específica (para reportes - RF11c).
     */
    public List<Compatibilidad> listarPorCamara(int camaraId) {
        List<Compatibilidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM Compatibilidad WHERE camara_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camaraId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al listar por cámara: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Compatibilidad comp) {
        String sql = "UPDATE Compatibilidad SET tipoConexion = ?, nivel = ?, grabador_id = ?, camara_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, comp.getTipoConexion());
            pstmt.setString(2, comp.getNivel());
            pstmt.setInt(3, comp.getGrabadorId());
            pstmt.setInt(4, comp.getCamaraId());
            pstmt.setInt(5, comp.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Compatibilidad WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CompatibilidadDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    private Compatibilidad mapear(ResultSet rs) throws SQLException {
        Compatibilidad c = new Compatibilidad();
        c.setId(rs.getInt("id"));
        c.setTipoConexion(rs.getString("tipoConexion"));
        c.setNivel(rs.getString("nivel"));
        c.setGrabadorId(rs.getInt("grabador_id"));
        c.setCamaraId(rs.getInt("camara_id"));
        return c;
    }
}
