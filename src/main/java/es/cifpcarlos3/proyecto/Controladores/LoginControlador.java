package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Sesion;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Base64;

public class LoginControlador {

    @FXML
    private Label labelUser;
    @FXML
    private Button btnAcceder;
    @FXML
    private Label labelBienvenido;
    @FXML
    private Label labelNombre;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfUserName;


    private DatabaseConnection db = new DatabaseConnection();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl(db);

    @FXML
    public void acceder(ActionEvent actionEvent) {
        String user = tfUserName.getText();
        String password = tfPassword.getText();

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioDAO.devolverUser(user);

        // Si el usuario no existe
        if (usuario == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error en el usuario");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Usuario no encontrado");
            alerta.showAndWait();
            return;
        }

        // Comprobar la contraseña
        String passwordHash = Base64.getEncoder().encodeToString(password.getBytes());
        if (usuario.getPasswordHash().equals(passwordHash)) {
            // Login correcto: crear sesión y abrir ventana principal
            Sesion.inicioSession(usuario);
            Stage ventanaLogin = (Stage) labelNombre.getScene().getWindow();
            ventanaInicio(ventanaLogin);
        } else {
            // Contraseña incorrecta
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error en la contraseña");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Contraseña incorrecta");
            alerta.showAndWait();
        }
    }

    public void ventanaInicio(Stage ventana){
        try {
            FXMLLoader vistaInicio= new FXMLLoader(HelloApplication.class.getResource("base.fxml"));
            Parent root = vistaInicio.load();
            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("INICIO");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(false);
            stage.show();


        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la nueva vista");
        }
        ventana.close();

    }
}