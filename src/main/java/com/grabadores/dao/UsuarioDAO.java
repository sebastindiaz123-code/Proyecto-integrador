package com.grabadores.dao;

import com.grabadores.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Usuario.
 * Implementa operaciones CRUD usando PreparedStatement
 * para prevenir inyección SQL (RNF03).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    // ==================== CREATE ====================

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario objeto Usuario con los datos a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nombre, correo, contrasena, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getRol());
            pstmt.setInt(5, usuario.isActivo() ? 1 : 0);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        usuario.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    // ==================== READ ====================

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return objeto Usuario o null si no se encontró
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al buscar por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo correo del usuario
     * @return objeto Usuario o null si no se encontró
     */
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM Usuario WHERE correo = ? AND activo = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al buscar por correo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los usuarios activos.
     *
     * @return lista de usuarios activos
     */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE activo = 1 ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public List<Usuario> listarTodosIncluyendoInactivos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapearUsuario(rs));
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al listar historial de usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ==================== UPDATE ====================

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuario objeto Usuario con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE Usuario SET nombre = ?, correo = ?, contrasena = ?, rol = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getRol());
            pstmt.setInt(5, usuario.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    // ==================== DELETE (lógica) ====================

    /**
     * Desactiva un usuario (eliminación lógica).
     *
     * @param id ID del usuario a desactivar
     * @return true si fue desactivado exitosamente
     */
    public boolean desactivar(int id) {
        String sql = "UPDATE Usuario SET activo = 0 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al desactivar: " + e.getMessage());
        }
        return false;
    }

    // ==================== UTILIDAD ====================

    /**
     * Mapea un ResultSet a un objeto Usuario.
     *
     * @param rs ResultSet posicionado en un registro válido
     * @return objeto Usuario con los datos del registro
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getInt("activo") == 1);
        return u;
    }
}
