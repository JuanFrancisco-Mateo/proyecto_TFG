package es.cifpcarlos3.proyecto.model;

public enum Especialidad {
    PROFUNDO("Profundo"),
    APNEA("Apnea"),
    CORRIENTES("Corrientes"),
    PECIOS("Pecios"),
    CUEVAS("Cuevas");
    private final String nombre;

    Especialidad(String nombre) {
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
