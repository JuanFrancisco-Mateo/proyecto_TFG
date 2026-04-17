package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;

public interface UsuarioDAO {
    Usuario devolverUser(String userName); //Recuperar un objeto usuario
    void crearUsuario(String nombre, String email, String tlf, String userName, String passwordHash, Rol rol); //Crear un usuario
    void modifUsuario(int idUsuario, String email, String telefono); //Para que los usuarios puedan modificar el email o el telefono

    void eliminarUsuario(String userName); //Para eliminar un usuario, solo lo usan administradores
    void modifPassword(int idUsuario, String password); //Para que los usuarios puedan cambiar su contraseña
}
