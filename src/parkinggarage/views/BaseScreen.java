package parkinggarage.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by melle on 23-1-2017.
 */
public abstract class BaseScreen {

    protected Stage mainStage;

    public BaseScreen() throws IOException {
        mainStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../layouts/"+getLayoutFile()));
        mainStage.setTitle(getTitle());

        Scene mainScene = new Scene(root, getWidth(), getHeight());
        mainScene.getStylesheets().add(getClass().getResource("../resources/css/style.css").toString());

        mainStage.setScene(mainScene);

        Image icon = new Image(getClass().getResourceAsStream("../resources/images/car.jpg"));
        mainStage.getIcons().add(icon);
    }

    public void show() {
        mainStage.show();
    }

    public void hide() {
        mainStage.hide();
    }

    public abstract String getLayoutFile();

    public abstract String getTitle();

    public int getWidth() {
        return 500;
    }

    public int getHeight() {
        return 500;
    }
}
