package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.*;
import es.cifpcarlos3.proyecto.dao.impl.*;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrearReservasControlador {
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<LocalTime> cbHora;
    @FXML
    private ComboBox<Barco> cbBarco;
    @FXML
    private Button btnAddCliente;
    @FXML
    private ListView<Cliente> lvClientes;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<Instructor> cbInstructor;
    @FXML
    private Button btnCrear;
    @FXML
    private TextField tfPlazas;
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

    private ReservaDAO reservaDAO;
    private InmersionesDAO inmersionesDAO;
    private InstructorDAO instructorDAO;
    private ClienteDAO clienteDAO;
    private BarcoDAO barcoDAO;
    private List<Cliente> clientesSeleccionados;
    private Inmersion inmersionSeleccionada;

    Usuario usuario;
    private static final Logger log = LogManager.getLogger(CrearReservasControlador.class);


    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();
        reservaDAO = new ReservaDAOImpl(db);
        inmersionesDAO = new InmersionesDAOImpl(db);
        instructorDAO = new InstructorDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);
        barcoDAO = new BarcoDAOImpl(db);
        clientesSeleccionados = new ArrayList<>();

        // Cargar tipos de inmersión
        cbTipo.getItems().addAll("BARCO", "COSTA");
        cbEspecialidad.getItems().setAll(Especialidad.values());

        // Cargar horas disponibles (de 8:00 a 18:00 en intervalos de 1 hora)
        for (int h = 8; h <= 18; h++) {
            cbHora.getItems().add(LocalTime.of(h, 0));
        }

        // Listeners para actualizar instructores disponibles
        dpFecha.valueProperty().addListener((obs, old, newVal) -> actualizarInstructoresYBarco());
        cbHora.valueProperty().addListener((obs, old, newVal) -> actualizarInstructoresYBarco());
        cbTipo.valueProperty().addListener((obs, old, newVal) -> onTipoChanged());

        //para que puedan buscar los clientes por nombre, en principio los añadimos todos
        cbClientes.setEditable(true);
        List<Cliente> listaClientes = clienteDAO.listarClientes();
        cbClientes.getItems().setAll(listaClientes);
        //ponemos un listener para que al buscar un cliente aparezcan solo los que tienen ese nombre
        cbClientes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            buscadorClientes(newValue);
        });
    }

    private void onTipoChanged() {
        String tipo = cbTipo.getValue();
        // Mostrar/ocultar barco según tipo
        cbBarco.setDisable(!"BARCO".equals(tipo));
        if (!"BARCO".equals(tipo)) {
            cbBarco.getSelectionModel().clearSelection();
            cbBarco.setValue(null);
        }

        inmersionSeleccionada = null;

        tfPlazas.clear();
        tfPrecio.clear();
        // Según tipo, cargar inmersiones disponibles y actualizar precio/plazas
        List<Inmersion> inmersiones = inmersionesDAO.listarInmersiones();
        for (Inmersion i : inmersiones) {
            if (i.getTipo().equals(tipo)) {
                // Usamos la primera inmersión de ese tipo como referencia
                inmersionSeleccionada = i;
                tfPlazas.setText(String.valueOf(i.getPlazasMax()));
                tfPrecio.setText(String.format("%.2f", i.getPrecio()));
                break;
            }
        }
    }

    private void actualizarInstructoresYBarco() {
        if (dpFecha.getValue() != null && cbHora.getValue() != null) {
            LocalDate fecha = dpFecha.getValue();
            LocalTime hora = cbHora.getValue();

            // Instructores disponibles
            List<Instructor> disponibles = instructorDAO.buscarDisponibles(fecha, hora);
            cbInstructor.getItems().setAll(disponibles);

            // Barco disponible (solo si es tipo BARCO)
            if ("BARCO".equals(cbTipo.getValue())) {
                Barco barco = barcoDAO.devolverBarcoDisponible(fecha, hora);
                if (barco != null) {
                    cbBarco.getItems().clear();
                    cbBarco.getItems().add(barco);
                    cbBarco.getSelectionModel().select(0);
                } else {
                    cbBarco.getItems().clear();
                }
            }
        }
    }

    @FXML
    public void addCliente(ActionEvent actionEvent) {

        Cliente cliente = cbClientes.getValue();
            if (cliente == null) {
                mostrarError("Error", "Seleccione un cliente del listado");
                return;
            }

            // Validar certificación mínima
            if (inmersionSeleccionada != null && inmersionSeleccionada.getCertificacionMinima() != null) {
                Certificacion certMin = inmersionSeleccionada.getCertificacionMinima();
                if (!tieneCertificacionSuficiente(cliente, certMin)) {
                    mostrarError("Certificación insuficiente",
                            "El cliente necesita certificación " + certMin + " para esta inmersión");
                    return;
                }
            }

            // Comprobar que no esté ya añadido
            if (clientesSeleccionados.contains(cliente)) {
                mostrarError("Cliente duplicado", "El cliente ya está añadido a la reserva");
                return;
            }

            clientesSeleccionados.add(cliente);
            lvClientes.getItems().setAll(clientesSeleccionados);
        // Limpiar selección
        cbClientes.getSelectionModel().clearSelection();
        cbClientes.getEditor().clear();
        }


    private boolean tieneCertificacionSuficiente(Cliente cliente, Certificacion certMin) {
        if (cliente.getCertificacion() == null) return false;
        // Orden: SCUBA < OWD < AOWD < RESCUE < MASTERSCUBA < DIVEMASTER < ASSISTANT < OWSI < MASTERSCUBATAINER
        return cliente.getCertificacion().ordinal() >= certMin.ordinal();
    }

    @FXML
    public void crearReserva(ActionEvent actionEvent) {
        // comprobar que todos los campos estan rellenos
        if (cbTipo.getValue() == null || dpFecha.getValue() == null || cbHora.getValue() == null) {
            mostrarError("Campos incompletos", "Debe seleccionar tipo, fecha y hora");
            return;
        }

        LocalDate fecha = dpFecha.getValue();
        LocalTime hora = cbHora.getValue();

        // Validar fecha pasada
        if (fecha.isBefore(LocalDate.now())) {
            mostrarError("Fecha inválida", "No se pueden crear reservas en fechas pasadas");
            return;
        }

        if (inmersionSeleccionada == null) {
            mostrarError("Inmersión no seleccionada", "Seleccione un tipo de inmersión válido");
            return;
        }

        // comprobar que hay instructor seleccionado y que esta libre
        Instructor instructor = cbInstructor.getValue();
        if (instructor == null) {
            mostrarError("Instructor requerido", "Debe seleccionar un instructor disponible");
            return;
        }

        if (!reservaDAO.isDisponible(fecha, hora, instructor.getIdInstructor())) {
            mostrarError("Instructor no disponible", "El instructor ya tiene una reserva en esa fecha/hora");
            return;
        }

        // comprobar que hay clientes y no se pasa del maximo
        if (clientesSeleccionados.isEmpty()) {
            mostrarError("Sin clientes", "Debe añadir al menos un cliente a la reserva");
            return;
        }

        if (clientesSeleccionados.size() > inmersionSeleccionada.getPlazasMax()) {
            mostrarError("Demasiados clientes",
                    "Máximo " + inmersionSeleccionada.getPlazasMax() + " clientes por reserva");
            return;
        }

        // guardar la reserva en la base de datos
        Reserva reserva = new Reserva();
        reserva.setInmersion(inmersionSeleccionada);
        reserva.setFecha(fecha);
        reserva.setHora(hora);
        reserva.setInstructor(instructor);

        int idReserva = reservaDAO.crearReserva(reserva);
        if (idReserva > 0) {
            // Añadir clientes a la reserva
            for (Cliente c : clientesSeleccionados) {
                reservaDAO.addCliente(idReserva, c.getIdCliente());
            }

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Reserva creada");
            alerta.setHeaderText("Éxito");
            alerta.setContentText("Reserva creada correctamente");
            alerta.showAndWait();
        } else {
            mostrarError("Error al crear reserva", "No se pudo crear la reserva");
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("ERROR");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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
    //metodo para buscar clientes por su nombre, se le llama cada vez que se modifica el valor del combo box:
    public void buscadorClientes(String texto) {

        //Si no ha escrito nada devolvemos todos los clientes
        if (texto == null || texto.isBlank()) {
            cbClientes.getItems().clear();
            List<Cliente> listaClientes = clienteDAO.listarClientes();
            cbClientes.getItems().setAll(listaClientes);
            return;
        }
        //Buscamos clientes por nombre
         List<Cliente> clientes = clienteDAO.buscarPorNombre(texto);
        //tambien se puede buscar por dni
        //List<Cliente> cliente = clienteDAO.buscarPorDni(dni);
        //Actualizamos el combo box
        cbClientes.getItems().setAll(clientes);
    }
}