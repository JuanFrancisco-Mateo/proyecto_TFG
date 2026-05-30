module es.cifpcarlos3.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.calendarfx.view;
    requires lombok;
    requires java.sql;
    requires jbcrypt;
    requires org.apache.logging.log4j;


    opens es.cifpcarlos3.proyecto to javafx.fxml;
    opens es.cifpcarlos3.proyecto.model to javafx.base;
    exports es.cifpcarlos3.proyecto;
    exports es.cifpcarlos3.proyecto.Controladores;
    opens es.cifpcarlos3.proyecto.Controladores to javafx.fxml;
}