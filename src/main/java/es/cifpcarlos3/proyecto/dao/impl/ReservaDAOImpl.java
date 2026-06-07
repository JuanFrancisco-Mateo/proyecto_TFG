package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.ReservaDAO;
import es.cifpcarlos3.proyecto.model.*;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements ReservaDAO {
    private final DatabaseConnection db;

    public ReservaDAOImpl(DatabaseConnection db) {
        this.db = db;
    }

    private Reserva mapearReserva(ResultSet rdo) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rdo.getInt("idReserva"));

        // Mapear inmersión (datos básicos)
        Inmersion inmersion= new Inmersion();
        inmersion.setIdInmersion(rdo.getInt("idInmersion"));
        inmersion.setTipo(rdo.getString("tipo"));
        //inmersion.setLugar(rdo.getString("lugar"));
        /*
        if ("BARCO".equals(tipo)) {
            InmersionBarco ib = new InmersionBarco();
            ib.setIdInmersion(idInmersion);
            // El barco se cargaría por separado si se necesita
            inmersion = ib;
        } else {
            InmersionCosta ic = new InmersionCosta();
            ic.setIdInmersion(idInmersion);
            ic.setLugar(rdo.getString("lugar") != null ? rdo.getString("lugar") : "");
            inmersion = ic;
        }*/
        inmersion.setNombre(rdo.getString("nombre"));
        String certMin = rdo.getString("certificacionMinima");
        if (certMin != null && !certMin.isEmpty()) {
            inmersion.setCertificacionMinima(Certificacion.valueOf(certMin));
        }
        inmersion.setPlazasMax(rdo.getInt("plazasMax"));
        inmersion.setPrecio(rdo.getDouble("precio"));
        inmersion.setDuracionMin(rdo.getInt("duracionMin"));

        reserva.setInmersion(inmersion);
        reserva.setFecha(rdo.getDate("fecha").toLocalDate());
        reserva.setHora(rdo.getTime("hora").toLocalTime());

        // Instructor (podría cargarse por separado)
        int idInstructor = rdo.getInt("idInstructor");
        if (!rdo.wasNull()) {
            Instructor instructor = new Instructor();
            instructor.setIdInstructor(idInstructor);
            reserva.setInstructor(instructor);
        }

        return reserva;
    }

    @Override
    public List<Reserva> listarReservas() {
        List<Reserva> lista = new ArrayList<>();
        String consulta = "SELECT r.*, i.nombre, i.certificacionMinima, i.plazasMax, i.precio, i.duracionMin, i.tipo, ic.lugar " +
                          "FROM reservas r " +
                          "JOIN inmersion i ON r.idInmersion = i.idInmersion " +
                          "LEFT JOIN inmersion_costa ic ON i.idInmersion = ic.idInmersion " +
                          "ORDER BY r.fecha, r.hora";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rdo = stmt.executeQuery(consulta)) {
            while (rdo.next()) {
                lista.add(mapearReserva(rdo));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar reservas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Reserva devolverReserva(int idReserva) {
        String consulta = "SELECT r.*, i.nombre, i.certificacionMinima, i.plazasMax, i.precio, i.duracionMin, i.tipo, ic.lugar " +
                          "FROM reservas r " +
                          "JOIN inmersion i ON r.idInmersion = i.idInmersion " +
                          "LEFT JOIN inmersion_costa ic ON i.idInmersion = ic.idInmersion " +
                          "WHERE r.idReserva = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return mapearReserva(rdo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reserva: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int crearReserva(Reserva reserva) {
        String consulta = "INSERT INTO reservas (idInmersion, fecha, hora, idInstructor, idBarco) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reserva.getInmersion().getIdInmersion());
            stmt.setDate(2, Date.valueOf(reserva.getFecha()));
            stmt.setTime(3, Time.valueOf(reserva.getHora()));
            if (reserva.getInstructor() != null) {
                stmt.setInt(4, reserva.getInstructor().getIdInstructor());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (reserva.getBarco() != null)
                stmt.setInt(5, reserva.getBarco().getIdBarco());
            else
                stmt.setNull(5, Types.INTEGER);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear reserva: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void modifReserva(Reserva reserva) {
        String consulta = "UPDATE reservas SET idInmersion=?, fecha=?, hora=?, idInstructor=? WHERE idReserva=?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, reserva.getInmersion().getIdInmersion());
            stmt.setDate(2, Date.valueOf(reserva.getFecha()));
            stmt.setTime(3, Time.valueOf(reserva.getHora()));
            if (reserva.getInstructor() != null) {
                stmt.setInt(4, reserva.getInstructor().getIdInstructor());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, reserva.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar reserva: " + e.getMessage());
        }
    }

    @Override
    public void eliminarReserva(int idReserva) {
        String consulta = "DELETE FROM reservas WHERE idReserva = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
        }
    }

    @Override
    public void addCliente(int idReserva, int idCliente) {
        String consulta = "INSERT INTO reserva_clientes (idReserva, idCliente) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            stmt.setInt(2, idCliente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al añadir cliente a la reserva: " + e.getMessage());
        }
    }

    @Override
    public void removeCliente(int idReserva, int idCliente) {
        String consulta = "DELETE FROM reserva_clientes WHERE idReserva = ? AND idCliente = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            stmt.setInt(2, idCliente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente de la reserva: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> getClientes(int idReserva) {
        List<Cliente> clientes = new ArrayList<>();
        String consulta = "SELECT c.* FROM clientes c " +
                          "JOIN reserva_clientes rc ON c.idCliente = rc.idCliente " +
                          "WHERE rc.idReserva = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            try (ResultSet rdo = stmt.executeQuery()) {
                while (rdo.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rdo.getInt("idCliente"));
                    cliente.setNombre(rdo.getString("nombre"));
                    cliente.setApellidos(rdo.getString("apellidos"));
                    cliente.setDni(rdo.getString("dni"));
                    Date fechaNac = rdo.getDate("fechaNacimiento");
                    if (fechaNac != null) {
                        cliente.setFechaNacimiento(fechaNac.toLocalDate());
                    }
                    cliente.setTelefono(rdo.getString("telefono"));
                    cliente.setEmail(rdo.getString("email"));
                    cliente.setTelefonoUrgencia(rdo.getString("telefonoUrgencia"));
                    String cert = rdo.getString("certificacion");
                    if (cert != null && !cert.isEmpty()) {
                        cliente.setCertificacion(Certificacion.valueOf(cert));
                    }
                    cliente.setNumeroSeguro(rdo.getString("numeroSeguro"));
                    Date seguroHasta = rdo.getDate("seguroHasta");
                    if (seguroHasta != null) {
                        cliente.setSeguroHasta(seguroHasta.toLocalDate());
                    }
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes de la reserva: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public boolean isDisponible(LocalDate fecha, LocalTime hora, int idInstructor) {
        String consulta = "SELECT COUNT(*) FROM reservas WHERE fecha = ? AND hora = ? AND idInstructor = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setTime(2, Time.valueOf(hora));
            stmt.setInt(3, idInstructor);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return rdo.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al comprobar disponibilidad: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int contarClientes(int idReserva) {
        String consulta = "SELECT COUNT(*) FROM reserva_clientes WHERE idReserva = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return rdo.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar clientes: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getPlazasMaximas(int idReserva) {
        String consulta = "SELECT i.plazasMax FROM reservas r JOIN inmersion i ON r.idInmersion = i.idInmersion WHERE r.idReserva = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idReserva);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return rdo.getInt("plazasMax");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener plazas máximas: " + e.getMessage());
        }
        return 0;
    }
}