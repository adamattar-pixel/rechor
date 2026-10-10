package ch.epfl.rechor.guiTempTest;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class test extends Application {
    public void start(Stage stage) {
        stage.setScene(new Scene(new Label("Hello JavaFX!"), 200, 100));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

