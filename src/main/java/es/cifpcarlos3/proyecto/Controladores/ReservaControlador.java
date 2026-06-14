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
    @javafx.fxml.FXML
    private TextField txtBuscador;
    @javafx.fxml.FXML
    private ComboBox cbInmersion;
    @javafx.fxml.FXML
    private Button btnDeleteReserva;
    @javafx.fxml.FXML
    private TextField txtCertificacionMin;
    @javafx.fxml.FXML
    private Button btnGuardarCambios;


    Reserva reserva;
    Inmersion inmersion;
    private ReservaDAO reservaDAO;
    private ClienteDAO clienteDAO;
    private InmersionesDAO inmersionesDAO;
    private BarcoDAO barcoDAO;
    private InstructorDAO instructorDAO;
    private List<Cliente> todosClientes;

    Usuario usuario;
    private static final Logger log = LogManager.getLogger(ReservaControlador.class);


    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        //recuperamos el usuario que inicio sesion
        usuario = Sesion.recuperarUsuario();
        inmersionesDAO = new InmersionesDAOImpl(db);
        reservaDAO = new ReservaDAOImpl(db);
        clienteDAO = new ClienteDAOImpl(db);
        barcoDAO = new BarcoDAOImpl(db);
        instructorDAO = new InstructorDAOImpl(db);

        //Todos los campos aparecen bloqueados hasta que el usuario le da a modificar reserva
        cbTipo.setDisable(true);
        tfPlazas.setDisable(true);
        tfPrecio.setDisable(true);
        txtLugar.setDisable(true);
        txtCertificacionMin.setDisable(true);
        cbInmersion.setDisable(true);
        cbHora.setDisable(true);
        dpFecha.setDisable(true);
        cbInstructor.setDisable(true);
        cbBarco.setDisable(true);

        //el boton de guardar cambios no aparece hasta clicar el de modificar datos
        btnGuardarCambios.setVisible(false);

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
        //Al seleccionar barco cambia la capacidad maxima de la reserva
        cbBarco.valueProperty().addListener((obs, oldVal, newVal) -> {
            tfPlazas.setText(String.valueOf(calcularCapacidadReserva()));
        });
        // Cargar horas disponibles (de 8:00 a 18:00 en intervalos de 1 hora)
        for (int h = 8; h <= 18; h++) {
            cbHora.getItems().add(LocalTime.of(h, 0));
        }

    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        this.inmersion = reserva.getInmersion();
        dpFecha.setValue(reserva.getFecha());
        cbHora.setValue(reserva.getHora());
        cbInstructor.getItems().setAll(instructorDAO.listarInstructores());
        cbInstructor.setValue(reserva.getInstructor());
        List<Cliente> clientes = reservaDAO.getClientes(reserva.getId());
        reserva.setClientes(clientes);
        lvClientes.getItems().setAll(reservaDAO.getClientes(reserva.getId()));
        cbTipo.setValue(reserva.getInmersion().getTipo());
        tfPrecio.setText(String.valueOf(reserva.getInmersion().getPrecio()));
        tfPlazas.setText(String.valueOf(reserva.getInmersion().getPlazasMax()));
        if(reserva.getInmersion().getTipo().equalsIgnoreCase("Costa")){
            txtLugar.setText(reserva.getInmersion().getLugar());
        }else if(reserva.getInmersion().getTipo().equalsIgnoreCase("Barco")){
            cbBarco.getItems().setAll(barcoDAO.listarBarcos());
            cbBarco.setValue(reserva.getBarco());
        }
        cbInmersion.setValue(inmersion);
        txtCertificacionMin.setText(String.valueOf(reserva.getInmersion().getCertificacionMinima()));

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
        int capacidad = calcularCapacidadReserva();
        if(lvClientes.getItems().size()>=capacidad){
            mostrarError("Plazas completas", "No quedan plazas disponibles para esta reserva");
            return;
        }
        //Añadimos el cliente a la reserva
        reservaDAO.addCliente(reserva.getId(), cliente.getIdCliente());
        reserva.getClientes().add(cliente);
        lvClientes.getItems().setAll(reserva.getClientes());

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
        reserva.getClientes().remove(cliente);
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
        int capacidad = calcularCapacidadReserva();
        if (lvClientes.getItems().size() > capacidad) {
            mostrarError("Capacidad insuficiente", "El barco seleccionado no tiene plazas suficientes para los clientes actuales");
            return;
        }
        if (reserva == null) return;

        reserva.setFecha(dpFecha.getValue());
        reserva.setHora(cbHora.getValue());
        reserva.setInstructor(cbInstructor.getValue());
        if(reserva.getInmersion().getTipo().equalsIgnoreCase("Barco")){
            reserva.setBarco(cbBarco.getValue());

        }

        reservaDAO.modifReserva(reserva);
        //volvemos a deshabilitar los campos y cambiar los botones visibles
        btnGuardarCambios.setVisible(false);
        btnModificar.setVisible(true);
        cbInstructor.setDisable(true);
        dpFecha.setDisable(true);
        cbHora.setDisable(true);
        cbBarco.setDisable(true);

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

    @javafx.fxml.FXML
    public void cambioEditar(ActionEvent actionEvent) {
        //desaparece el boton de editar y aparece el de guardar cambios
        btnModificar.setVisible(false);
        btnGuardarCambios.setVisible(true);
        //Se habilitan los campos que pueden ser modificados
        if(inmersion.getTipo().equalsIgnoreCase("Barco")){
            cbBarco.setDisable(false);
        }
        cbInstructor.setDisable(false);
        dpFecha.setDisable(false);
        cbHora.setDisable(false);
    }
    //metodo para gestionar la capacidad maxima de la reserva
    private int calcularCapacidadReserva(){
        int capacidad=0;
        if(reserva.getInmersion()==null){
            capacidad=0;
        }
        if(cbTipo.getValue().equalsIgnoreCase("Barco")){
            Barco barco=cbBarco.getValue();
            if(barco == null || barco.getIdBarco()==0){
                capacidad= reserva.getInmersion().getPlazasMax();
            }else{
                if(barco.getCapacidad()<reserva.getInmersion().getPlazasMax()) {
                    capacidad = barco.getCapacidad();
                }
            }
        }else if(cbTipo.getValue().equalsIgnoreCase("Costa")){
            capacidad = reserva.getInmersion().getPlazasMax();
        }else{
            capacidad=0;
        }
        return capacidad;
    }
}
