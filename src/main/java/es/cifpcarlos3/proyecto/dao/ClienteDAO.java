package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;

import java.util.List;

public interface ClienteDAO {
    List<Cliente> listarClientes();
    Cliente devolverCliente(int idCliente);
    Cliente buscarPorDni(String dni);
    List<Cliente> buscarPorNombre(String nombre);
    void crearCliente(Cliente cliente);
    void modifCliente(Cliente cliente);
    void eliminarCliente(int idCliente);
    void addEspecialidad(int idCliente, int idEspecialidad);
    void removeEspecialidad(int idCliente, int idEspecialidad);
    List<Especialidad> getEspecialidades(int idCliente);
    int getIdEspecialidadPorNombre(String nombre);
}