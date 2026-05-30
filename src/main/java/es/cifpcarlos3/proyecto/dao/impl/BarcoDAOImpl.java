package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.BarcoDAO;
import es.cifpcarlos3.proyecto.model.Barco;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BarcoDAOImpl implements BarcoDAO {
    private final DatabaseConnection db;
    public BarcoDAOImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Barco devolverBarcoDisponible(LocalDate fecha, LocalTime hora) {
        // Buscar un barco que no tenga inmersiones programadas en esa fecha/hora
        String consulta = "SELECT b.* FROM barcos b WHERE b.idBarco NOT IN (" +
                          "SELECT ib.idBarco FROM inmersion_barco ib " +
                          "JOIN inmersion i ON ib.idInmersion = i.idInmersion " +
                          "JOIN reservas r ON r.idInmersion = i.idInmersion " +
                          "WHERE r.fecha = ? AND r.hora = ?) LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setTime(2, Time.valueOf(hora));
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    Barco b = new Barco();
                    b.setIdBarco(rdo.getInt("idBarco"));
                    b.setNombre(rdo.getString("nombre"));
                    b.setCapacidad(rdo.getInt("capacidad"));
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar barco disponible: " + e.getMessage());
        }
        return null;
    }
}
