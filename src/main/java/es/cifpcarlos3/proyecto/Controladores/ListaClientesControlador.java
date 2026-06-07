package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.model.Cliente;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListaClientesControlador {

    @javafx.fxml.FXML
    private AnchorPane clientesContenido;
    @javafx.fxml.FXML
    private TableView<Cliente> tableClientes; //cada fila es un cliente
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> colTlf;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String>colDni;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> colNombre;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> colEmail;
    @javafx.fxml.FXML
    private Button btnCrearCliente;

    Usuario usuario;
    ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    DatabaseConnection db = new DatabaseConnection();
    private ClienteDAO clienteDAO = new ClienteDAOImpl(db);

    public void initialize(){

        tableClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        usuario = Sesion.recuperarUsuario();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTlf.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));


        tableClientes.setItems(listaClientes);

        cargarClientes();


        //funcion para escuchar si el usuario selecciona un cliente y abrir su informacion
        tableClientes.setOnMouseClicked(event -> {
            //obtenemos el cliente seleccionado
            Cliente clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();

            if(clienteSeleccionado != null){
                datosCliente(clienteSeleccionado);
            }
        });

    }

    public void cargarClientes(){

        List<Cliente> clientes = clienteDAO.listarClientes();

        listaClientes.setAll(clientes);

    }


    //Para crear un nuevo cliente se abre la vista por separado
    @javafx.fxml.FXML
    public void crearCliente(ActionEvent actionEvent) {
            try {
                FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearCliente.fxml"));
                Parent root = vista.load();
                Scene scene = new Scene(root, 700, 530);
                scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
                Stage stage = new Stage();
                stage.setTitle("Nuevo Cliente");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                //cuando se cierre la ventana al crear el cliente se vuelve a cargar la lista para que aparezca
                stage.setOnHidden(e -> cargarClientes());
            } catch (IOException e) {
                System.err.println("ERROR. Fallo al cargar la vista crear cliente");
            }
    }

    public void datosCliente(Cliente cliente){
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("cliente.fxml"));
            Parent root = vista.load();
            //el controlador de la vista que se va a abrir (ficha con los datos del cliente)
            ClientesControlador controlador = vista.getController();
            controlador.setCliente(cliente); //le pasamos el objeto cliente para que aparezcan sus datos
            Scene scene = new Scene(root, 700, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Datos del cliente");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> cargarClientes());
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la ficha del cliente");
        }
    }
}
