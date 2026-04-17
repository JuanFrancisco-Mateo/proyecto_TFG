package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.model.Barco;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.model.Instructor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalTime;

public class CrearReservas {
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
    private ComboBox cbTipo;
    @FXML
    private TextField tfPrecio;
    @FXML
    private ComboBox<Especialidad> cbEspecialidad;

    public void initialize(){

        cbTipo.getItems().addAll("BARCO", "COSTA");
        cbEspecialidad.getItems().setAll(Especialidad.values());
    }
    @FXML
    public void addCliente(ActionEvent actionEvent) {
        //abre una ventana a parte, no cierra la de reservas para que no se borre lo que tenemos
    }

    @FXML
    public void crearReserva(ActionEvent actionEvent) {
    }

}
