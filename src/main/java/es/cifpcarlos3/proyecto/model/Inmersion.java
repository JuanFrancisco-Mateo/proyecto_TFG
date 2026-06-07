package es.cifpcarlos3.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Inmersion {
    private int idInmersion;
    private String nombre;
    private String tipo;
    private Certificacion certificacionMinima;
    private int plazasMax;
    private double precio;
    private int duracionMin;
    private String lugar;


    @Override
    public String toString() {
        return nombre;
    }
}
