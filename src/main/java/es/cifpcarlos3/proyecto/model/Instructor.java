package es.cifpcarlos3.proyecto.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data


public class Instructor extends Persona{
    private int idInstructor;
    private Certificacion certificacion;
    private List<Especialidad> especialidades;
}
