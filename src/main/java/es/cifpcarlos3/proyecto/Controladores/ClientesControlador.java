package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.ClienteDAO;
import es.cifpcarlos3.proyecto.dao.impl.ClienteDAOImpl;
import es.cifpcarlos3.proyecto.model.Cliente;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ClientesControlador {

    @FXML
    private AnchorPane clientesContenido;

    private ClienteDAO clienteDAO;

    public void initialize(){
        DatabaseConnection db = new DatabaseConnection();
        clienteDAO = new ClienteDAOImpl(db);
    }

    @FXML
    public void crearCliente(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("crearCliente.fxml"));
            Parent root = vista.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Crear Cliente");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(clientesContenido.getScene().getWindow());
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error al cargar crear cliente: " + e.getMessage());
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("ERROR");
            alerta.setContentText("No se pudo abrir el formulario de creación de cliente");
            alerta.showAndWait();
        }
    }
}