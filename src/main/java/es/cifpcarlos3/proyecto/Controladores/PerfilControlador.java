package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Sesion;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerfilControlador {

    @javafx.fxml.FXML
    private PasswordField tfPassword;
    @javafx.fxml.FXML
    private Text txtEmail;
    @javafx.fxml.FXML
    private PasswordField tfRepetir;
    @javafx.fxml.FXML
    private Text txtPasswordNueva;
    @javafx.fxml.FXML
    private Text txtNombre;
    @javafx.fxml.FXML
    private Text txtRepetir;
    @javafx.fxml.FXML
    private ImageView imgPerfil;
    @javafx.fxml.FXML
    private Button btnModifDatos;
    @javafx.fxml.FXML
    private PasswordField tfPasswordNueva;
    @javafx.fxml.FXML
    private Text txtRol;
    @javafx.fxml.FXML
    private Text txtTlf;
    @javafx.fxml.FXML
    private Text txtPassword;
    @javafx.fxml.FXML
    private Button btnGuardar;
    @javafx.fxml.FXML
    private Button btnCambiarPassword;
    @javafx.fxml.FXML
    private Button btnGuardarDatos;

    /*
    Usuario usuario;
    private UsuarioDAO usuarioDAO;
    DatabaseConnection db;
     */

    public void initialize(){
        /*
        db = new DatabaseConnection();
        usuarioDAO = new UsuarioDAOImpl(db);
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();
        */
        txtPasswordNueva.setVisible(false);
        txtPassword.setVisible(false);
        txtRepetir.setVisible(false);
        tfPassword.setVisible(false);
        tfPasswordNueva.setVisible(false);
        tfRepetir.setVisible(false);
        btnGuardar.setVisible(false);
        btnGuardarDatos.setVisible(false);

        /*
       txtNombre.setText(usuario.getNombre());
       txtRol.setText(usuario.getRol().toString());
       txtEmail.setText(usuario.getEmail());
       txtTlf.setText(usuario.getTelefono());
         */

        txtTlf.setDisable(true);
        txtEmail.setDisable(true);
        txtRol.setDisable(true);
        txtNombre.setDisable(true);

    }


    @javafx.fxml.FXML
    public void modificarDatos(ActionEvent actionEvent) {
        /*
        if(txtEmail.getText().isEmpty() || txtTlf.getText().isEmpty()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Faltan datos");
            alerta.showAndWait();
        }else{
            Pattern patternEmail = Pattern.compile("");
            Matcher matcherEmail = patternEmail.matcher(txtEmail.getText());
            Pattern patternTlf = Pattern.compile("[0-9]{9}");
            Matcher matcherTlf = patternEmail.matcher(txtTlf.getText());
            if(matcherEmail.matches() && matcherTlf.matches()){
                String email= txtEmail.getText();
                String telefono=txtTlf.getText();
              usuarioDAO.modifUsuario(usuario.getIdUsuario(), email, telefono);
            }
        }

         */
    }

    @javafx.fxml.FXML
    public void cambiarPassword(Event event) {
        /*
        if(tfPassword.getText().isBlank() || tfPasswordNueva.getText().isBlank() || tfRepetir.getText().isBlank()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Tiene que completar los tres campos");
            alerta.showAndWait();
        }else {
            String password = Base64.getEncoder().encodeToString(tfPassword.getText().getBytes());
            String passwordNueva = Base64.getEncoder().encodeToString(tfPasswordNueva.getText().getBytes());
            String passwordRepetir = Base64.getEncoder().encodeToString(tfRepetir.getText().getBytes());
            if (!password.equals(usuario.getPasswordHash())) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Contraseña incorrecta");
                alerta.showAndWait();
            } else if (!passwordNueva.equals(passwordRepetir)) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error en la contraseña");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("Las contraseñas no coinciden");
                alerta.showAndWait();
            } else {
                usuarioDAO.modifPassword(usuario.getIdUsuario(), passwordNueva);

            }
        }


         */

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
