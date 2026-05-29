package es.cifpcarlos3.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class Inmersion {
    protected int idInmersion;
    protected String nombre;
    protected String tipo;
    protected Certificacion certificacionMinima;
    protected int plazasMax;
    protected double precio;
    protected int duracionMin;
}
