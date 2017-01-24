package parkinggarage.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Silvan on 19-1-2017.
 */
public class CreditsScreen extends BaseScreen {

    public CreditsScreen() throws IOException {
        super();
    }

    @Override
    public String getLayoutFile() {
        return "CreditsScreen.fxml";
    }

    @Override
    public String getTitle() {
        return "Credits";
    }
}
