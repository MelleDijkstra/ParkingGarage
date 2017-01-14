package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import parkinggarage.Simulation;

/**
 * Created by jandu on 12/01/2017.
 */
public class MainController {

    Simulation simulation;

    @FXML
    private Button btnSimulate;

    @FXML
    public void onBtnSimulateClick(ActionEvent actionEvent) {
        new Thread(() -> {
            if(simulation == null) {
                simulation = new Simulation();
                simulation.run();
            }
        }).start();
    }
}
