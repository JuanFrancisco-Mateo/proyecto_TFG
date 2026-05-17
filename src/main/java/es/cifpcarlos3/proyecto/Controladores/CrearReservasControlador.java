package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.*;
import es.cifpcarlos3.proyecto.model.*;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class CrearReservasControlador {
    @javafx.fxml.FXML
    private ComboBox<LocalTime> cbHora;
    @javafx.fxml.FXML
    private ComboBox<Barco> cbBarco;
    @javafx.fxml.FXML
    private Button btnAddCliente;
    @javafx.fxml.FXML
    private ListView<Cliente> lvClientes;
    @javafx.fxml.FXML
    private DatePicker dpFecha;
    @javafx.fxml.FXML
    private ComboBox<Instructor> cbInstructor;
    @javafx.fxml.FXML
    private Button btnCrear;
    @javafx.fxml.FXML
    private TextField tfPlazas;
    @javafx.fxml.FXML
    private ComboBox<String> cbTipo;
    @FXML
    private TextField tfPrecio;
    @FXML
    private ComboBox<Especialidad> cbEspecialidad;
    @FXML
    private ComboBox<Cliente> cbClientes;
    @FXML
    private Button btnCrearCliente;
    @FXML
    private TextField txtLugar;

    DatabaseConnection db;
    private InstructorDAO instructorDAO;
    private BarcoDAO barcoDAO;
    private ClienteDAO clienteDAO;
    private ReservaDAO reservaDAO;
    Usuario usuario;
    private static final Logger log = LogManager.getLogger(CrearReservasControlador.class);
    public void initialize(){

        /*
        db = new DatabaseConnection();
        instructorDAO = new InstructorDAOImpl(db);
        barcoDAO = new BarcoDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);
        reservaDAO = new ReservaDAOImpl(db)
         */
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();

        cbTipo.getItems().addAll("BARCO", "COSTA");
        cbEspecialidad.getItems().setAll(Especialidad.values());
        for(int i = 9; i <= 20; i++){
            cbHora.getItems().add(LocalTime.of(i,0));
        }
        //Hasta que no elija el tipo, no puede poner un barco
        cbBarco.setDisable(true);

        //listeners
        //cada vez que cambien los valores de los elementos, se ejecutan los metodos
        cbTipo.setOnAction(event -> cambioTipo());
        dpFecha.setOnAction(event -> cargarDisponibles());
        cbHora.setOnAction(event -> cargarDisponibles());

        //para que puedan buscar los clientes por nombre, en principio los añadimos todos
        cbClientes.setEditable(true);
        //List<Cliente> listaClientes = clienteDAO.obtenerTodos();
        // cbClientes.getItems().setAll(listaClientes);

        //ponemos un listener para que al buscar un cliente aparezcan solo los que tienen ese nombre
        cbClientes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            buscadorClientes(newValue);
        });

    }
    @FXML
    public void addCliente(ActionEvent actionEvent) {
        //Obtenemos el cliente seleccionado del combo box y lo añadimos a la lista de clientes de la reserva
        Cliente clienteSeleccionado = cbClientes.getValue();

        if(clienteSeleccionado != null){
            //comprobamos que el cliente no estuviese ya registrado en la reserva
            if(!lvClientes.getItems().contains(clienteSeleccionado)){
                lvClientes.getItems().add(clienteSeleccionado);
            }

        }
    }

    @FXML
    public void crearReserva(ActionEvent actionEvent) {

        log.info("Creando una reserva nueva");
        Reserva reserva = new Reserva();

        reserva.setFecha(dpFecha.getValue());
        reserva.setHora(cbHora.getValue());
        reserva.setInstructor(cbInstructor.getValue());
        reserva.setClientes(new ArrayList<>(lvClientes.getItems()));

        //la inmersion depende del tipo
        Inmersion inmersion;
        //si es de barco
        if(cbTipo.getValue().equals("BARCO")){
                log.info("La reserva es de tipo barco");
                InmersionBarco inmersionBarco = new InmersionBarco();
                inmersionBarco.setBarco(cbBarco.getValue());
                inmersionBarco.setPrecio(Double.parseDouble(tfPrecio.getText()));
                inmersionBarco.setPlazasMax(Integer.parseInt(tfPlazas.getText()));
                inmersionBarco.setNombre(cbEspecialidad.getValue().toString());
                inmersion = inmersionBarco;
        }else{ //si es de costa
            log.info("La reserva es de tipo costa");
            InmersionCosta inmersionCosta = new InmersionCosta();
            inmersionCosta.setPrecio(Double.parseDouble(tfPrecio.getText()));
            inmersionCosta.setPlazasMax(Integer.parseInt(tfPlazas.getText()));
            inmersionCosta.setNombre(cbEspecialidad.getValue().toString());
            inmersionCosta.setLugar(txtLugar.getText());
            inmersion = inmersionCosta;
        }
        reserva.setInmersion(inmersion);
        //reservaDAO.crearReserva(reserva);
        log.info("Reserva creada");

    }

    //Metodo para mostrar los instructores y los barcos disponibles una vez haya seleccionado la fecha y hora
    public void cargarDisponibles(){
        //Mientras no haya seleccionado la fecha y la hora no devuelve nada
        if(dpFecha.getValue() == null || cbHora.getValue() == null){
            return;
        }
        cbInstructor.getItems().clear();
        cbBarco.getItems().clear();
/*CUANDO ESTE EL BACK
        //lista en la que se guardan todos los instructores disponibles para esa fecha y hora
        List<Instructor> instructoresDisponibles = instructorDAO.devolverInstructorDisponible(dpFecha.getValue(), cbHora.getValue());

        //los ponemos en el combo box
        cbInstructor.getItems().setAll(instructoresDisponibles);

        //Si el tipo de reserva es barco buscamos los dispobnibles
        if(cbTipo.getValue().equals("BARCO")){

            List<Barco> barcosDisponibles = barcoDAO.devolverBarcoDisponible(dpFecha.getValue(), cbHora.getValue());

            cbBarco.getItems().setAll(barcosDisponibles);
        }


 */
    }

    //cada vez que se cambia el tipo de reserva se llama a este metodo
    public void cambioTipo(){
        String tipo = cbTipo.getValue();
        log.info("Se ha cambiado el tipo de reserva al crearla");
        if(tipo.equals("BARCO")){
            cbBarco.setDisable(false);
            txtLugar.setDisable(true);
        }else{
            txtLugar.setDisable(false);
            cbBarco.setDisable(true);
            cbBarco.getSelectionModel().clearSelection();
        }
    }

    //metodo para buscar clientes por su nombre, se le llama cada vez que se modifica el valor del combo box:
    public void buscadorClientes(String texto) {

        //Si no ha escrito nada devolvemos todos los clientes
        if (texto == null || texto.isBlank()) {
            cbClientes.getItems().clear();
            //List<Cliente> listaClientes = clienteDAO.obtenerTodos();
            // cbClientes.getItems().setAll(listaClientes);
            return;
        }
        //Buscamos clientes por nombre
        // List<Cliente> clientes = clienteDAO.buscarPorNombre(texto);
        //Actualizamos el combo box
       // cbClientes.getItems().setAll(clientes);
    }

    @FXML
    public void irCrearCliente(ActionEvent actionEvent) {
        //se abre en una nueva ventana la vista para crear un cliente, no se cierra la que tenemos
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearCliente.fxml"));
            Parent root = vista.load();
            Scene scene = new Scene(root, 640, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Nuevo Cliente");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.err.println("ERROR. Fallo al cargar la vista crear cliente");
            log.warn("Error al cargar la vista para crear un nuevo cliente desde crear reservas");
        }
    }
}
