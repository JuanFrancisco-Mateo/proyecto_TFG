package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.BarcoDAO;
import es.cifpcarlos3.proyecto.model.Barco;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BarcoDAOImpl implements BarcoDAO {
    private final DatabaseConnection db;
    public BarcoDAOImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public List<Barco> devolverBarcoDisponible(LocalDate fecha, LocalTime hora) {

        List<Barco> barcos = new ArrayList<>();

        // Buscar un barco que no tenga inmersiones programadas en esa fecha/hora
        String consulta = "SELECT b.* FROM barcos b WHERE b.idBarco NOT IN (" +
                          "SELECT r.idBarco FROM reservas r "+
                          "WHERE r.fecha = ? AND r.hora = ? AND r.idBarco IS NOT NULL)";
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
                    barcos.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar barco disponible: " + e.getMessage());
        }
        return barcos;
    }

    @Override
    public Barco devolverBarco(int idBarco){
        String consulta = "SELECT * FROM barcos WHERE idBarco = ?";
        try(Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idBarco);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return mapearBarco(rdo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener barco: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Barco> listarBarcos() {
        List<Barco> lista = new ArrayList<>();
        String consulta = "SELECT * FROM barcos ORDER BY idBarco";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rdo = stmt.executeQuery(consulta)) {
            while (rdo.next()) {
                lista.add(mapearBarco(rdo));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar barcos: " + e.getMessage());
        }
        return lista;
    }

    private Barco mapearBarco(ResultSet rdo) throws SQLException{
        Barco barco = new Barco();
        barco.setIdBarco(rdo.getInt("idBarco"));
        barco.setCapacidad(rdo.getInt("capacidad"));
        barco.setNombre(rdo.getString("nombre"));
        return barco;
    }
}
