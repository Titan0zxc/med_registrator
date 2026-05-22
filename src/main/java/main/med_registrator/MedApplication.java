package main.med_registrator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.med_registrator.db.DatabaseManager;

import java.io.IOException;
import java.sql.SQLException;

public class MedApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseManager.getInstance().init();

        FXMLLoader fxmlLoader = new FXMLLoader(
                MedApplication.class.getResource("main-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000);
        stage.setTitle("Цифровой двойник медицинского регистратора");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
