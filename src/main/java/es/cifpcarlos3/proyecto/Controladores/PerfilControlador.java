package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Sesion;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import es.cifpcarlos3.proyecto.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerfilControlador {

    @javafx.fxml.FXML
    private PasswordField tfPassword;
    @javafx.fxml.FXML
    private PasswordField tfRepetir;
    @javafx.fxml.FXML
    private Text txtPasswordNueva;
    @javafx.fxml.FXML
    private Text txtRepetir;
    @javafx.fxml.FXML
    private ImageView imgPerfil;
    @javafx.fxml.FXML
    private Button btnModifDatos;
    @javafx.fxml.FXML
    private PasswordField tfPasswordNueva;
    @javafx.fxml.FXML
    private Text txtPassword;
    @javafx.fxml.FXML
    private Button btnGuardar;
    @javafx.fxml.FXML
    private Button btnCambiarPassword;
    @javafx.fxml.FXML
    private Button btnGuardarDatos;
    @javafx.fxml.FXML
    private TextField txtUserName;
    @javafx.fxml.FXML
    private AnchorPane btnCambioPassword;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private TextField txtRol;
    @javafx.fxml.FXML
    private TextField txtTlf;


    private UsuarioDAO usuarioDAO;
    DatabaseConnection db;
    Usuario usuario;
    private static final Logger log = LogManager.getLogger(PerfilControlador.class);

    public void initialize(){

        db = new DatabaseConnection();
        usuarioDAO = new UsuarioDAOImpl(db);

        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();

        txtPasswordNueva.setVisible(false);
        txtPassword.setVisible(false);
        txtRepetir.setVisible(false);
        tfPassword.setVisible(false);
        tfPasswordNueva.setVisible(false);
        tfRepetir.setVisible(false);
        btnGuardar.setVisible(false);
        btnGuardarDatos.setVisible(false);


        // Mostrar datos del usuario
        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtRol.setText(usuario.getRol().toString());
            txtEmail.setText(usuario.getEmail());
            txtTlf.setText(usuario.getTelefono());
            txtUserName.setText(usuario.getUsername());
        }

        txtTlf.setDisable(true);
        txtEmail.setDisable(true);
        txtRol.setDisable(true);
        txtNombre.setDisable(true);
        txtUserName.setDisable(true);

    }


    @javafx.fxml.FXML
    public void modificarDatos(ActionEvent actionEvent) {

        log.info("Se van a modificar los datos del usuario");

        String email = txtEmail.getText();
        String telefono = txtTlf.getText();

        if (email.isEmpty() || telefono.isEmpty()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Faltan datos");
            alerta.showAndWait();
            log.warn("No se pueden cambiar los datos del usuario ya que hay campos vacios");
            return;
        }
        // Validar email simple (que tenga @)
        if (!email.contains("@")) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Email inválido");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("El email no es válido");
            alerta.showAndWait();
            return;
        }

        // Validar teléfono (9 dígitos)
        if (!telefono.matches("[0-9]{9}")) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Teléfono inválido");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("El teléfono debe tener 9 dígitos");
            alerta.showAndWait();
            return;
        }

        // Guardar cambios
        usuarioDAO.modifUsuario(usuario.getIdUsuario(), email, telefono);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        txtEmail.setText(email);
        txtTlf.setText(telefono);
        txtTlf.setDisable(true);
        txtEmail.setDisable(true);
        btnModifDatos.setVisible(true);
        btnGuardarDatos.setVisible(false);
        log.info("Se han cambiado los datos del usuario");

        //informamos al usuario
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Datos modificados");
        alerta.setHeaderText("Éxito");
        alerta.setContentText("Datos actualizados correctamente");
        alerta.showAndWait();

    }

    @javafx.fxml.FXML
    public void cambiarPassword(Event event) {
        log.info("Se va a cambiar la contraseña del usuario");

        String passActual = tfPassword.getText();
        String passNueva = tfPasswordNueva.getText();
        String passRepetir = tfRepetir.getText();

        if (passActual.isBlank() || passNueva.isBlank() || passRepetir.isBlank()){

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Tiene que completar los tres campos");
            alerta.showAndWait();
            log.warn("No se pueden cambiar la contraseña, hay campos vacios");
            return;
        }else {
            if (!PasswordUtil.verificarPassword(passActual, usuario.getPasswordHash())) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Contraseña incorrecta");
                alerta.showAndWait();
                log.warn("La contraseña es incorrecta, no se puede cambiar");
            } else if (!passNueva.equals(passRepetir)) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Las contraseñas no coinciden");
                alerta.showAndWait();
                log.warn("Las contraseñas no coinciden, no se puede cambiar");
            } else {

                String nuevaHash = PasswordUtil.hashPassword(passNueva);
                usuarioDAO.modifPassword(usuario.getIdUsuario(), nuevaHash);
                usuario.setPasswordHash(nuevaHash);
                log.info("La contraseña se ha cambiado");
                //Se vuelven a ocultar los campos
                txtPasswordNueva.setVisible(false);
                txtPassword.setVisible(false);
                txtRepetir.setVisible(false);
                tfPassword.setVisible(false);
                tfPasswordNueva.setVisible(false);
                tfRepetir.setVisible(false);
                btnGuardar.setVisible(false);
                btnCambioPassword.setVisible(true);

                //informamos al usuario
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setHeaderText("Confirmación");
                alerta.setContentText("Contraseña cambiada con éxito");
                alerta.showAndWait();
            }
        }
    }

    @javafx.fxml.FXML
    public void mostrarPassword(ActionEvent actionEvent) {//Cuando el usuario quiere modificar la contraseña
        //Muestro los campos en los que puede escribir
        txtPasswordNueva.setVisible(true);
        txtPassword.setVisible(true);
        txtRepetir.setVisible(true);
        tfPassword.setVisible(true);
        tfPasswordNueva.setVisible(true);
        tfRepetir.setVisible(true);

        //Quito el boton de cambiar contraseña y muestro el de guardar
        btnCambiarPassword.setVisible(false);
        btnGuardar.setVisible(true);
    }

    @javafx.fxml.FXML
    public void mostrarCampos(ActionEvent actionEvent) {
        //Habilito los campos que puede cambiar, quito el boton de modificar datos y muestro el de guardar los cambios
        txtTlf.setDisable(false);
        txtEmail.setDisable(false);
        btnModifDatos.setVisible(false);
        btnGuardarDatos.setVisible(true);
    }
}
