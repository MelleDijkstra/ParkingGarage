package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import parkinggarage.SettingsScreen;
import parkinggarage.Simulation;

import java.io.IOException;

/**
 * Created by jandu on 12/01/2017.
 */
public class MainController {

    private Simulation simulation;

    @FXML
    public Spinner spinIterationCount;

    @FXML
    private Button btnSimulate;

    @FXML
    private Button btnSettings;

    @FXML
    public void onBtnSimulateClick(ActionEvent actionEvent) {
        int iterations = Integer.parseInt(spinIterationCount.getValue().toString());
        System.out.println("Simulation iterations: " + iterations);
        new Thread(() -> {
            if (simulation == null) {
                simulation = new Simulation(iterations);
                simulation.run();
            }
        }).start();
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
}
