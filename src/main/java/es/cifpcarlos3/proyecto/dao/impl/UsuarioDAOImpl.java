package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                    String username = rdo.getString("username");
                    String password = rdo.getString("passwordHash");
                    Usuario usuario = new Usuario(id, nombre,apellidos, email, tlf, username, password, rol);
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el usuario " + e.getMessage());
            return null;
        }
    }
    @Override
    public void crearUsuario(String nombre, String apellidos, String email, String tlf, String userName, String passwordHash, Rol rol){
        final String consulta = "INSERT INTO usuario (nombre, apellidos, email, telefono, username, passwordHash, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(var conexion  = db.getConnection();
            var sentencia = conexion.prepareStatement(consulta) ){

            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, email);
            sentencia.setString(4, tlf);
            sentencia.setString(5, userName);
            sentencia.setString(6, passwordHash);
            sentencia.setString(7, rol.toString());
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
    @Override
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String consulta = "SELECT * FROM usuario ORDER BY idUsuario";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rdo = stmt.executeQuery(consulta)) {
            while (rdo.next()) {
                lista.add(mapearUsuario(rdo));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    private Usuario mapearUsuario(ResultSet rdo) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rdo.getInt("idUsuario"));
        usuario.setNombre(rdo.getString("nombre"));
        usuario.setApellidos(rdo.getString("apellidos"));
        usuario.setTelefono(rdo.getString("telefono"));
        usuario.setEmail(rdo.getString("email"));
        usuario.setUsername(rdo.getString("username"));
        usuario.setPasswordHash(rdo.getString("passwordHash"));
        usuario.setRol(Rol.valueOf(rdo.getString("rol")));
        return usuario;
    }

    @Override
    public void modificarRol(int idUsuario, Rol rol){
        String consulta ="UPDATE usuario SET rol = ? WHERE idUsuario = ?";

        try(var con = db.getConnection();
            var sentencia = con.prepareStatement(consulta)){

            sentencia.setString(1, rol.name());
            sentencia.setInt(2, idUsuario);

            sentencia.executeUpdate();

        }catch(SQLException e){
            System.err.println("Error al modificar el rol del usuarios: " + e.getMessage());

        }
    }
}