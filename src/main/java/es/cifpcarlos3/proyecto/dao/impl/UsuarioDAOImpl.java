package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAOImpl implements UsuarioDAO {
    private final DatabaseConnection db;
    public UsuarioDAOImpl(DatabaseConnection db) {
        this.db = db;
    }
    @Override
    public Usuario devolverUser(String userName){
        String consulta = "SELECT * FROM usuario WHERE username=?";

        try (var conexion = db.getConnection();
             var sentencia = conexion.prepareStatement(consulta)) {

            sentencia.setString(1, userName);

            try (ResultSet rdo = sentencia.executeQuery()) {
                if (!rdo.next()) {
                    return null; //Si no encuentra un profesor
                } else {
                    //habra que comprobar luego los nombres de los campos en las tablas
                    String nombre = rdo.getString("nombre");
                    Rol rol = Rol.valueOf(rdo.getString("rol"));
                    int id = rdo.getInt("idUsuario");
                    String email = rdo.getString("email");
                    String tlf = rdo.getString("telefono");
                    String username = rdo.getString("username");
                    String password = rdo.getString("passwordHash");
                    Usuario usuario = new Usuario(id, nombre, email, tlf, username, password, rol);
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el usuario " + e.getMessage());
            return null;
        }
    }
    @Override
    public void crearUsuario(String nombre, String email, String tlf, String userName, String passwordHash, Rol rol){
        final String consulta = "INSERT INTO usuario (nombre, email, telefono, username, passwordHash, rol) VALUES (?, ?, ?)";
        int filas=0;

        try(var conexion  = db.getConnection();
            var sentencia = conexion.prepareStatement(consulta) ){

            sentencia.setString(1, nombre);
            sentencia.setString(2, email);
            sentencia.setString(3, tlf);
            sentencia.setString(4, userName);
            sentencia.setString(5, passwordHash);
            sentencia.setString(6, rol.toString());
            filas=sentencia.executeUpdate();

            if(filas==0){
                System.out.println("No se ha podido crear el usuario");
            }else{
                System.out.println("Usuario creado corrrectamente");
            }
            System.out.println("Filas afectadas: " + filas);
        }catch (SQLException e){
            System.err.println("Error al añadir el cliente: " + e.getMessage());
        }
    }
    @Override
    public void modifUsuario(int idUsuario, String email, String tlf){}

    @Override
    public void modifPassword(int idUsuario, String password){}

    @Override
    public void eliminarUsuario(String userName){
        String consulta = "DELETE FROM usuario WHERE t_profesor.username = ?";
        int filas=0;

        try(var conexion  = db.getConnection();
            var sentencia = conexion.prepareStatement(consulta) ){

            sentencia.setString(1, userName);
            filas=sentencia.executeUpdate();

            if(filas!=0){
                System.out.println("Profesor no se ha podido eliminar");
            }else{
                System.out.println("Profesor eliminado corrrectamente");
            }

        }catch (SQLException e){
            System.err.println("Error al eliminar el profesor: " + e.getMessage());
        }
    }
}
