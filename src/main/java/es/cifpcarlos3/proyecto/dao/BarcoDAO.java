package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Barco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BarcoDAO {
    List<Barco> devolverBarcoDisponible(LocalDate fecha, LocalTime hora);
    Barco devolverBarco(int idBarco);
    List<Barco> listarBarcos();
}
