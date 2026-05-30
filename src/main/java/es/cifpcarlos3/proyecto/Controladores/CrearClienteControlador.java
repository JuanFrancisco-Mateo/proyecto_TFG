package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CrearClienteControlador {
    @javafx.fxml.FXML
    private ListView<Especialidad> lvEspecialidades;
    @javafx.fxml.FXML
    private TextField txtApellidos;
    @javafx.fxml.FXML
    private Button btnAddEspecialidad;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private TextField txtSeguro;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private DatePicker txtFechaValidez;
    @javafx.fxml.FXML
    private ComboBox<Certificacion> cbCertificado;
    @javafx.fxml.FXML
    private RadioButton btnPresentado;
    @javafx.fxml.FXML
    private Button btnGuardarCliente;
    @javafx.fxml.FXML
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private DatePicker txtFechaCertificado;
    @javafx.fxml.FXML
    private ComboBox<Especialidad> cbEspecialidades;
    @javafx.fxml.FXML
    private TextField txtTlfEmergencia;

    private ClienteDAO clienteDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(CrearClienteControlador.class);

    public void initialize(){
        db = new DatabaseConnection();
        clienteDAO = new ClienteDAOImpl(db);

        cbEspecialidades.getItems().setAll(Especialidad.values());
        cbCertificado.getItems().setAll(Certificacion.values());
        log.info("Se abre la ventana para crear un nuevo cliente");

    }
    @javafx.fxml.FXML
    public void addEspecialidad(ActionEvent actionEvent) {
        Especialidad especialidad = cbEspecialidades.getValue();
        if(especialidad != null){
            //Para que no haya duplicados
            if(!lvEspecialidades.getItems().contains(especialidad)){
                lvEspecialidades.getItems().add(especialidad);

            }
        }
    }

    @javafx.fxml.FXML
    public void crearCliente(ActionEvent actionEvent) {

        if(txtNombre.getText().isEmpty() ||
                txtTlf.getText().isEmpty() ||
                txtApellidos.getText().isEmpty() ||
                txtEmail.getText().isEmpty() ||
                txtDni.getText().isEmpty() ||
                txtSeguro.getText().isEmpty() ||
                txtTlfEmergencia.getText().isEmpty()

        ){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Faltan datos");
            alerta.showAndWait();
            log.warn("No se puede crear el cliente si no estan todos los datos completos");
        }else{
           try{
               Cliente cliente = new Cliente();
               cliente.setNombre(txtNombre.getText());
               cliente.setApellidos(txtApellidos.getText());
               cliente.setDni(txtDni.getText());
               cliente.setEmail(txtEmail.getText());
               cliente.setTelefono(txtTlf.getText());
               cliente.setNumeroSeguro(txtSeguro.getText());
               cliente.setFechaNacimiento(txtFecha.getValue());
               cliente.setSeguroHasta(txtFechaValidez.getValue());
               cliente.setFechaExp(txtFechaCertificado.getValue());
               cliente.setCertificacion(cbCertificado.getValue());
               cliente.setTelefonoUrgencia(txtTlfEmergencia.getText());

               //Especialidades
               cliente.setEspecialidades(
                       new ArrayList<>(lvEspecialidades.getItems())
               );
               clienteDAO.crearCliente(cliente);
               log.info("Se ha creado un nuevo cliente");
           }catch (NumberFormatException e){
               Alert alerta = new Alert(Alert.AlertType.ERROR);
               alerta.setTitle("Formato incorrecto");
               alerta.setHeaderText("ERROR");
               alerta.setContentText("Formato de teléfono incorrecto");
               alerta.showAndWait();
               log.warn("El formato del número de telefono es incorrecto, no se ha podido crear el cliente");
           }
        }

    }
}
