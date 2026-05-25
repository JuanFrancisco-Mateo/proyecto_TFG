package es.cifpcarlos3.proyecto.dao;

import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaDAO {
    List<Reserva> listarReservas();
    Reserva devolverReserva(int idReserva);
    int crearReserva(Reserva reserva);
    void modifReserva(Reserva reserva);
    void eliminarReserva(int idReserva);
    void addCliente(int idReserva, int idCliente);
    void removeCliente(int idReserva, int idCliente);
    List<Cliente> getClientes(int idReserva);
    boolean isDisponible(LocalDate fecha, LocalTime hora, int idInstructor);
    int contarClientes(int idReserva);
    int getPlazasMaximas(int idReserva);
}