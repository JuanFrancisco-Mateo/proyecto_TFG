package es.cifpcarlos3.proyecto.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data


public class Cliente extends Persona{
    private Certificacion certificacion;
    private List<Especialidad> especialidades;
    private String numeroSeguro;
    private LocalDate seguroHasta;

    public void addEspecialidad(Especialidad especialidad){}
    public void removeEspecialidad(Especialidad especialidad){}
}
