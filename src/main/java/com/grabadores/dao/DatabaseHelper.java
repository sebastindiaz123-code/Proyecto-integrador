package com.grabadores.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Singleton para gestionar la conexión a la base de datos SQLite.
 * Se encarga de crear las tablas automáticamente si no existen
 * y de cargar los datos de prueba en el primer inicio.
 *
 * @author Equipo Lior-Sebastian-Brayan — UNIAJC Grupo 411
 */
public class DatabaseHelper {

    /** Ruta del archivo de base de datos SQLite */
    private static final String DB_URL = "jdbc:sqlite:"
            + System.getProperty("grabadores.db.path", "grabadores.db");

    /** Instancia única del Singleton */
    private static DatabaseHelper instance;

    /** Conexión activa a la base de datos */
    private Connection connection;

    /**
     * Constructor privado — solo se invoca desde getInstance().
     * Establece la conexión y activa las claves foráneas.
     */
    private DatabaseHelper() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            // Activar soporte de claves foráneas (desactivado por defecto en SQLite)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA journal_mode = WAL");
            }
            System.out.println("[DB] Conexión a SQLite establecida correctamente.");
        } catch (SQLException e) {
            System.err.println("[DB] Error al conectar con SQLite: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la base de datos.", e);
        }
    }

    /**
     * Obtiene la instancia única del DatabaseHelper (patrón Singleton).
     * Si la conexión fue cerrada, la reabre automáticamente.
     *
     * @return instancia de DatabaseHelper
     */
    public static synchronized DatabaseHelper getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseHelper();
            }
        } catch (SQLException e) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /**
     * Devuelve la conexión activa a la base de datos.
     *
     * @return objeto Connection de JDBC
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Inicializa la base de datos: crea las tablas y carga datos de prueba
     * si la base de datos está vacía (primer inicio).
     */
    public void initializeDatabase() {
        try {
            // Ejecutar schema.sql para crear las tablas
            executeSQLFile("/sql/schema.sql");
            ensureColumn("Camara", "activo", "INTEGER NOT NULL DEFAULT 1");
            migrarImagenesExistentes();
            System.out.println("[DB] Esquema de base de datos verificado.");

            // Verificar si ya existen datos en la tabla Usuario
            if (isTableEmpty("Usuario")) {
                executeSQLFile("/sql/seed_data.sql");
                System.out.println("[DB] Datos de prueba cargados exitosamente.");
            } else {
                System.out.println("[DB] La base de datos ya contiene datos.");
            }

        } catch (Exception e) {
            System.err.println("[DB] Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ensureColumn(String tableName, String columnName, String definition) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) return;
            }
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        }
    }

    private void migrarImagenesExistentes() throws SQLException {
        String sql = "INSERT INTO Imagen (grabador_id, ruta, tipo_vista) "
                   + "SELECT g.id, g.imagenRuta, 'FRONTAL' FROM GrabadorExterno g "
                   + "WHERE g.imagenRuta IS NOT NULL AND TRIM(g.imagenRuta) <> '' "
                   + "AND NOT EXISTS (SELECT 1 FROM Imagen i WHERE i.grabador_id = g.id AND i.tipo_vista = 'FRONTAL')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Verifica si una tabla está vacía.
     *
     * @param tableName nombre de la tabla a verificar
     * @return true si la tabla no tiene registros
     */
    private boolean isTableEmpty(String tableName) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            // La tabla podría no existir aún
            return true;
        }
    }

    /**
     * Lee y ejecuta un archivo SQL desde los recursos del classpath.
     * Divide el archivo por ';' y ejecuta cada sentencia individualmente.
     *
     * @param resourcePath ruta del recurso SQL (ej: "/sql/schema.sql")
     */
    private void executeSQLFile(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("[DB] No se encontró el archivo: " + resourcePath);
                return;
            }

            String sql = new BufferedReader(new InputStreamReader(is, "UTF-8"))
                    .lines()
                    .filter(line -> !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            // Dividir por punto y coma para ejecutar cada sentencia
            String[] statements = sql.split(";");
            try (Statement stmt = connection.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DB] Error ejecutando " + resourcePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexión a la base de datos.
     * Debe llamarse al cerrar la aplicación.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Conexión a SQLite cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
