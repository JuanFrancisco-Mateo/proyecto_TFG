package es.cifpcarlos3.proyecto.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public abstract class Persona {
    protected String nombre;
    protected String apellidos;
    protected String dni;
    protected LocalDate fechaNacimiento;
    protected int telefono;
    protected String email;
    protected String telefonoUrgencia;

}
