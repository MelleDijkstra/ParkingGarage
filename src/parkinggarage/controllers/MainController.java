package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import parkinggarage.views.CreditsScreen;
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
    public void onBtnSimulateClick(ActionEvent actionEvent) {
        int iterations = Integer.parseInt(spinIterationCount.getValue().toString());
        System.out.println("Simulation iterations: "+iterations);
        new Thread(() -> {
            if (simulation == null) {
                simulation = new Simulation(iterations);
                simulation.run();
            }
        }).start();
    }

    @FXML
    private Button btnCredits;

    @FXML
    public void onBtnCreditsClick(ActionEvent actionEvent) {
        try {
            CreditsScreen creditsScreen = new CreditsScreen();
            creditsScreen.show();
        } catch (IOException e) {
            System.out.println("Credits file not found");
            e.printStackTrace();
        }
    }
}
