package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import parkinggarage.views.SettingsScreen;
import parkinggarage.Simulation;
import parkinggarage.views.CreditsScreen;

import java.io.IOException;

/**
 * Created by jandu on 12/01/2017.
 */
public class MainController {

    private Simulation simulation;

    @FXML
    private TextField tfIterationCount;

    @FXML
    private Button btnSimulate;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnCredits;

    @FXML
    public void onBtnSimulateClick(ActionEvent actionEvent) {
        int iterations = Integer.parseInt(tfIterationCount.getText());
        // Simulation should run at least 1 time!
        if(iterations >= 1) {
            System.out.println("Iterations Specified: "+iterations);
            new Thread(() -> {
                if (simulation == null) {
                    simulation = new Simulation(iterations);
                    simulation.run();
                }
            }).start();
        } else {
            new Alert(Alert.AlertType.WARNING, "Please specify a higher number").show();
        }
    }

    @FXML
    public void onBtnSettingsClick(ActionEvent actionEvent) {
        try {
            SettingsScreen settingsScreen = new SettingsScreen();
            settingsScreen.show();
        } catch (IOException e) {
            System.out.println("Settings file not found");
            e.printStackTrace();
        }
    }

    @FXML
    public void onBtnCreditsClick(ActionEvent actionEvent) {
        try {
            CreditsScreen creditsScreen = new CreditsScreen();
            creditsScreen.show();
        } catch (NullPointerException e) {
            System.out.println("Credits file not found");
            new Alert(Alert.AlertType.ERROR, "Layout file not found").show();
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something went wrong");
            new Alert(Alert.AlertType.ERROR, "FXML not valid").show();
            e.printStackTrace();
        }
    }
}
