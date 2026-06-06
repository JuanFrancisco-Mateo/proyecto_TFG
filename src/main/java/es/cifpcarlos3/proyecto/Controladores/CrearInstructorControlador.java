package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.InstructorDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.dao.impl.InstructorDAOImpl;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.model.Instructor;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

public class CrearInstructorControlador {
    @javafx.fxml.FXML
    private Button btnGuardarInstructor;
    @javafx.fxml.FXML
    private ListView<Especialidad> lvEspecialidades;
    @javafx.fxml.FXML
    private TextField txtApellidos;
    @javafx.fxml.FXML
    private ComboBox<Certificacion> cbCertificado;
    @javafx.fxml.FXML
    private Button btnAddEspecialidad;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private TextField txtTlfEmergencia;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private Button btnDeleteEspecialidad;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private ComboBox<Especialidad> cbEspecialidades;
    @javafx.fxml.FXML
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtTlf;

    private InstructorDAO instructorDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(CrearInstructorControlador.class);

    public void initialize(){
        db = new DatabaseConnection();
        instructorDAO = new InstructorDAOImpl(db);

        cbEspecialidades.getItems().setAll(Especialidad.values());
        cbCertificado.getItems().setAll(Certificacion.values());
        log.info("Se abre la ventana para crear un nuevo instructor");

    }
    @FXML
    public void crearInstructor(ActionEvent actionEvent) {

        if(txtNombre.getText().isEmpty() ||
                txtTlf.getText().isEmpty() ||
                txtApellidos.getText().isEmpty() ||
                txtEmail.getText().isEmpty() ||
                txtDni.getText().isEmpty() ||
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
                Instructor instructor = new Instructor();
                instructor.setNombre(txtNombre.getText());
                instructor.setApellidos(txtApellidos.getText());
                instructor.setDni(txtDni.getText());
                instructor.setEmail(txtEmail.getText());
                instructor.setTelefono(txtTlf.getText());
                instructor.setFechaNacimiento(txtFecha.getValue());
              instructor.setCertificacion(cbCertificado.getValue());
                instructor.setTelefonoUrgencia(txtTlfEmergencia.getText());

                //Especialidades
                instructor.setEspecialidades(
                        new ArrayList<>(lvEspecialidades.getItems())
                );
                try{
                    instructorDAO.crearInstructor(instructor);
                    log.info("Se ha creado un nuevo cliente");

                    //se cierra la ventana
                    btnGuardarInstructor.getScene().getWindow().hide();
                }catch (SQLException e){
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error");
                    alerta.setHeaderText("No se pudo crear el cliente");

                    if (e.getMessage().contains("dni")) {
                        alerta.setContentText("Ya existe un cliente con ese DNI");
                    } else {
                        alerta.setContentText(e.getMessage());
                    }

                    alerta.showAndWait();
                }

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

    @javafx.fxml.FXML
    public void deleteEspecialidad(ActionEvent actionEvent) {
        Especialidad especialidad = lvEspecialidades.getSelectionModel().getSelectedItem();
        if(especialidad != null){
            lvEspecialidades.getItems().remove(especialidad);
        }
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
}
