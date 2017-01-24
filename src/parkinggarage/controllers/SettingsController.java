package parkinggarage.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import parkinggarage.SettingsScreen;

import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsController {

    @FXML
    private Button btnSettings;

    @FXML
    public void onBtnSettingsClick(ActionEvent actionEvent) {
        try {
            SettingsScreen settingsScreen = new SettingsScreen();
        } catch (IOException e) {
            System.out.println("Settings file not found");
            e.printStackTrace();
        }
    }

}
