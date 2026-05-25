package es.cifpcarlos3.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reserva {
    private int id;
    private Inmersion inmersion;
    private LocalDate fecha;
    private LocalTime hora;
    private Instructor instructor;
    private List<Cliente> clientes;
}