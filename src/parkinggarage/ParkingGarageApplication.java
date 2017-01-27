package parkinggarage;

import javafx.application.Application;
import javafx.stage.Stage;
import parkinggarage.views.MainScreen;

/**
 * Created by melle on 10-1-2017.
 */
public class ParkingGarageApplication extends Application {

    public static void main(String args[]) {
        // Launches JavaFX initialization
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainScreen mainScreen = new MainScreen();
        mainScreen.show();
    }

}
