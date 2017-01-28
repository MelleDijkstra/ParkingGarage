package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import parkinggarage.Simulation;
import parkinggarage.views.CreditsScreen;
import parkinggarage.views.SettingsScreen;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by jandu on 12/01/2017.
 */
public class MainController {

    // Views
    //Nieuwe bitbuckit commit

    private SettingsScreen settingsScreen;
    private CreditsScreen creditsScreen;

    private Simulation simulation;

    @FXML
    private TextField tfIterationCount;

    @FXML
    private Button btnSimulate;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnCredits;

    /**
     * Checking for (and loading) settings file.
     * Sets minimum for iterations.
     * @param actionEvent
     */
    @FXML
    public void onBtnSimulateClick(ActionEvent actionEvent) {
        int iterations = Integer.parseInt(tfIterationCount.getText());
        // Read the settings file to load all settings
        Properties settings = new Properties();
        try {
            settings.load(new FileInputStream(SettingsController.settingsFile));
        } catch (IOException e) {
            System.out.println("Settings file does not exist");
        }
        // Simulation should run at least 1 time!
        // TODO: change to settings.getOrDefault("iterations");
        if(iterations >= 1) {
            System.out.println("Iterations Specified: "+iterations);
            new Thread(() -> {
                if (simulation != null) {
                    simulation.close();
                    // TODO: remove iterations and only give settings (which includes the iterationCount)
                }
                simulation = new Simulation(iterations, settings);
                simulation.run();
            }).start();
        } else {
            new Alert(Alert.AlertType.WARNING, "Please specify a higher number").show();
        }
    }

    @FXML
    public void onBtnSettingsClick(ActionEvent actionEvent) {
        if(settingsScreen == null) {
            try {
                settingsScreen = new SettingsScreen();
                settingsScreen.show();
            } catch (IOException e) {
                System.out.println("Settings file not found");
                e.printStackTrace();
            }
        }
        settingsScreen.show();
    }

    /**
     * Checking for (and loading) credits screen.
     * @param actionEvent
     */
    @FXML
    public void onBtnCreditsClick(ActionEvent actionEvent) {
        if(creditsScreen == null) {
            try {
                creditsScreen = new CreditsScreen();
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
        creditsScreen.show();
    }
}
