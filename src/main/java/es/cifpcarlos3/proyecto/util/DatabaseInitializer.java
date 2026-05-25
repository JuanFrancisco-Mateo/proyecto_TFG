package es.cifpcarlos3.proyecto.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseInitializer {
    private final DatabaseConnection db;

    public DatabaseInitializer(DatabaseConnection db) {
        this.db = db;
    }

    public void initialize() {
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            // Leer el script SQL desde resources
            InputStream is = getClass().getClassLoader().getResourceAsStream("init.sql");
            if (is == null) {
                System.err.println("No se encontró init.sql");
                return;
            }
            Scanner scanner = new Scanner(is).useDelimiter(";");
            while (scanner.hasNext()) {
                String sql = scanner.next().trim();
                if (!sql.isEmpty() && !sql.startsWith("--") && !sql.startsWith("CREATE DATABASE") && !sql.startsWith("USE ")) {
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        System.err.println("Error ejecutando SQL: " + e.getMessage());
                        System.err.println("SQL: " + sql);
                    }
                }
            }
            System.out.println("Base de datos inicializada correctamente");
        } catch (Exception e) {
            System.err.println("Error al inicializar BD: " + e.getMessage());
        }
    }
}