package com.grabadores.dao;

import com.grabadores.model.GrabadorExterno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad GrabadorExterno.
 * Implementa CRUD completo con búsquedas por marca, modelo, formato y resolución.
 * Usa PreparedStatement para prevenir SQL Injection (RNF03).
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class GrabadorExternoDAO {

    private final Connection connection;

    public GrabadorExternoDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    // ==================== CREATE ====================

    /**
     * Inserta un nuevo grabador externo en la base de datos.
     *
     * @param g objeto GrabadorExterno con los datos a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(GrabadorExterno g) {
        String sql = "INSERT INTO GrabadorExterno (marca, modelo, numeroSerie, anio, formatos, "
                   + "resolucion, conexiones, almacenamiento, precio, imagenRuta, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, g.getMarca());
            pstmt.setString(2, g.getModelo());
            pstmt.setString(3, g.getNumeroSerie());
            pstmt.setInt(4, g.getAnio());
            pstmt.setString(5, g.getFormatos());
            pstmt.setString(6, g.getResolucion());
            pstmt.setString(7, g.getConexiones());
            pstmt.setString(8, g.getAlmacenamiento());
            pstmt.setDouble(9, g.getPrecio());
            pstmt.setString(10, g.getImagenRuta());
            pstmt.setInt(11, g.isActivo() ? 1 : 0);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        g.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al insertar: " + e.getMessage());
        }
        return false;
    }

    public String insertarConInventarioInicial(GrabadorExterno grabador, int stockInicial,
                                               String estadoInicial, int usuarioId) {
        boolean autoCommit = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            if (!insertar(grabador)) {
                connection.rollback();
                return "Error al registrar el grabador. Verifique que el número de serie no esté duplicado.";
            }

            String resultado = new MovimientoInventarioDAO().registrarInicialEnTransaccion(
                    grabador.getId(), usuarioId, stockInicial, estadoInicial);
            if (!"OK".equals(resultado)) {
                connection.rollback();
                return resultado;
            }
            connection.commit();
            return "OK";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            System.err.println("[GrabadorExternoDAO] Error al registrar el inventario inicial: " + e.getMessage());
            return "No se pudo registrar el grabador y su inventario inicial.";
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                System.err.println("[GrabadorExternoDAO] Error al restaurar la conexión: " + e.getMessage());
            }
        }
    }

    // ==================== READ ====================

    /**
     * Busca un grabador por su ID.
     */
    public GrabadorExterno buscarPorId(int id) {
        String sql = "SELECT * FROM GrabadorExterno WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al buscar por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los grabadores activos.
     */
    public List<GrabadorExterno> listarTodos() {
        List<GrabadorExterno> lista = new ArrayList<>();
        String sql = "SELECT * FROM GrabadorExterno WHERE activo = 1 ORDER BY marca, modelo";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista TODOS los grabadores incluyendo inactivos (para reportes).
     */
    public List<GrabadorExterno> listarTodosIncluyendoInactivos() {
        List<GrabadorExterno> lista = new ArrayList<>();
        String sql = "SELECT * FROM GrabadorExterno ORDER BY marca, modelo";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al listar todos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca grabadores por marca (búsqueda parcial, case-insensitive).
     */
    public List<GrabadorExterno> buscarPorMarca(String marca) {
        return buscarPorCampo("marca", marca);
    }

    /**
     * Busca grabadores por modelo (búsqueda parcial, case-insensitive).
     */
    public List<GrabadorExterno> buscarPorModelo(String modelo) {
        return buscarPorCampo("modelo", modelo);
    }

    /**
     * Busca grabadores por formato de grabación (búsqueda parcial).
     */
    public List<GrabadorExterno> buscarPorFormato(String formato) {
        return buscarPorCampo("formatos", formato);
    }

    /**
     * Busca grabadores por resolución (búsqueda parcial).
     */
    public List<GrabadorExterno> buscarPorResolucion(String resolucion) {
        return buscarPorCampo("resolucion", resolucion);
    }

    /**
     * Búsqueda general por cualquier campo de texto (RF06).
     *
     * @param termino término de búsqueda
     * @return lista de grabadores que coinciden en marca, modelo, formato o resolución
     */
    public List<GrabadorExterno> buscarGeneral(String termino) {
        List<GrabadorExterno> lista = new ArrayList<>();
        String sql = "SELECT * FROM GrabadorExterno WHERE activo = 1 AND "
                   + "(marca LIKE ? OR modelo LIKE ? OR formatos LIKE ? OR resolucion LIKE ? OR conexiones LIKE ?) "
                   + "ORDER BY marca, modelo";
        String like = "%" + termino + "%";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, like);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error en búsqueda general: " + e.getMessage());
        }
        return lista;
    }

    public List<GrabadorExterno> buscarFiltrado(String termino, String marca, String modelo,
                                                 String formato, String resolucion,
                                                 String disponibilidad) {
        List<GrabadorExterno> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT g.* FROM GrabadorExterno g WHERE g.activo = 1");
        List<String> parametros = new ArrayList<>();
        agregarFiltroTexto(sql, parametros, "(g.marca LIKE ? OR g.modelo LIKE ? OR g.formatos LIKE ? "
                + "OR g.resolucion LIKE ? OR g.conexiones LIKE ?)", termino, 5);
        agregarFiltroTexto(sql, parametros, "g.marca LIKE ?", marca, 1);
        agregarFiltroTexto(sql, parametros, "g.modelo LIKE ?", modelo, 1);
        agregarFiltroTexto(sql, parametros, "g.formatos LIKE ?", formato, 1);
        agregarFiltroTexto(sql, parametros, "g.resolucion LIKE ?", resolucion, 1);
        if ("Disponible".equals(disponibilidad)) {
            sql.append(" AND EXISTS (SELECT 1 FROM Inventario i WHERE i.grabador_id = g.id ")
               .append("AND i.stock > 0 AND i.estado = 'disponible')");
        } else if ("No disponible".equals(disponibilidad)) {
            sql.append(" AND NOT EXISTS (SELECT 1 FROM Inventario i WHERE i.grabador_id = g.id ")
               .append("AND i.stock > 0 AND i.estado = 'disponible')");
        }
        sql.append(" ORDER BY g.marca, g.modelo");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) stmt.setString(i + 1, parametros.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al filtrar grabadores: " + e.getMessage());
        }
        return lista;
    }

    private void agregarFiltroTexto(StringBuilder sql, List<String> parametros,
                                    String condicion, String valor, int repeticiones) {
        if (valor == null || valor.isBlank()) return;
        sql.append(" AND ").append(condicion);
        String like = "%" + valor.trim() + "%";
        for (int i = 0; i < repeticiones; i++) parametros.add(like);
    }

    /**
     * Método auxiliar para búsqueda por un campo específico.
     */
    private List<GrabadorExterno> buscarPorCampo(String campo, String valor) {
        List<GrabadorExterno> lista = new ArrayList<>();
        String sql = "SELECT * FROM GrabadorExterno WHERE activo = 1 AND " + campo + " LIKE ? ORDER BY marca, modelo";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + valor + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al buscar por " + campo + ": " + e.getMessage());
        }
        return lista;
    }

    // ==================== UPDATE ====================

    /**
     * Actualiza los datos de un grabador existente.
     */
    public boolean actualizar(GrabadorExterno g) {
        String sql = "UPDATE GrabadorExterno SET marca = ?, modelo = ?, numeroSerie = ?, anio = ?, "
                   + "formatos = ?, resolucion = ?, conexiones = ?, almacenamiento = ?, precio = ?, "
                   + "imagenRuta = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, g.getMarca());
            pstmt.setString(2, g.getModelo());
            pstmt.setString(3, g.getNumeroSerie());
            pstmt.setInt(4, g.getAnio());
            pstmt.setString(5, g.getFormatos());
            pstmt.setString(6, g.getResolucion());
            pstmt.setString(7, g.getConexiones());
            pstmt.setString(8, g.getAlmacenamiento());
            pstmt.setDouble(9, g.getPrecio());
            pstmt.setString(10, g.getImagenRuta());
            pstmt.setInt(11, g.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al actualizar: " + e.getMessage());
        }
        return false;
    }

    // ==================== DELETE (lógica) ====================

    /**
     * Desactiva un grabador (eliminación lógica - RF05).
     */
    public boolean desactivar(int id) {
        String sql = "UPDATE GrabadorExterno SET activo = 0 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GrabadorExternoDAO] Error al desactivar: " + e.getMessage());
        }
        return false;
    }

    // ==================== UTILIDAD ====================

    /**
     * Mapea un ResultSet a un objeto GrabadorExterno.
     */
    private GrabadorExterno mapear(ResultSet rs) throws SQLException {
        GrabadorExterno g = new GrabadorExterno();
        g.setId(rs.getInt("id"));
        g.setMarca(rs.getString("marca"));
        g.setModelo(rs.getString("modelo"));
        g.setNumeroSerie(rs.getString("numeroSerie"));
        g.setAnio(rs.getInt("anio"));
        g.setFormatos(rs.getString("formatos"));
        g.setResolucion(rs.getString("resolucion"));
        g.setConexiones(rs.getString("conexiones"));
        g.setAlmacenamiento(rs.getString("almacenamiento"));
        g.setPrecio(rs.getDouble("precio"));
        g.setImagenRuta(rs.getString("imagenRuta"));
        g.setActivo(rs.getInt("activo") == 1);
        return g;
    }
}
