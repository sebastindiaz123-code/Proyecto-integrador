package com.grabadores.dao;

import com.grabadores.model.Reporte;
import com.grabadores.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Reporte.
 * Gestiona el registro y consulta de reportes generados (RF11, RF12).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class ReporteDAO {

    private final Connection connection;

    public ReporteDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public boolean insertar(Reporte r) {
        String sql = "INSERT INTO Reporte (tipo, usuario_id, contenido) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, r.getTipo());
            pstmt.setInt(2, r.getUsuarioId());
            pstmt.setString(3, r.getContenido());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) r.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ReporteDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public Reporte buscarPorId(int id) {
        String sql = "SELECT * FROM Reporte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ReporteDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Reporte> listarTodos() {
        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT r.*, u.nombre AS usuario_nombre FROM Reporte r "
                   + "LEFT JOIN Usuario u ON u.id = r.usuario_id "
                   + "ORDER BY r.fecha DESC, r.id DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reporte reporte = mapear(rs);
                Usuario usuario = new Usuario();
                usuario.setId(reporte.getUsuarioId());
                usuario.setNombre(rs.getString("usuario_nombre"));
                reporte.setUsuario(usuario);
                lista.add(reporte);
            }
        } catch (SQLException e) {
            System.err.println("[ReporteDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista reportes por tipo.
     */
    public List<Reporte> listarPorTipo(String tipo) {
        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reporte WHERE tipo = ? ORDER BY fecha DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ReporteDAO] Error al listar por tipo: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Reporte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReporteDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    private Reporte mapear(ResultSet rs) throws SQLException {
        Reporte r = new Reporte();
        r.setId(rs.getInt("id"));
        r.setFecha(rs.getString("fecha"));
        r.setTipo(rs.getString("tipo"));
        r.setUsuarioId(rs.getInt("usuario_id"));
        r.setContenido(rs.getString("contenido"));
        return r;
    }
}
