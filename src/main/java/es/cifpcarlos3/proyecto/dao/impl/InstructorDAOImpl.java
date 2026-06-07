package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.InstructorDAO;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.model.Instructor;

import java.time.LocalDate;
import java.time.LocalTime;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class InstructorDAOImpl implements InstructorDAO {

    private final DatabaseConnection db;

    public InstructorDAOImpl(DatabaseConnection db) {
        this.db = db;
    }


    private Instructor mapearInstructor(ResultSet rdo) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setIdInstructor(rdo.getInt("idInstructor"));
        instructor.setNombre(rdo.getString("nombre"));
        instructor.setApellidos(rdo.getString("apellidos"));
        instructor.setDni(rdo.getString("dni"));
        Date fechaNac = rdo.getDate("fechaNacimiento");
        if (fechaNac != null) {
            instructor.setFechaNacimiento(fechaNac.toLocalDate());
        }
        instructor.setTelefono(rdo.getString("telefono"));
        instructor.setEmail(rdo.getString("email"));
        instructor.setTelefonoUrgencia(rdo.getString("telefonoUrgencia"));
        String cert = rdo.getString("certificacion");
        if (cert != null && !cert.isEmpty()) {
            instructor.setCertificacion(Certificacion.valueOf(cert));
        }
        return instructor;
    }

    @Override
    public List<Instructor> listarInstructores() {
        List<Instructor> lista = new ArrayList<>();
        String consulta = "SELECT * FROM instructores ORDER BY idInstructor";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rdo = stmt.executeQuery(consulta)) {
            while (rdo.next()) {
                lista.add(mapearInstructor(rdo));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar instructores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Instructor devolverInstructor(int idInstructor) {
        String consulta = "SELECT * FROM instructores WHERE idInstructor = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idInstructor);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return mapearInstructor(rdo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener instructor: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Instructor> buscarDisponibles(LocalDate fecha, LocalTime hora) {
        List<Instructor> lista = new ArrayList<>();
        String consulta = "SELECT i.* FROM instructores i WHERE i.idInstructor NOT IN (" +
                          "SELECT r.idInstructor FROM reservas r WHERE r.fecha = ? AND r.hora = ? AND r.idInstructor IS NOT NULL)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setTime(2, Time.valueOf(hora));
            try (ResultSet rdo = stmt.executeQuery()) {
                while (rdo.next()) {
                    lista.add(mapearInstructor(rdo));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar instructores disponibles: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void crearInstructor(Instructor instructor) throws SQLException{
        String consulta = "INSERT INTO instructores (nombre, apellidos, dni, fechaNacimiento, telefono, email, telefonoUrgencia, certificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, instructor.getNombre());
            stmt.setString(2, instructor.getApellidos());
            stmt.setString(3, instructor.getDni());
            stmt.setDate(4, instructor.getFechaNacimiento() != null ? Date.valueOf(instructor.getFechaNacimiento()) : null);
            stmt.setString(5, instructor.getTelefono());
            stmt.setString(6, instructor.getEmail());
            stmt.setString(7, instructor.getTelefonoUrgencia());
            stmt.setString(8, instructor.getCertificacion() != null ? instructor.getCertificacion().name() : null);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                int idCliente = rs.getInt(1);
                for(Especialidad esp : instructor.getEspecialidades()){
                    int idEspecialidad = getIdEspecialidadPorNombre(esp.name());
                    addEspecialidad(idCliente, idEspecialidad);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear instructor: " + e.getMessage());
        }
    }

    @Override
    public void modifInstructor(Instructor instructor) {
        String consulta = "UPDATE instructores SET nombre=?, apellidos=?, dni=?, fechaNacimiento=?, telefono=?, email=?, telefonoUrgencia=?, certificacion=? WHERE idInstructor=?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, instructor.getNombre());
            stmt.setString(2, instructor.getApellidos());
            stmt.setString(3, instructor.getDni());
            stmt.setDate(4, instructor.getFechaNacimiento() != null ? Date.valueOf(instructor.getFechaNacimiento()) : null);
            stmt.setString(5, instructor.getTelefono());
            stmt.setString(6, instructor.getEmail());
            stmt.setString(7, instructor.getTelefonoUrgencia());
            stmt.setString(8, instructor.getCertificacion() != null ? instructor.getCertificacion().name() : null);
            stmt.setInt(9, instructor.getIdInstructor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar instructor: " + e.getMessage());
        }
    }

    @Override
    public void eliminarInstructor(int idInstructor) {
        String consulta = "DELETE FROM instructores WHERE idInstructor = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idInstructor);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar instructor: " + e.getMessage());
        }
    }

    @Override
    public void addEspecialidad(int idInstructor, int idEspecialidad) {
        String consulta = "INSERT INTO instructor_especialidad (idInstructor, idEspecialidad) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idInstructor);
            stmt.setInt(2, idEspecialidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al añadir especialidad al instructor: " + e.getMessage());
        }
    }

    @Override
    public void removeEspecialidad(int idInstructor, int idEspecialidad) {
        String consulta = "DELETE FROM instructor_especialidad WHERE idInstructor = ? AND idEspecialidad = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idInstructor);
            stmt.setInt(2, idEspecialidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar especialidad del instructor: " + e.getMessage());
        }
    }

    @Override
    public int getIdEspecialidadPorNombre(String nombre) {
        String sql = "SELECT idEspecialidad FROM especialidades WHERE nombre = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idEspecialidad");
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo id especialidad: " + e.getMessage());
        }
        return -1;    }
}
