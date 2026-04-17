package es.cifpcarlos3.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data


public class Usuario {
    private int idUsuario;
    private String nombre;//nombre completo
    private String email;
    private String telefono;
    private String username;
    private String passwordHash; //Se guarda el hash de la contraseña, no la contraseña real
    private Rol rol;
}
