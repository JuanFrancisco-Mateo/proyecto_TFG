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
            // Primero crear la BD si no existe (ejecutamos con CREATE DATABASE aparte)
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS centro_buceo";
            stmt.execute(createDbSQL);
            stmt.execute("USE centro_buceo");

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
                // Saltar líneas de CREATE DATABASE y USE (ya las ejecutamos arriba)
                if (line.trim().startsWith("CREATE DATABASE") || line.trim().startsWith("USE ")) {
                    continue;
                }
                // Saltar comentarios de una línea
                if (line.trim().startsWith("--")) {
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
