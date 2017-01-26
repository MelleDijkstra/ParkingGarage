package parkinggarage.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by melle on 10-1-2017.
 */
public class MainScreen extends Application {

    Scene mainScene;

    public static void main(String args[]) {
        // Launches JavaFX initialization
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../layouts/MainScreen.fxml"));
        primaryStage.setTitle("Dashboard");

        // TODO: add icon for taskbar
        //primaryStage.getIcons().add(new Image(""));

        mainScene = new Scene(root, 850, 650);
        mainScene.getStylesheets().add(getClass().getResource("../resources/css/style.css").toString());

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

}
