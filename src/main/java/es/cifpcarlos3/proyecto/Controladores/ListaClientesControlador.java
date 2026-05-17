package es.cifpcarlos3.proyecto.Controladores;

import es.cifpcarlos3.proyecto.model.Cliente;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class ClientesControlador {

    @javafx.fxml.FXML
    private AnchorPane clientesContenido;
    @javafx.fxml.FXML
    private TableView<Cliente> tableClientes; //cada fila es un cliente

    public void initialize(){
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")
        );
    }


}
