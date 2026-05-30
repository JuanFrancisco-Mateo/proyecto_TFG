package es.cifpcarlos3.proyecto.model;

public enum Certificacion {
    //Puestos por orden
    //Recreativo
    SCUBA ("Scuba"),
    OWD("Open Water Diver") , //open water driver
    AOWD("Advanced Open Water Diver"), //Advanced open water driver
    RESCUE ("Rescue Diver"),
    MASTERSCUBA("Master Scuba Diver"),
    //profesional
    DIVEMASTER("Divemaster"),
    ASSISTANT("Assistant instructor"),
    OWSI("Open Water Scuba Instructor"), //open water scuba instructor
    MASTERSCUBATAINER("Master Scuba Trainer");

    private final String nombre;

    Certificacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
