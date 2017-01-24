package parkinggarage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsScreen {

    private Stage settingsStage;

    public SettingsScreen() throws IOException {
        settingsStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("layouts/SettingsScreen.fxml"));
        settingsStage.setTitle("Settings");

        Scene scene =  new Scene(root, 600 ,600);
        settingsStage.setScene(scene);
    }

    public void show() {
        settingsStage.show();
    }

    public void hide() {
        settingsStage.hide();
    }
}

