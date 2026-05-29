package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.*;
import es.cifpcarlos3.proyecto.dao.impl.*;
import es.cifpcarlos3.proyecto.model.*;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrearReservas {
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

    private ReservaDAO reservaDAO;
    private InmersionesDAO inmersionesDAO;
    private InstructorDAO instructorDAO;
    private ClienteDAO clienteDAO;
    private List<Cliente> clientesSeleccionados;
    private Inmersion inmersionSeleccionada;

    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        reservaDAO = new ReservaDAOImpl(db);
        inmersionesDAO = new InmersionesDAOImpl(db);
        instructorDAO = new InstructorDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);
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
    }

    private void onTipoChanged() {
        String tipo = cbTipo.getValue();
        // Mostrar/ocultar barco según tipo
        cbBarco.setDisable(!"BARCO".equals(tipo));
        if (!"BARCO".equals(tipo)) {
            cbBarco.getSelectionModel().clearSelection();
            cbBarco.setValue(null);
        }
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
                // Usamos BarcoDAO
                BarcoDAO barcoDAO = new BarcoDAOImpl(new DatabaseConnection());
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
        // Mostrar diálogo para buscar cliente por DNI
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Añadir Cliente");
        dialog.setHeaderText("Buscar cliente por DNI");
        dialog.setContentText("Introduce el DNI del cliente:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String dni = result.get().trim();
            if (dni.isEmpty()) {
                mostrarError("DNI inválido", "El DNI no puede estar vacío");
                return;
            }

            Cliente cliente = clienteDAO.buscarPorDni(dni);
            if (cliente == null) {
                mostrarError("Cliente no encontrado", "No existe un cliente con DNI: " + dni);
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
            if (clientesSeleccionados.stream().anyMatch(c -> c.getDni().equals(dni))) {
                mostrarError("Cliente duplicado", "El cliente ya está añadido a la reserva");
                return;
            }

            clientesSeleccionados.add(cliente);
            lvClientes.getItems().setAll(clientesSeleccionados);
        }
    }

    private boolean tieneCertificacionSuficiente(Cliente cliente, Certificacion certMin) {
        if (cliente.getCertificacion() == null) return false;
        // Orden: SCUBA < OWD < AOWD < RESCUE < MASTERSCUBA < DIVEMASTER < ASSISTANT < OWSI < MASTERSCUBATAINER
        return cliente.getCertificacion().ordinal() >= certMin.ordinal();
    }

    @FXML
    public void crearReserva(ActionEvent actionEvent) {
        // Validar campos
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

        // Validar instructor
        Instructor instructor = cbInstructor.getValue();
        if (instructor == null) {
            mostrarError("Instructor requerido", "Debe seleccionar un instructor disponible");
            return;
        }

        // Validar disponibilidad del instructor
        if (!reservaDAO.isDisponible(fecha, hora, instructor.getIdInstructor())) {
            mostrarError("Instructor no disponible", "El instructor ya tiene una reserva en esa fecha/hora");
            return;
        }

        // Validar número de clientes vs plazas máximas
        if (clientesSeleccionados.isEmpty()) {
            mostrarError("Sin clientes", "Debe añadir al menos un cliente a la reserva");
            return;
        }

        if (clientesSeleccionados.size() > inmersionSeleccionada.getPlazasMax()) {
            mostrarError("Demasiados clientes",
                    "Máximo " + inmersionSeleccionada.getPlazasMax() + " clientes por reserva");
            return;
        }

        // Crear la reserva
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
}