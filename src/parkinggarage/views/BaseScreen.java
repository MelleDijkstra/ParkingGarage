package parkinggarage.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import parkinggarage.controllers.BaseController;

import java.io.IOException;

/**
 * Created by melle on 23-1-2017.
 */
public abstract class BaseScreen {

    protected BaseController controller;

    protected Stage mainStage;

    public BaseScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("../layouts/"+getLayoutFile()).openStream());

        controller = fxmlLoader.getController();

        mainStage = new Stage();
        mainStage.setTitle(getTitle());

        Scene mainScene = new Scene(root, getWidth(), getHeight());
        mainScene.getStylesheets().add(getClass().getResource("../resources/css/style.css").toString());
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("../resources/images/car.jpg")));
        mainStage.setScene(mainScene);
    }

    public void show() {
        mainStage.show();
    }

    public void hide() {
        mainStage.hide();
    }

    public void close() { mainStage.close(); }

    public abstract String getLayoutFile();

    public abstract String getTitle();

    public int getWidth() {
        return 500;
    }

    public int getHeight() {
        return 500;
    }
}
