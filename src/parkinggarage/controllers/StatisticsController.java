package parkinggarage.controllers;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import parkinggarage.Simulation;
import parkinggarage.models.Garage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by jandu on 26/01/2017.
 */
public class StatisticsController extends BaseController implements Initializable {

    // Controls
    public PieChart pieCarStats;

    private Simulation simulation;

    /*public void StatisticsController(Statistics) {
        Statistics.getDailyIncome();
        Statistics.getIncomeFromeRemainingCars();
    }*/

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void update() {
        HashMap<Garage.CarType, Integer> carStats = simulation.getGarage().getCarStats();
        Integer adhoc = carStats.get(Garage.CarType.AD_HOC);
        Integer pass = carStats.get(Garage.CarType.PASS);
        Integer reserved = carStats.get(Garage.CarType.RESERVED);
        ObservableList<PieChart.Data> stats = FXCollections.observableArrayList(
                new PieChart.Data("AdHocCar", adhoc.doubleValue()),
                new PieChart.Data("Pass", pass.doubleValue()),
                new PieChart.Data("Reserved", reserved.doubleValue()
                ));
        pieCarStats.setData(stats);
        applyCustomColorSequence(stats, "red", "blue", "green");
        stats.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pieCarStats.setAnimated(false);
    }

    private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle(
                    "-fx-pie-color: " + pieColors[i % pieColors.length] + ";"
            );
            i++;
        }
    }
}
