package es.cifpcarlos3.proyecto.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseConnection db;

    public DatabaseInitializer(DatabaseConnection db) {
        this.db = db;
    }

    public void initialize() {
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {

            // Leer el script SQL desde resources con UTF-8
            InputStream is = getClass().getClassLoader().getResourceAsStream("init.sql");
            if (is == null) {
                System.err.println("No se encontró init.sql");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Saltar líneas de CREATE DATABASE, USE y comentarios
                String trimmed = line.trim();
                if (trimmed.startsWith("CREATE DATABASE") || trimmed.startsWith("USE ") || trimmed.startsWith("--")) {
                    continue;
                }
                sqlScript.append(line).append("\n");
            }

            // Dividir por punto y coma y ejecutar cada sentencia
            String[] statements = sqlScript.toString().split(";");
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty() && !trimmedSql.startsWith("--")) {
                    try {
                        stmt.execute(trimmedSql);
                    } catch (SQLException e) {
                        System.err.println("Error ejecutando SQL: " + e.getMessage());
                        System.err.println("SQL: " + trimmedSql);
                    }
                }
            }
            System.out.println("Base de datos inicializada correctamente");
        } catch (Exception e) {
            System.err.println("Error al inicializar BD: " + e.getMessage());
        }
    }
}
