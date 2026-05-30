package es.cifpcarlos3.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Barco {
    private int idBarco;
    private String nombre;
    private int capacidad;

    @Override
    public String toString(){
        return nombre + "(máx: "+capacidad+")";
    }
}
