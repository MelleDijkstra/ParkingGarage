package parkinggarage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by melle on 10-1-2017.
 */
public class Main extends Application {
    public static void main(String args[]) {
        // Launches JavaFX initialization
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("layouts/MainScreen.fxml"));
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

}
