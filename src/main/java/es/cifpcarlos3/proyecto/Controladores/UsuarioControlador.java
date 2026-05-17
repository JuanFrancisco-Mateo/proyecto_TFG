package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UsuarioControlador {
    @javafx.fxml.FXML
    private Button btnModifUsuario;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private TextField txtApellidos;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private ComboBox<Rol> cbRol;

    Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(UsuarioControlador.class);

    public void initialize(){
        db = new DatabaseConnection();
        //usuarioDAO = new UsuarioDAOImpl(db);

        cbRol.getItems().setAll(Rol.values());
        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtEmail.setDisable(true);
        txtFecha.setDisable(true);
        txtDni.setDisable(true);
        txtTlf.setDisable(true);
    }

    public void setUsuario(Usuario usuario){

        this.usuario = usuario;

        txtNombre.setText(usuario.getNombre());
        txtApellidos.setText(usuario.getApellidos());
        txtDni.setText(usuario.getDni());
        txtEmail.setText(usuario.getEmail());
        txtTlf.setText(usuario.getTelefono());
        txtFecha.setValue(usuario.getFechaNacimiento());
        cbRol.setValue(usuario.getRol());
    }

    @javafx.fxml.FXML
    public void modifUsuario(ActionEvent actionEvent) {
        Rol nuevoRol = cbRol.getValue();

        usuario.setRol(nuevoRol);

        //usuarioDAO.modificarRol(usuario.getIdUsuario(), nuevoRol);
        log.info("Rol actualizado del usuario");
    }
}
