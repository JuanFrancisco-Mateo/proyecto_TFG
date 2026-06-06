package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.InstructorDAO;
import es.cifpcarlos3.proyecto.dao.impl.InstructorDAOImpl;
import es.cifpcarlos3.proyecto.model.*;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class InstructorControlador {
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private ComboBox<Certificacion> cbCertificacion;
    @javafx.fxml.FXML
    private TextField txtApellidos;
    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private Button btnDeleteInstructor;
    @javafx.fxml.FXML
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private Button btnModifInstructor;
    @javafx.fxml.FXML
    private ListView<Especialidad> lvEspecialidades;
    @javafx.fxml.FXML
    private Button btnAddEspecialidad;
    @javafx.fxml.FXML
    private ComboBox<Especialidad> cbEspecialidad;
    @javafx.fxml.FXML
    private Button btnDeleteEspecialidad;
    @javafx.fxml.FXML
    private TextField txtTlfEMergencia;

    private Instructor instructor;
    private InstructorDAO instructorDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(InstructorControlador.class);


    public void initialize(){
        db = new DatabaseConnection();
        instructorDAO = new InstructorDAOImpl(db);

        cbCertificacion.getItems().setAll();
        //En este no se bloquean los inputs, ya que el administrador si puede cambiarlos
    }

    public void setInstructor(Instructor instructor){
        this.instructor = instructor;

        txtNombre.setText(instructor.getNombre());
        txtApellidos.setText(instructor.getApellidos());
        cbCertificacion.setValue(instructor.getCertificacion());
        txtDni.setText(instructor.getDni());
        txtEmail.setText(instructor.getEmail());
        txtTlf.setText(instructor.getTelefono());
        txtFecha.setValue(instructor.getFechaNacimiento());
        txtTlfEMergencia.setText(instructor.getTelefonoUrgencia());
    }

    @FXML
    public void modifInstructor(ActionEvent actionEvent) {
        if(txtNombre.getText().isBlank() ||
                txtApellidos.getText().isBlank() ||
                txtEmail.getText().isBlank() ||
                txtTlf.getText().isBlank() ||
                txtTlfEMergencia.getText().isBlank()){

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("Debe completar todos los campos");
            alerta.showAndWait();

            log.warn("Intento de modificar instructor con campos vacíos");
            return;
        }

        try{
            instructor.setNombre(txtNombre.getText());
            instructor.setApellidos(txtApellidos.getText());
            instructor.setEmail(txtEmail.getText());
            instructor.setTelefono(txtTlf.getText());
            instructor.setTelefonoUrgencia(txtTlfEMergencia.getText() );
            instructor.setFechaNacimiento(txtFecha.getValue());
            instructor.setEspecialidades(lvEspecialidades.getItems());

            instructorDAO.modifInstructor(instructor);
            log.info("Instructor modificado correctamente");

        }catch(NumberFormatException e){

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Formato incorrecto");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("El teléfono debe ser numérico");
            alerta.showAndWait();

            log.warn("Error al modificar instructor: teléfono incorrecto");
        }
    }

    @javafx.fxml.FXML
    public void deleteInstructor(ActionEvent actionEvent) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar Instructor");
        alerta.setHeaderText("Confirmación");
        alerta.setContentText("¿Está seguro de que quiere eliminar al instructor?");

        //Para coger lo seleccionado por el usuario
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            instructorDAO.eliminarInstructor(instructor.getIdInstructor());
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("Instructor eliminado correctamente");
            ok.showAndWait();
            //se cierra la ventana
            btnDeleteInstructor.getScene().getWindow().hide();
        }else{
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("No se pudo eliminar el instructor");
            ok.showAndWait();
        }
    }

    @javafx.fxml.FXML
    public void deleteEspecialidad(ActionEvent actionEvent) {
        Especialidad especialidad = lvEspecialidades.getSelectionModel().getSelectedItem();
        int idEsp = instructorDAO.getIdEspecialidadPorNombre(especialidad.name());
        instructorDAO.removeEspecialidad(instructor.getIdInstructor(), idEsp);

    }

    @javafx.fxml.FXML
    public void addEspecialidad(ActionEvent actionEvent) {
        Especialidad especialidad = cbEspecialidad.getValue();
        if(especialidad != null){
            //Para que no haya duplicados
            if(!lvEspecialidades.getItems().contains(especialidad)){
                lvEspecialidades.getItems().add(especialidad);
                int idEsp = instructorDAO.getIdEspecialidadPorNombre(especialidad.name());
                instructorDAO.addEspecialidad(instructor.getIdInstructor(), idEsp);
                log.info("Se ha añadido una nueva especialidad a la lista del instructor");
            }
        }
    }
}
