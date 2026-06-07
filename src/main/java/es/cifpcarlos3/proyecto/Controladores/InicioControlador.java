package es.cifpcarlos3.proyecto.Controladores;

import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import es.cifpcarlos3.proyecto.HelloApplication;
import es.cifpcarlos3.proyecto.dao.ReservaDAO;
import es.cifpcarlos3.proyecto.dao.impl.ReservaDAOImpl;
import es.cifpcarlos3.proyecto.model.Reserva;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioControlador {

    @javafx.fxml.FXML
    private AnchorPane contenido;
    @javafx.fxml.FXML
    private CalendarView calendario;

    private ReservaDAO reservaDAO;
    public void initialize(){
        calendario.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());

        DatabaseConnection db = new DatabaseConnection();
        reservaDAO = new ReservaDAOImpl(db);

        cargarReservas();

        calendario.setEntryDetailsCallback(param -> {
            Reserva reserva = (Reserva) param.getEntry().getUserObject();
            abrirReserva(reserva);
            return null;
        });
    }
    //Cargar en el calendario todas las reservas de la base de adtos
    private void cargarReservas() {

        calendario.getCalendarSources().clear();

        Calendar calendar = new Calendar("Reservas");

        for (Reserva reserva : reservaDAO.listarReservas()) {
            Entry<Reserva> entry = new Entry<>();
            entry.setTitle(reserva.getInmersion().getNombre());

            entry.changeStartDate(reserva.getFecha());
            entry.changeEndDate(reserva.getFecha());

            entry.changeStartTime(reserva.getHora());
            entry.changeEndTime(reserva.getHora().plusHours(1));

            entry.setUserObject(reserva);
            calendar.addEntry(entry);
        }

        CalendarSource source = new CalendarSource("Centro");
        source.getCalendars().add(calendar);
        calendario.getCalendarSources().add(source);
    }

    //Ver la ficha de reserva al selecionarla
    private void abrirReserva(Reserva reserva) {

        try {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("reserva.fxml"));
            Parent root = vista.load();
            ReservaControlador controlador = vista.getController();
            controlador.setReserva(reserva);
            Scene scene = new Scene(root, 700, 530);
            scene.getStylesheets().add(getClass().getResource("/es/cifpcarlos3/proyecto/stylesPantallas.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Datos de la reserva");
            stage.setScene(scene);
            stage.show();

            stage.setOnHidden(e -> cargarReservas());

        } catch (Exception e) {
            System.err.println("ERROR. Fallo al cargar la ficha de la reserva");
        }
    }

}
