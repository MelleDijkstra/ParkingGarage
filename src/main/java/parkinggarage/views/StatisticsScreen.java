package parkinggarage.views;

import parkinggarage.Simulation;
import parkinggarage.controllers.StatisticsController;

import java.io.IOException;

/**
 * Created by jandu on 26/01/2017.
 */

/**
 * Loads the statisticsScreen
 */
public class StatisticsScreen extends BaseScreen {

    /**
     * Model for the statistics screen
     */
    Simulation simulation;

    public StatisticsScreen(Simulation simulation) throws IOException {
        super();
        this.simulation = simulation;
        // TODO: view should not be aware of controller, other way around (JavaFX struggles)
        ((StatisticsController)this.controller).setSimulation(simulation);
        ((StatisticsController)this.controller).update();
    }

    @Override
    protected boolean includeStyling() {
        return false;
    }

    @Override
    public String getLayoutFile() {
        return "StatisticsScreen.fxml";
    }

    @Override
    public String getTitle() { return "Statistics"; }

    public void updateView() {
        // TODO: view should not update controller
        ((StatisticsController)controller).update();
    }

    @Override
    public int getWidth() {
        return 1000;
    }

    @Override
    public int getHeight() {
        return 500;
    }
}
