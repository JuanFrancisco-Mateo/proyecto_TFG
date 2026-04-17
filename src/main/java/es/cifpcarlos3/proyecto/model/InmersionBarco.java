package es.cifpcarlos3.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class InmersionBarco extends Inmersion{
    private Barco barco;
}
