package es.cifpcarlos3.proyecto.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
public class Reserva {
    private int id;
    private Inmersion inmersion;
    private LocalDate fecha;
    private LocalTime hora;
    private Instructor instructor;
    private List<Cliente> clientes;
}
