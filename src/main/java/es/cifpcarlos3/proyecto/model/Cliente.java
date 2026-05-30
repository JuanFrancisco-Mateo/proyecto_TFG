package es.cifpcarlos3.proyecto.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Persona{
    private int idCliente;
    private Certificacion certificacion;
    private List<Especialidad> especialidades;
    private String numeroSeguro;
    private LocalDate seguroHasta;
    private LocalDate fechaExp;

    public void addEspecialidad(Especialidad especialidad){}
    public void removeEspecialidad(Especialidad especialidad){}
}