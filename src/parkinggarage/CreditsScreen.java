package parkinggarage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Silvan on 19-1-2017.
 */
public class CreditsScreen {

    private Stage mainStage;

    public CreditsScreen() throws IOException {
        mainStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("layouts/CreditsScreen.fxml"));
        mainStage.setTitle("Credits");

        Scene mainScene = new Scene(root, 500, 500);
        mainScene.getStylesheets().add(getClass().getResource("resources/css/mainScreenStyle.css").toString());

        mainStage.setScene(mainScene);
        mainStage.show();
    }

}
