package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
    @javafx.fxml.FXML
    private Button btnDeleteUser;

    Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(UsuarioControlador.class);


    public void initialize(){
        db = new DatabaseConnection();
        usuarioDAO = new UsuarioDAOImpl(db);

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

        usuarioDAO.modificarRol(usuario.getIdUsuario(), nuevoRol);
        log.info("Rol actualizado del usuario");
    }

    @javafx.fxml.FXML
    public void deleteUser(ActionEvent actionEvent) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar Usuario");
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Está seguro de que quiere eliminar al usuario?");

        //Para coger lo seleccionado por el usuario
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioDAO.eliminarUsuario(usuario.getUsername());
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("Usuario eliminado correctamente");
            ok.showAndWait();
            //se cierra la ventana
            btnDeleteUser.getScene().getWindow().hide();
        }else{
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("No se pudo eliminar el usuario");
            ok.showAndWait();
        }
    }
}
