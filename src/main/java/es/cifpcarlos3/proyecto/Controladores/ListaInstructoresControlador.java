package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.InstructorDAO;
import es.cifpcarlos3.proyecto.dao.UsuarioDAO;
import es.cifpcarlos3.proyecto.dao.impl.InstructorDAOImpl;
import es.cifpcarlos3.proyecto.dao.impl.UsuarioDAOImpl;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Instructor;
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

public class ListaInstructoresControlador {
    @javafx.fxml.FXML
    private TableColumn<Instructor, Integer> colTlf;
    @javafx.fxml.FXML
    private TableColumn<Instructor, Certificacion> colCertificacion;
    @javafx.fxml.FXML
    private TableColumn<Instructor, String> colNombre;
    @javafx.fxml.FXML
    private Button btnCrearInstructor;
    @javafx.fxml.FXML
    private TableColumn<Instructor, String> colEmail;
    @javafx.fxml.FXML
    private TableView<Instructor> tableInstructores;

    Usuario usuario;
    ObservableList<Instructor> listaInstructores = FXCollections.observableArrayList();

    DatabaseConnection db = new DatabaseConnection();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl(db);
    private InstructorDAO instructorDAO;


    public void initialize(){
        tableInstructores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usuario = Sesion.recuperarUsuario();
        instructorDAO = new InstructorDAOImpl(db);

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTlf.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCertificacion.setCellValueFactory(new PropertyValueFactory<>("certificacion"));

        tableInstructores.setItems(listaInstructores);

        cargarInstructores();

        tableInstructores.setOnMouseClicked(event ->{
            Instructor instructorSeleccionado = tableInstructores.getSelectionModel().getSelectedItem();

            if(instructorSeleccionado != null){
                datosInstructor(instructorSeleccionado);
            }
        });
    }

    public void cargarInstructores(){
        List<Instructor> instructores = instructorDAO.listarInstructores();
        listaInstructores.setAll(instructores);
    }
    @javafx.fxml.FXML
    public void crearInstructor(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearInstructor.fxml"));
            Parent root = vista.load();
            Scene scene = new Scene(root, 640, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Nuevo Instructor");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            stage.setOnHidden(e -> cargarInstructores());
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la vista crear instructor");
        }
    }

    public void datosInstructor(Instructor instructor){
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("instructor.fxml"));
            Parent root = vista.load();
            //el controlador de la vista que se va a abrir (ficha con los datos del instructor)
            InstructorControlador controlador = vista.getController();
            controlador.setInstructor(instructor); //le pasamos el objeto instructor para que aparezcan sus datos
            Scene scene = new Scene(root, 640, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Datos del instructor");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> cargarInstructores());
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la ficha del instructor");
        }
    }
}
