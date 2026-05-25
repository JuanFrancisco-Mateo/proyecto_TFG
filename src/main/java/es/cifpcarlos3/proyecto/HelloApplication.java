package es.cifpcarlos3.proyecto;

import es.cifpcarlos3.proyecto.util.DatabaseConnection;
import es.cifpcarlos3.proyecto.util.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar base de datos
        DatabaseConnection db = new DatabaseConnection();
        DatabaseInitializer init = new DatabaseInitializer(db);
        init.initialize();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CENTRO DE BUCEO");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
