package com.grabadores.dao;

import com.grabadores.model.Camara;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Camara.
 * Implementa CRUD completo para gestionar cámaras de video.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class CamaraDAO {

    private final Connection connection;

    public CamaraDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public boolean insertar(Camara c) {
        String sql = "INSERT INTO Camara (marca, modelo, tipoConexion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getMarca());
            pstmt.setString(2, c.getModelo());
            pstmt.setString(3, c.getTipoConexion());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) c.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public Camara buscarPorId(int id) {
        String sql = "SELECT * FROM Camara WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    public List<Camara> listarTodas() {
        List<Camara> lista = new ArrayList<>();
        String sql = "SELECT * FROM Camara WHERE activo = 1 ORDER BY marca, modelo";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public List<Camara> buscarPorMarca(String marca) {
        List<Camara> lista = new ArrayList<>();
        String sql = "SELECT * FROM Camara WHERE activo = 1 AND marca LIKE ? ORDER BY modelo";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + marca + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al buscar por marca: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Camara c) {
        String sql = "UPDATE Camara SET marca = ?, modelo = ?, tipoConexion = ? WHERE id = ? AND activo = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, c.getMarca());
            pstmt.setString(2, c.getModelo());
            pstmt.setString(3, c.getTipoConexion());
            pstmt.setInt(4, c.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE Camara SET activo = 0 WHERE id = ? AND activo = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CamaraDAO] Error al eliminar: " + e.getMessage());
        }
        return false;
    }

    private Camara mapear(ResultSet rs) throws SQLException {
        Camara c = new Camara();
        c.setId(rs.getInt("id"));
        c.setMarca(rs.getString("marca"));
        c.setModelo(rs.getString("modelo"));
        c.setTipoConexion(rs.getString("tipoConexion"));
        c.setActivo(rs.getInt("activo") == 1);
        return c;
    }
}
