package com.grabadores.dao;

import com.grabadores.model.ImagenGrabador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImagenGrabadorDAO {

    private final Connection connection;

    public ImagenGrabadorDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    public List<ImagenGrabador> listarPorGrabador(int grabadorId) {
        List<ImagenGrabador> imagenes = new ArrayList<>();
        String sql = "SELECT * FROM Imagen WHERE grabador_id = ? ORDER BY tipo_vista";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, grabadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) imagenes.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ImagenGrabadorDAO] Error al consultar imágenes: " + e.getMessage());
        }
        return imagenes;
    }

    public boolean guardar(int grabadorId, String tipoVista, String ruta) {
        String sql = "INSERT INTO Imagen (grabador_id, tipo_vista, ruta) VALUES (?, ?, ?) "
                   + "ON CONFLICT(grabador_id, tipo_vista) DO UPDATE SET ruta = excluded.ruta";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, grabadorId);
            stmt.setString(2, tipoVista);
            stmt.setString(3, ruta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ImagenGrabadorDAO] Error al guardar imagen: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int grabadorId, String tipoVista) {
        String sql = "DELETE FROM Imagen WHERE grabador_id = ? AND tipo_vista = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, grabadorId);
            stmt.setString(2, tipoVista);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ImagenGrabadorDAO] Error al eliminar imagen: " + e.getMessage());
            return false;
        }
    }

    private ImagenGrabador mapear(ResultSet rs) throws SQLException {
        ImagenGrabador imagen = new ImagenGrabador();
        imagen.setId(rs.getInt("id"));
        imagen.setGrabadorId(rs.getInt("grabador_id"));
        imagen.setTipoVista(rs.getString("tipo_vista"));
        imagen.setRuta(rs.getString("ruta"));
        return imagen;
    }
}
