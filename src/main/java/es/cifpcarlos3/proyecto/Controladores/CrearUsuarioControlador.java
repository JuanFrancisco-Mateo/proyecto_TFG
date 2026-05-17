package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import es.cifpcarlos3.proyecto.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private TextField txtPassword;

    private DatabaseConnection db;
    private UsuarioDAO usuarioDAO;
    private static final Logger log = LogManager.getLogger(CrearUsuarioControlador.class);

    public void initialize(){
        db = new DatabaseConnection();
        //usuarioDAO = new UsuarioDAOImpl(db);
        cbRoles.getItems().setAll(Rol.values());
    }
    @javafx.fxml.FXML
    public void crearUsuario(ActionEvent actionEvent) {
        if(txtApellidos.getText().isEmpty() ||
                txtNombre.getText().isEmpty()
                || txtDni.getText().isBlank()
                || txtEmail.getText().isBlank()
                || txtTlf.getText().isBlank()
                || txtPassword.getText().isBlank()
                || txtFecha.getValue() == null
                || cbRoles.getValue() == null){

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Debe completar todos los campos");
            alerta.showAndWait();
            log.warn("No se puede guardar el usuario, faltan datos por completar");

        }else {
            Pattern patternTlf = Pattern.compile("[0-9]{9}");
            Matcher matcherTlf = patternTlf.matcher(txtTlf.getText());

            if(!matcherTlf.matches()){

                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("El teléfono debe tener 9 dígitos");
                alerta.showAndWait();

                log.warn("Formato incorrecto teléfono");

                return;
            }

            try {
                Usuario usuario = new Usuario();
                usuario.setRol(cbRoles.getValue());
                usuario.setNombre(txtNombre.getText());
                usuario.setEmail(txtEmail.getText());
                usuario.setApellidos(txtApellidos.getText());
                usuario.setTelefono(txtTlf.getText());
                usuario.setDni(txtDni.getText());
                usuario.setFechaNacimiento(txtFecha.getValue());
                usuario.setRol(cbRoles.getValue());
                usuario.setPasswordHash(PasswordUtil.hashPassword(txtPassword.getText()));
                //usuarioDAO.crearUsuario(usuario);

                //se cierra la ventana
                btnGuardarUsuario.getScene().getWindow().hide();

            }catch (NumberFormatException e){
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
