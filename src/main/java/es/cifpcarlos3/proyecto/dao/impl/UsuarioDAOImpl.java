package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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
                    return null;
                } else {
                    String nombre = rdo.getString("nombre");
                    String apellidos = rdo.getString("apellidos");
                    Rol rol = Rol.valueOf(rdo.getString("rol"));
                    int id = rdo.getInt("idUsuario");
                    String email = rdo.getString("email");
                    String tlf = rdo.getString("telefono");
                    LocalDate fechaNac = rdo.getDate("fechaNacimiento").toLocalDate();
                    String password = rdo.getString("passwordHash");
                    String dni = rdo.getString("dni");
                    Usuario usuario = new Usuario(id, nombre, apellidos, email, dni, tlf, fechaNac, password, rol);
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
        final String consulta = "INSERT INTO usuario (nombre, email, telefono, username, passwordHash, rol) VALUES (?, ?, ?, ?, ?, ?)";

        try(var conexion  = db.getConnection();
            var sentencia = conexion.prepareStatement(consulta) ){

            sentencia.setString(1, nombre);
            sentencia.setString(2, email);
            sentencia.setString(3, tlf);
            sentencia.setString(4, userName);
            sentencia.setString(5, passwordHash);
            sentencia.setString(6, rol.toString());
            sentencia.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error al añadir el cliente: " + e.getMessage());
        }
    }
    @Override
    public void modifUsuario(int idUsuario, String email, String tlf){
        String consulta = "UPDATE usuario SET email = ?, telefono = ? WHERE idUsuario = ?";
        try (var conexion = db.getConnection();
             var sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, email);
            sentencia.setString(2, tlf);
            sentencia.setInt(3, idUsuario);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        }
    }

    @Override
    public void modifPassword(int idUsuario, String password){
        String consulta = "UPDATE usuario SET passwordHash = ? WHERE idUsuario = ?";
        try (var conexion = db.getConnection();
             var sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, password);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar password: " + e.getMessage());
        }
    }

    @Override
    public void eliminarUsuario(String userName){
        String consulta = "DELETE FROM usuario WHERE username = ?";

        try(var conexion  = db.getConnection();
            var sentencia = conexion.prepareStatement(consulta) ){

            sentencia.setString(1, userName);
            sentencia.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
        }
    }
}