package parkinggarage.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsScreen extends BaseScreen {

    public SettingsScreen() throws IOException {
        super();
    }

    @Override
    public String getLayoutFile() {
        return "SettingsScreen.fxml";
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}

