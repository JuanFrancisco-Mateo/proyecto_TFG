package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Barco;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BarcoDAO {
    Barco devolverBarcoDisponible(LocalDate fecha, LocalTime hora);
}
