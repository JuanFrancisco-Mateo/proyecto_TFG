package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.model.Especialidad;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientesControlador {

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
    private AnchorPane clientesContenido;
    @javafx.fxml.FXML
    private TextField txtDni;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private DatePicker txtFechaValidez;
    @javafx.fxml.FXML
    private TextField txtCertificado;
    @javafx.fxml.FXML
    private RadioButton btnPresentado;
    @javafx.fxml.FXML
    private DatePicker txtFecha;
    @javafx.fxml.FXML
    private TextField txtTlf;
    @javafx.fxml.FXML
    private DatePicker txtFechaCertificado;
    @javafx.fxml.FXML
    private Button btnModificarCliente;
    @javafx.fxml.FXML
    private ComboBox<Especialidad> cbEspecialidad;
    @javafx.fxml.FXML
    private TextField txtTlfEMergencia;

    Cliente cliente;
    private ClienteDAO clienteDAO;
    private DatabaseConnection db;
    private static final Logger log = LogManager.getLogger(ClientesControlador.class);

    public void initialize(){
        db = new DatabaseConnection();
        //clienteDAO = new ClienteDAOImpl(db);
        cbEspecialidad.getItems().setAll(Especialidad.values());

    }

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
        //Ponemos todos los datos del cliente en los textfields
        txtApellidos.setText(cliente.getApellidos());
        txtCertificado.setText(String.valueOf(cliente.getCertificacion()));
        txtDni.setText(cliente.getDni());
        txtEmail.setText(cliente.getEmail());
        txtNombre.setText(cliente.getNombre());
        txtTlf.setText(String.valueOf(cliente.getTelefono()));
        txtFecha.setValue(cliente.getFechaNacimiento());
        txtSeguro.setText(cliente.getNumeroSeguro());
        txtTlfEMergencia.setText(cliente.getTelefonoUrgencia());
        lvEspecialidades.getItems().setAll(cliente.getEspecialidades());

    }

    @javafx.fxml.FXML
    public void addEspecialidad(ActionEvent actionEvent) {
        Especialidad especialidad = cbEspecialidad.getValue();
        if(especialidad != null){
            //Para que no haya duplicados
            if(!lvEspecialidades.getItems().contains(especialidad)){
                lvEspecialidades.getItems().add(especialidad);
                //clienteDAO.addEspecialidad(cliente.getIdCliente(), especialidad);
                log.info("Se ha añadido una nueva especialidad a la lista del cliente");
            }
        }
    }

    @javafx.fxml.FXML
    public void modificarCliente(ActionEvent actionEvent) {
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

                log.warn("Intento de modificar cliente con campos vacíos");
                return;
            }

            try{
                cliente.setNombre(txtNombre.getText());
                cliente.setApellidos(txtApellidos.getText());
                cliente.setEmail(txtEmail.getText());
                cliente.setTelefono(Integer.parseInt(txtTlf.getText()));
                cliente.setTelefonoUrgencia(txtTlfEMergencia.getText() );
                cliente.setNumeroSeguro(txtSeguro.getText());
                cliente.setFechaNacimiento(txtFecha.getValue());
                cliente.setFechaExp(txtFechaCertificado.getValue());
                cliente.setSeguroHasta(txtFechaValidez.getValue());
                cliente.setEspecialidades(lvEspecialidades.getItems());

                // clienteDAO.modificarCliente(cliente);
                log.info("Cliente modificado correctamente");

            }catch(NumberFormatException e){

                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText("ERROR");
                alerta.setContentText("El teléfono debe ser numérico");
                alerta.showAndWait();

                log.warn("Error al modificar cliente: teléfono incorrecto");
            }

    }
}
