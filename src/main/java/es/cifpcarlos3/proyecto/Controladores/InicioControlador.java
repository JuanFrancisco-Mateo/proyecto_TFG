package es.cifpcarlos3.proyecto.Controladores;

import com.calendarfx.view.CalendarView;
import es.cifpcarlos3.proyecto.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;

import java.io.IOException;

public class InicioControlador {

    @javafx.fxml.FXML
    private AnchorPane contenido;
    @javafx.fxml.FXML
    private CalendarView calendario;

    public void initialize(){
        calendario.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
    }




}
