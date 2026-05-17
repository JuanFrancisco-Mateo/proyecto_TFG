package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Instructor;

import java.time.LocalDate;
import java.time.LocalTime;

public interface InstructorDAO {
    Instructor devolverInstructorDisponible(LocalDate fecha, LocalTime hora);
}
