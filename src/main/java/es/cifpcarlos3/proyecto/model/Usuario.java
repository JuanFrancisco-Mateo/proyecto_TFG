package es.cifpcarlos3.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data


public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String dni;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String passwordHash; //Se guarda el hash de la contraseña, no la contraseña real
    private Rol rol;
}
