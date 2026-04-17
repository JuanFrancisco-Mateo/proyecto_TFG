package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.BarcoDAO;
import es.cifpcarlos3.proyecto.model.Barco;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.time.LocalDate;
import java.time.LocalTime;

public class BarcoDAOImpl implements BarcoDAO {
    private final DatabaseConnection db;
    public BarcoDAOImpl(DatabaseConnection db) {
        this.db = db;
    }
    public Barco devolverBarcoDisponible(LocalDate fecha, LocalTime hora){
        //Pongo esto por ahora para que no me de error
        Barco b= new Barco();
        return b;
    }
}
