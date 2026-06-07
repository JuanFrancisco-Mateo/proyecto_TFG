package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.dao.InmersionesDAO;
import es.cifpcarlos3.proyecto.dao.ReservaDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.dao.impl.InmersionesDAOImpl;
import es.cifpcarlos3.proyecto.dao.impl.ReservaDAOImpl;
import es.cifpcarlos3.proyecto.model.*;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservaControlador {
    @javafx.fxml.FXML
    private ComboBox<Cliente> cbClientes;
    @javafx.fxml.FXML
    private ComboBox<LocalTime> cbHora;
    @javafx.fxml.FXML
    private ListView<Cliente> lvClientes;
    @javafx.fxml.FXML
    private TextField tfPrecio;
    @javafx.fxml.FXML
    private DatePicker dpFecha;
    @javafx.fxml.FXML
    private ComboBox<Instructor> cbInstructor;
    @javafx.fxml.FXML
    private ComboBox<Especialidad> cbEspecialidad;
    @javafx.fxml.FXML
    private ComboBox<String> cbTipo;
    @javafx.fxml.FXML
    private ComboBox<Barco> cbBarco;
    @javafx.fxml.FXML
    private Button btnAddCliente;
    @javafx.fxml.FXML
    private TextField txtLugar;
    @javafx.fxml.FXML
    private Button btnCrearCliente;
    @javafx.fxml.FXML
    private TextField tfPlazas;
    @javafx.fxml.FXML
    private Button btnRemoveCliente;
    @javafx.fxml.FXML
    private Button btnModificar;

    Reserva reserva;
    private ReservaDAO reservaDAO;
    private ClienteDAO clienteDAO;
    private InmersionesDAO inmersionesDAO;
    Usuario usuario;
    private static final Logger log = LogManager.getLogger(ReservaControlador.class);



    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();
        inmersionesDAO = new InmersionesDAOImpl(db);
        reservaDAO = new ReservaDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);

        //Solo se puede modificar la hora, fecha, instructor, barco, lugar y clientes
        cbTipo.setDisable(true);
        tfPlazas.setDisable(true);
        tfPrecio.setDisable(true);

        //para que puedan buscar los clientes por nombre, en principio los añadimos todos
        cbClientes.setEditable(true);
        List<Cliente> listaClientes = clienteDAO.listarClientes();
        cbClientes.getItems().setAll(listaClientes);
        //ponemos un listener para que al buscar un cliente aparezcan solo los que tienen ese nombre
        cbClientes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> buscadorClientes(newValue));


    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        dpFecha.setValue(reserva.getFecha());
        cbHora.setValue(reserva.getHora());
        cbInstructor.setValue(reserva.getInstructor());
        lvClientes.getItems().setAll(reservaDAO.getClientes(reserva.getId()));
        cbTipo.setValue(reserva.getInmersion().getTipo());
        tfPrecio.setText(String.valueOf(reserva.getInmersion().getPrecio()));
        tfPlazas.setText(String.valueOf(reserva.getInmersion().getPlazasMax()));
        if(reserva.getInmersion().getTipo().equalsIgnoreCase("Costa")){
            txtLugar.setText(reserva.getLugar());
        }else if(reserva.getInmersion().getTipo().equalsIgnoreCase("Barco")){
            cbBarco.setValue(reserva.getBarco());
        }
    }

    @javafx.fxml.FXML
    public void addCliente(ActionEvent actionEvent) {
        Cliente cliente = cbClientes.getValue();
        if (cliente == null) {
            mostrarError("Error", "Seleccione un cliente del listado");
            return;
        }
        //recuperamos la inmersion de la reserva para comprobar que el cliente tiene la certificacion
        Inmersion inmersion = reserva.getInmersion();
        // Validar certificación mínima
        if (inmersion != null && inmersion.getCertificacionMinima() != null) {
            Certificacion certMin = inmersion.getCertificacionMinima();
            if (!tieneCertificacionSuficiente(cliente, certMin)) {
                mostrarError("Certificación insuficiente",
                        "El cliente necesita certificación " + certMin + " para esta inmersión");
                return;
            }
        }
        //comprobamos que no se dupliquen los clientes
        if(reserva.getClientes().contains(cliente)){
            mostrarError("Cliente duplicado", "El cliente ya está añadido a la reserva");
            return;
        }
        //Añadimos el cliente a la reserva
        reserva.getClientes().add(cliente);
        lvClientes.getItems().setAll(reserva.getClientes());
        // Limpiar selección
        cbClientes.getSelectionModel().clearSelection();
        cbClientes.getEditor().clear();

    }
    private boolean tieneCertificacionSuficiente(Cliente cliente, Certificacion certMin) {
        if (cliente.getCertificacion() == null) return false;
        // Orden: SCUBA < OWD < AOWD < RESCUE < MASTERSCUBA < DIVEMASTER < ASSISTANT < OWSI < MASTERSCUBATAINER
        return cliente.getCertificacion().ordinal() >= certMin.ordinal();
    }
    @javafx.fxml.FXML
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

    @javafx.fxml.FXML
    public void removeCliente(ActionEvent actionEvent) {
        Cliente cliente = lvClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarError("Error", "Selecciona un cliente");
            return;
        }

        reservaDAO.removeCliente(reserva.getId(), cliente.getIdCliente());
        lvClientes.getItems().remove(cliente);
    }
    public void buscadorClientes(String texto) {

        //Si no ha escrito nada devolvemos todos los clientes
        if (texto == null || texto.isBlank()) {
            cbClientes.getItems().clear();
            List<Cliente> listaClientes = clienteDAO.listarClientes();
            cbClientes.getItems().setAll(listaClientes);
            return;
        }
        //se puede buscar por nombre o por dni
        List<Cliente> clientesNombre = clienteDAO.buscarPorNombre(texto);
        List<Cliente> clientesDni = clienteDAO.buscarPorDni(texto);

        Set<Cliente> resultado = new HashSet<>();
        resultado.addAll(clientesNombre);
        resultado.addAll(clientesDni);

        cbClientes.getItems().setAll(resultado);

    }

    @javafx.fxml.FXML
    public void modifReserva(ActionEvent actionEvent) {
        if (reserva == null) return;

        reserva.setFecha(dpFecha.getValue());
        reserva.setHora(cbHora.getValue());
        reserva.setInstructor(cbInstructor.getValue());

        reservaDAO.modifReserva(reserva);

    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("ERROR");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
