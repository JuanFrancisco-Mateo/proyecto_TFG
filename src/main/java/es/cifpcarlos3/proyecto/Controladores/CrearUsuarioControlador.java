package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import es.cifpcarlos3.proyecto.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Base64;

public class CrearUsuarioControlador {



    @javafx.fxml.FXML
    private Button btnGuardarUsuario;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private TextField txtApellidos;
    @javafx.fxml.FXML
    private ComboBox<Rol> cbRoles;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private TextField txtPassword;
    @FXML
    private TextField txtUsername;

    private DatabaseConnection db;
    private UsuarioDAO usuarioDAO;
    private static final Logger log = LogManager.getLogger(CrearUsuarioControlador.class);


    public void initialize(){
        db = new DatabaseConnection();
        usuarioDAO = new UsuarioDAOImpl(db);
        cbRoles.getItems().setAll(Rol.values());
    }
    @javafx.fxml.FXML
    public void crearUsuario(ActionEvent actionEvent) {
        if (txtApellidos.getText().isEmpty() ||
                txtNombre.getText().isEmpty()
                || txtUsername.getText().isBlank()
                || txtEmail.getText().isBlank()
                || txtTlf.getText().isBlank()
                || txtPassword.getText().isBlank()
                || cbRoles.getValue() == null) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Debe completar todos los campos");
            alerta.showAndWait();
            log.warn("No se puede guardar el usuario, faltan datos por completar");

        } else {
            Pattern patternTlf = Pattern.compile("[0-9]{9}");
            Matcher matcherTlf = patternTlf.matcher(txtTlf.getText());

            if (!matcherTlf.matches()) {

                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("El teléfono debe tener 9 dígitos");
                alerta.showAndWait();

                log.warn("Formato incorrecto teléfono");

                return;
            }

            try {
                Rol rol =cbRoles.getValue();
                String nombre=txtNombre.getText();
                String apellidos= txtApellidos.getText();
                String email = txtEmail.getText();
                String tlf=txtTlf.getText();
                String username = txtUsername.getText();
                String passwordHash = PasswordUtil.hashPassword(txtPassword.getText());
                usuarioDAO.crearUsuario(nombre, apellidos, email, tlf, username, passwordHash, rol);

                //se cierra la ventana
                btnGuardarUsuario.getScene().getWindow().hide();

            } catch (NumberFormatException e) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("El formato del número de teléfono es incorrecto");
                alerta.showAndWait();
                log.warn("No se puede guardar el usuario, formato incorrecto del numero de telefonon");
            }
        }
    }

}
