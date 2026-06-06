package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;

import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {
    List<Cliente> listarClientes();
    Cliente devolverCliente(int idCliente);
    List<Cliente> buscarPorDni(String dni);
    List<Cliente> buscarPorNombre(String nombre);
    void crearCliente(Cliente cliente) throws SQLException;
    void modifCliente(Cliente cliente);
    void eliminarCliente(int idCliente);
    void addEspecialidad(int idCliente, int idEspecialidad);
    void removeEspecialidad(int idCliente, int idEspecialidad);
    List<Especialidad> getEspecialidades(int idCliente);
    int getIdEspecialidadPorNombre(String nombre);
}