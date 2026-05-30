package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Rol;
import es.cifpcarlos3.proyecto.model.Sesion;
import es.cifpcarlos3.proyecto.model.Usuario;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListaUsuariosControlador {

    @javafx.fxml.FXML
    private TableColumn<Usuario, Integer> colTlf;
    @javafx.fxml.FXML
    private TableColumn<Usuario, String> colDni;
    @javafx.fxml.FXML
    private TableColumn<Usuario, Rol> colRol;
    @javafx.fxml.FXML
    private TableColumn<Usuario, String> colNombre;
    @javafx.fxml.FXML
    private TableColumn<Usuario, String> colEmail;
    @javafx.fxml.FXML
    private TableView<Usuario> tableUsuarios;
    @javafx.fxml.FXML
    private Button btnCrearUser;

    Usuario usuario;
    ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();


    DatabaseConnection db = new DatabaseConnection();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl(db);

    public void initialize(){
        tableUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        usuario = Sesion.recuperarUsuario();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTlf.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        tableUsuarios.setItems(listaUsuarios);

        cargarUsuarios();
        tableUsuarios.setOnMouseClicked(event -> {
            //obtenemos el cliente seleccionado
            Usuario usuarioSeleccionado = tableUsuarios.getSelectionModel().getSelectedItem();

            if(usuarioSeleccionado != null){
                datosUsuario(usuarioSeleccionado);
            }
        });
    }

    public void cargarUsuarios(){
        /*
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();

        listaUsuarios.setAll(usuarios);
        */
    }

    @javafx.fxml.FXML
    public void crearUser(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearUsuario.fxml"));
            Parent root = vista.load();
            Scene scene = new Scene(root, 640, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            stage.setOnHidden(e -> cargarUsuarios());
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la vista crear usuario");
        }
    }
    public void datosUsuario(Usuario usuario){
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("usuario.fxml"));
            Parent root = vista.load();
            //el controlador de la vista que se va a abrir (ficha con los datos del cliente)
            UsuarioControlador controlador = vista.getController();
            controlador.setUsuario(usuario); //le pasamos el objeto cliente para que aparezcan sus datos
            Scene scene = new Scene(root, 640, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Datos del usuario");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la ficha del usuario");
        }
    }
}
