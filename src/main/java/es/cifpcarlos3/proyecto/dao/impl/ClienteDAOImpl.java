package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {
    private final DatabaseConnection db;

    public ClienteDAOImpl(DatabaseConnection db) {
        this.db = db;
    }

    private Cliente mapearCliente(ResultSet rdo) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rdo.getInt("idCliente"));
        cliente.setNombre(rdo.getString("nombre"));
        cliente.setApellidos(rdo.getString("apellidos"));
        cliente.setDni(rdo.getString("dni"));
        Date fechaNac = rdo.getDate("fechaNacimiento");
        if (fechaNac != null) {
            cliente.setFechaNacimiento(fechaNac.toLocalDate());
        }
        cliente.setTelefono(rdo.getInt("telefono"));
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
        return cliente;
    }

    @Override
    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String consulta = "SELECT * FROM clientes ORDER BY idCliente";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rdo = stmt.executeQuery(consulta)) {
            while (rdo.next()) {
                lista.add(mapearCliente(rdo));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente devolverCliente(int idCliente) {
        String consulta = "SELECT * FROM clientes WHERE idCliente = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return mapearCliente(rdo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Cliente buscarPorDni(String dni) {
        String consulta = "SELECT * FROM clientes WHERE dni = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, dni);
            try (ResultSet rdo = stmt.executeQuery()) {
                if (rdo.next()) {
                    return mapearCliente(rdo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por DNI: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> lista = new ArrayList<>();
        String consulta = "SELECT * FROM clientes WHERE nombre LIKE ? OR apellidos LIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, "%" + nombre + "%");
            stmt.setString(2, "%" + nombre + "%");
            try (ResultSet rdo = stmt.executeQuery()) {
                while (rdo.next()) {
                    lista.add(mapearCliente(rdo));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void crearCliente(Cliente cliente) {
        String consulta = "INSERT INTO clientes (nombre, apellidos, dni, fechaNacimiento, telefono, email, telefonoUrgencia, certificacion, numeroSeguro, seguroHasta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellidos());
            stmt.setString(3, cliente.getDni());
            stmt.setDate(4, cliente.getFechaNacimiento() != null ? Date.valueOf(cliente.getFechaNacimiento()) : null);
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getEmail());
            stmt.setString(7, cliente.getTelefonoUrgencia());
            stmt.setString(8, cliente.getCertificacion() != null ? cliente.getCertificacion().name() : null);
            stmt.setString(9, cliente.getNumeroSeguro());
            stmt.setDate(10, cliente.getSeguroHasta() != null ? Date.valueOf(cliente.getSeguroHasta()) : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
        }
    }

    @Override
    public void modifCliente(Cliente cliente) {
        String consulta = "UPDATE clientes SET nombre=?, apellidos=?, dni=?, fechaNacimiento=?, telefono=?, email=?, telefonoUrgencia=?, certificacion=?, numeroSeguro=?, seguroHasta=? WHERE idCliente=?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellidos());
            stmt.setString(3, cliente.getDni());
            stmt.setDate(4, cliente.getFechaNacimiento() != null ? Date.valueOf(cliente.getFechaNacimiento()) : null);
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getEmail());
            stmt.setString(7, cliente.getTelefonoUrgencia());
            stmt.setString(8, cliente.getCertificacion() != null ? cliente.getCertificacion().name() : null);
            stmt.setString(9, cliente.getNumeroSeguro());
            stmt.setDate(10, cliente.getSeguroHasta() != null ? Date.valueOf(cliente.getSeguroHasta()) : null);
            stmt.setInt(11, cliente.getIdCliente());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar cliente: " + e.getMessage());
        }
    }

    @Override
    public void eliminarCliente(int idCliente) {
        String consulta = "DELETE FROM clientes WHERE idCliente = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
    }

    @Override
    public void addEspecialidad(int idCliente, int idEspecialidad) {
        String consulta = "INSERT INTO cliente_especialidad (idCliente, idEspecialidad) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEspecialidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al añadir especialidad al cliente: " + e.getMessage());
        }
    }

    @Override
    public void removeEspecialidad(int idCliente, int idEspecialidad) {
        String consulta = "DELETE FROM cliente_especialidad WHERE idCliente = ? AND idEspecialidad = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEspecialidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar especialidad del cliente: " + e.getMessage());
        }
    }

    @Override
    public List<Especialidad> getEspecialidades(int idCliente) {
        List<Especialidad> especialidades = new ArrayList<>();
        String consulta = "SELECT e.nombre FROM especialidades e " +
                          "JOIN cliente_especialidad ce ON e.idEspecialidad = ce.idEspecialidad " +
                          "WHERE ce.idCliente = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rdo = stmt.executeQuery()) {
                while (rdo.next()) {
                    especialidades.add(Especialidad.valueOf(rdo.getString("nombre")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener especialidades del cliente: " + e.getMessage());
        }
        return especialidades;
    }
}