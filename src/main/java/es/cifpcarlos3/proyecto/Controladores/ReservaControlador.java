package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.*;
import es.cifpcarlos3.proyecto.dao.impl.*;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private BarcoDAO barcoDAO;
    private InstructorDAO instructorDAO;
    private List<Cliente> todosClientes;

    Usuario usuario;
    private static final Logger log = LogManager.getLogger(ReservaControlador.class);
    @javafx.fxml.FXML
    private TextField txtBuscador;
    @javafx.fxml.FXML
    private ComboBox cbInmersion;
    @javafx.fxml.FXML
    private Button btnDeleteReserva;


    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();
        inmersionesDAO = new InmersionesDAOImpl(db);
        reservaDAO = new ReservaDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);
        barcoDAO = new BarcoDAOImpl(db);
        instructorDAO = new InstructorDAOImpl(db);

        //Solo se puede modificar la hora, fecha, instructor, barco, lugar y clientes
        cbTipo.setDisable(true);
        tfPlazas.setDisable(true);
        tfPrecio.setDisable(true);
        cbEspecialidad.setDisable(true);

        // Listeners para actualizar instructores disponibles
        dpFecha.valueProperty().addListener((obs, old, newVal) -> actualizarInstructoresYBarco());
        cbHora.valueProperty().addListener((obs, old, newVal) -> actualizarInstructoresYBarco());

        //para que puedan buscar los clientes por nombre, en principio los añadimos todos
        cbClientes.setEditable(false);
        todosClientes = clienteDAO.listarClientes();
        cbClientes.getItems().setAll(todosClientes);
        //ponemos un listener para que al buscar un cliente aparezcan solo los que tienen ese nombre
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            buscadorClientes(newValue);
        });
        txtLugar.setDisable(true);

    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        dpFecha.setValue(reserva.getFecha());
        cbHora.setValue(reserva.getHora());
        cbInstructor.getItems().setAll(instructorDAO.listarInstructores());
        cbInstructor.setValue(reserva.getInstructor());
        lvClientes.getItems().setAll(reservaDAO.getClientes(reserva.getId()));
        cbTipo.setValue(reserva.getInmersion().getTipo());
        tfPrecio.setText(String.valueOf(reserva.getInmersion().getPrecio()));
        tfPlazas.setText(String.valueOf(reserva.getInmersion().getPlazasMax()));
        if(reserva.getInmersion().getTipo().equalsIgnoreCase("Costa")){
            txtLugar.setText(reserva.getLugar());
        }else if(reserva.getInmersion().getTipo().equalsIgnoreCase("Barco")){
            cbBarco.getItems().setAll(barcoDAO.listarBarcos());
            cbBarco.setValue(reserva.getBarco());
        }
        cbEspecialidad.getItems().setAll(Especialidad.values());

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
                List<Barco> barcos = barcoDAO.devolverBarcoDisponible(fecha, hora);
                if (barcos.isEmpty()) {
                    cbBarco.getItems().clear();
                    cbBarco.getItems().setAll(new Barco(0, "No hay barcos disponibles", 0));
                } else {
                    cbBarco.getItems().clear();
                    cbBarco.getItems().setAll(barcos);
                }
            }
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
            Scene scene = new Scene(root, 700, 530);
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
            cbClientes.getItems().setAll(todosClientes);
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
        if(reserva.getInmersion().getTipo().equalsIgnoreCase("Barco")){
            reserva.setBarco(cbBarco.getValue());
        }

        reservaDAO.modifReserva(reserva);

    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("ERROR");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @javafx.fxml.FXML
    public void deleteReserva(ActionEvent actionEvent) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar reserva");
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Está seguro de que quiere eliminar la reserva?");

        //Para coger lo seleccionado por el usuario
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            reservaDAO.eliminarReserva(reserva.getId());
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("Reserva eliminada correctamente");
            ok.showAndWait();
            //se cierra la ventana
            btnDeleteReserva.getScene().getWindow().hide();
        }else{
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("No se pudo eliminar la reserva");
            ok.showAndWait();
        }
    }
}
