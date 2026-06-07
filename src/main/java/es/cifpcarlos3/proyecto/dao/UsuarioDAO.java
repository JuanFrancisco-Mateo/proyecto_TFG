package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface UsuarioDAO {
    Usuario devolverUser(String userName); //Recuperar un objeto usuario
    void crearUsuario(String nombre, String apellidos, String email, String tlf, String userName, String passwordHash, Rol rol, String dni, LocalDate fechaNac) throws SQLException; //Crear un usuario
    void modifUsuario(int idUsuario, String email, String telefono); //Para que los usuarios puedan modificar el email o el telefono
    List<Usuario> listarUsuarios();
    void eliminarUsuario(String userName); //Para eliminar un usuario, solo lo usan administradores
    void modifPassword(int idUsuario, String password); //Para que los usuarios puedan cambiar su contraseña
    void modificarRol(int idUsuario, Rol rol);
}
