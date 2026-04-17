package es.cifpcarlos3.proyecto.model;


public class Sesion {
    private static Usuario usuario;

    public static void inicioSession(Usuario user){
        usuario=user;
    }
    public static Usuario recuperarUsuario() {
        return usuario;
    }
    public static void cerrarSesion(){
        usuario = null;
    }

}
