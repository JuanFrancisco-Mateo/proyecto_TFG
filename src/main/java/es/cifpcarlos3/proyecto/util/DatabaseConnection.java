package es.cifpcarlos3.proyecto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL  = "jdbc:mariadb://localhost:3306/centro_buceo";
    private static final String USER = "root";
    private static final String PWD  = "";

    public Connection getConnection () throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }
}
