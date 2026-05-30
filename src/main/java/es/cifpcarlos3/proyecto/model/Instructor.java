package es.cifpcarlos3.proyecto.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Instructor extends Persona{
    private int idInstructor;
    private Certificacion certificacion;
    private List<Especialidad> especialidades = new ArrayList<>();

    @Override
    public String toString(){
        return nombre+ " "+apellidos+" - "+certificacion;
    }
}
