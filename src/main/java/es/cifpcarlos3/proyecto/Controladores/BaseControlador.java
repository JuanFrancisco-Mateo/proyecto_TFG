package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Sesion;
import es.cifpcarlos3.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class BaseControlador { //Controlar tanto el menu latera como el menu superior

    @javafx.fxml.FXML
    private ImageView imgLogo;
    @javafx.fxml.FXML
    private AnchorPane contenido;

    @FXML
    private Label btnReservas;
    @FXML
    private Label btnInicio;
    @FXML
    private Label btnClientes;
    @FXML
    private MenuButton menuUsuario;
    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox menuSuperior;
    @FXML
    private VBox menuLateral;
    @FXML
    private MenuButton btnAdministracion;

    private static final Logger log = LogManager.getLogger(BaseControlador.class);

    public void initialize(){

        //recuperamos el usuario que inicio sesion
         Usuario usuario = Sesion.recuperarUsuario();


        //La primera vista que se muestra es la del calendario (Inicio)
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("inicio.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            borderPane.setRight(null);
            AnchorPane.setTopAnchor(centro, 0.0);
            AnchorPane.setBottomAnchor(centro, 0.0);
            AnchorPane.setLeftAnchor(centro, 0.0);
            AnchorPane.setRightAnchor(centro, 0.0);

            contenido.getChildren().setAll(centro);

        }catch(IOException e){
            System.err.println("Error al cargar la vista del calendario");
            log.warn("Error al cargar el calendario");
        }

        menuUsuario.setText(usuario.getNombre());
        //al inicio comprueba el rol para poner el menu administrador o no
        if(usuario.getRol()!= Rol.ADMINISTRADOR){ //Solo si es administrador puede ver la opcion administracion en el menu
          btnAdministracion.setVisible(false);
            log.info("El usuario no es adminsitrador por lo que no tiene acceso a la opcion Administracion del menu lateral");
        }

    }

    @FXML
    public void irReservas(Event event) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearReservas.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            AnchorPane.setTopAnchor(centro, 0.0);
            AnchorPane.setBottomAnchor(centro, 0.0);
            AnchorPane.setLeftAnchor(centro, 0.0);
            AnchorPane.setRightAnchor(centro, 0.0);

            contenido.getChildren().setAll(centro);
            log.info("El usuario ha accedido a 'Nueva reserva'");

        }catch(IOException e){
            System.err.println("Error al cargar la vista Reservas");
            log.warn("Error al cargar la vista Reservas");
        }
    }

    @FXML
    public void irClientes(Event event) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("listaClientes.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            AnchorPane.setTopAnchor(centro, 0.0);
            AnchorPane.setBottomAnchor(centro, 0.0);
            AnchorPane.setLeftAnchor(centro, 0.0);
            AnchorPane.setRightAnchor(centro, 0.0);

            contenido.getChildren().setAll(centro);
            log.info("El usuario ha accedido a 'Clientes'");
        }catch(IOException e){
            System.err.println("Error al cargar la vista clientes");
            log.warn("Error al cargar la lista de clientes del menu lateral");
        }
    }


    @FXML
    public void irInicio(Event event) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("inicio.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            AnchorPane.setTopAnchor(centro, 0.0);
            AnchorPane.setBottomAnchor(centro, 0.0);
            AnchorPane.setLeftAnchor(centro, 0.0);
            AnchorPane.setRightAnchor(centro, 0.0);

            contenido.getChildren().setAll(centro);
            log.info("El usuario ha accedido a 'Inicio'");

        }catch(IOException e){
            System.err.println("Error al cargar la vista de Inicio");
            log.warn("Error al cargar la vista de inicio en el menu lateral");
        }
    }

    @FXML
    public void cerrarSesion(ActionEvent actionEvent) {
        /*
        Sesion.cerrarSesion();
         */
        Stage ventana= (Stage) btnInicio.getScene().getWindow();
        salir(ventana);
        log.info("El usuario ha cerrado sesion");
    }
    public void salir(Stage ventana){
            try {
                FXMLLoader vistaLogin = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                Parent root = vistaLogin.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesLogin.css").toExternalForm());
                Stage stage = new Stage();
                stage.setTitle("Login");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                System.err.println("ERROR. Fallo al cargar la nueva vista");
                log.warn("Error al cargar la pantalla de login tras cerrar sesion");
            }
            ventana.close();
        }


    @FXML
    public void verPerfil(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("perfil.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            contenido.getChildren().add(centro);
            log.info("El usuario ha accedido a 'Perfil'");
        }catch(IOException e){
            System.err.println("Error al cargar la vista del perfil");
            log.warn("Error al cargar el perfil del menu superior");
        }
    }

    @FXML
    public void irUsuarios(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("listaUsuarios.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            contenido.getChildren().add(centro);

            contenido.getChildren().setAll(centro);
            log.info("El usuario (administrador) ha accedido a 'Usuarios'");
        }catch(IOException e){
            System.err.println("Error al cargar la vista lista de usuarios");
            log.warn("Error al cargar la lista de usuarios del menu lateral");
        }
    }

    @FXML
    public void irInstructores(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("listaInstructores.fxml"));
            AnchorPane centro = vista.load();

            contenido.getChildren().clear();
            contenido.getChildren().add(centro);

            contenido.getChildren().setAll(centro);
            log.info("El usuario (administrador) ha accedido a 'Instructores'");
        }catch(IOException e){
            System.err.println("Error al cargar la vista lista de usuarios");
            log.warn("Error al cargar la lista de instructores del menu lateral");
        }
    }
}


