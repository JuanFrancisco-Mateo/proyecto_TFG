package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.model.Instructor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface InstructorDAO {
    List<Instructor> listarInstructores();
    Instructor devolverInstructor(int idInstructor);
    List<Instructor> buscarDisponibles(LocalDate fecha, LocalTime hora);
    void crearInstructor(Instructor instructor) throws SQLException;
    void modifInstructor(Instructor instructor);
    void eliminarInstructor(int idInstructor);
    void addEspecialidad(int idInstructor, int idEspecialidad);
    void removeEspecialidad(int idInstructor, int idEspecialidad);
    int getIdEspecialidadPorNombre(String nombre);
}
