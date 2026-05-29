package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Base64;

public class CrearUsuarioControlador {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTlf;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<Rol> cbRoles;

    private DatabaseConnection db = new DatabaseConnection();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl(db);

    @FXML
    public void initialize() {
        // Cargar los roles en el ComboBox
        cbRoles.getItems().setAll(Rol.values());
    }

    @FXML
    public void crearUsuario(ActionEvent actionEvent) {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String telefono = txtTlf.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        Rol rol = cbRoles.getValue();

        // Validar que no hay campos vacíos
        if (nombre.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || rol == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Todos los campos son obligatorios");
            alerta.showAndWait();
            return;
        }

        // Hashear la contraseña
        String passwordHash = Base64.getEncoder().encodeToString(password.getBytes());

        // Crear el usuario llamando al DAO con los parámetros sueltos
        usuarioDAO.crearUsuario(nombre, email, telefono, username, passwordHash, rol);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Usuario creado");
        alerta.setHeaderText("Éxito");
        alerta.setContentText("Usuario creado correctamente");
        alerta.showAndWait();
    }
}
