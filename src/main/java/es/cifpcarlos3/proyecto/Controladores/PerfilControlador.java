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

    Usuario usuario;
    private UsuarioDAO usuarioDAO;
    DatabaseConnection db;

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
        }

        txtTlf.setDisable(true);
        txtEmail.setDisable(true);
        txtRol.setDisable(true);
        txtNombre.setDisable(true);

    }


    @javafx.fxml.FXML
    public void modificarDatos(ActionEvent actionEvent) {
        String email = txtEmail.getText();
        String telefono = txtTlf.getText();

        if (email.isEmpty() || telefono.isEmpty()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Faltan datos");
            alerta.showAndWait();
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

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Datos modificados");
        alerta.setHeaderText("Éxito");
        alerta.setContentText("Datos actualizados correctamente");
        alerta.showAndWait();
    }

    @javafx.fxml.FXML
    public void cambiarPassword(Event event) {
        String passActual = tfPassword.getText();
        String passNueva = tfPasswordNueva.getText();
        String passRepetir = tfRepetir.getText();

        if (passActual.isBlank() || passNueva.isBlank() || passRepetir.isBlank()){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Tiene que completar los tres campos");
            alerta.showAndWait();
            return;
        }

        // Hashear las contraseñas
        String passwordHash = Base64.getEncoder().encodeToString(passActual.getBytes());
        String passwordNuevaHash = Base64.getEncoder().encodeToString(passNueva.getBytes());
        String passwordRepetirHash = Base64.getEncoder().encodeToString(passRepetir.getBytes());

        // Comprobar que la contraseña actual es correcta
        if (!passwordHash.equals(usuario.getPasswordHash())) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error en la contraseña");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Contraseña incorrecta");
            alerta.showAndWait();
            return;
        }

        // Comprobar que las nuevas coinciden
        if (!passwordNuevaHash.equals(passwordRepetirHash)) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error en la contraseña");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Las contraseñas no coinciden");
            alerta.showAndWait();
            return;
        }

        // Guardar nueva contraseña
        usuarioDAO.modifPassword(usuario.getIdUsuario(), passwordNuevaHash);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Contraseña cambiada");
        alerta.setHeaderText("Éxito");
        alerta.setContentText("Contraseña actualizada correctamente");
        alerta.showAndWait();
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
