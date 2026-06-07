package es.cifpcarlos3.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Usuario extends Persona{
    private int idUsuario;
    private String username;
    private String passwordHash; //Se guarda el hash de la contraseña, no la contraseña real
    private Rol rol;

    @Override
    public String toString(){
        return nombre+ " "+ apellidos+" - " +dni;
    }
}
