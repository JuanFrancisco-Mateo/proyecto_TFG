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


       txtNombre.setText(usuario.getNombre());
       txtRol.setText(usuario.getRol().toString());
       txtEmail.setText(usuario.getEmail());
       txtTlf.setText(usuario.getTelefono());
       txtUserName.setText(usuario.getEmail());

        txtTlf.setDisable(true);
        txtEmail.setDisable(true);
        txtRol.setDisable(true);
        txtNombre.setDisable(true);

    }


    @javafx.fxml.FXML
    public void modificarDatos(ActionEvent actionEvent) {
        log.info("Se van a modificar los datos del usuario");
        if(txtEmail.getText().isEmpty() || txtTlf.getText().isEmpty()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Faltan datos");
            alerta.showAndWait();
            log.warn("No se pueden cambiar los datos del usuario ya que hay campos vacios");
        }else{
            Pattern patternTlf = Pattern.compile("[0-9]{9}");
            Matcher matcherTlf = patternTlf.matcher(txtTlf.getText());
            if(matcherTlf.matches()){
                String email= txtEmail.getText();
                String telefono=txtTlf.getText();
              //usuarioDAO.modifUsuario(usuario.getIdUsuario(), email, telefono);
                usuario.setEmail(email);
                usuario.setTelefono(telefono);
                txtEmail.setText(email);
                txtTlf.setText(telefono);
                txtTlf.setDisable(true);
                txtEmail.setDisable(true);
                btnModifDatos.setVisible(true);
                btnGuardarDatos.setVisible(false);
                log.info("Se han cambiado los datos del usuario");
            }else{
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("El formato del teléfono es incorrecto");
                alerta.showAndWait();
                log.warn("Los datos del usuario no se han podido modificar porque el número de telefono no cumple el formato");
            }
        }


    }

    @javafx.fxml.FXML
    public void cambiarPassword(Event event) {
        log.info("Se va a cambiar la contraseña del usuario");
        if(tfPassword.getText().isBlank() || tfPasswordNueva.getText().isBlank() || tfRepetir.getText().isBlank()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Tiene que completar los tres campos");
            alerta.showAndWait();
            log.warn("No se pueden cambiar la contraseña, hay campos vacios");
        }else {
            String password = tfPassword.getText();
            String passwordNueva = tfPasswordNueva.getText();
            String passwordRepetir = tfRepetir.getText();
            if (!PasswordUtil.verificarPassword(password, usuario.getPasswordHash())) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Contraseña incorrecta");
                alerta.showAndWait();
                log.warn("La contraseña es incorrecta, no se puede cambiar");
            } else if (!passwordNueva.equals(passwordRepetir)) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Las contraseñas no coinciden");
                alerta.showAndWait();
                log.warn("Las contraseñas no coinciden, no se puede cambiar");
            } else {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setHeaderText("Confirmación");
                alerta.setContentText("Contraseña cambiada con éxito");
                alerta.showAndWait();
                String nuevaHash = PasswordUtil.hashPassword(passwordNueva);
                //usuarioDAO.modifPassword(usuario.getIdUsuario(), nuevaHash);
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
