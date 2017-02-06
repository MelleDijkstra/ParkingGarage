package parkinggarage.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import parkinggarage.Simulation;
import parkinggarage.model.Garage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by jandu on 26/01/2017.
 */

/**
 * Shows the information in statistics view
 */
public class StatisticsController extends BaseController implements Initializable {

    // Controls
    @FXML
    public PieChart pieCarStats;

    @FXML
    public LineChart<Number, Number> lchartCarStats;

    @FXML
    public PieChart pieMoneyStats;

    private Simulation simulation;

    XYChart.Series<Number, Number> adhocSeries;
    XYChart.Series<Number, Number> passSeries;
    XYChart.Series<Number, Number> reservedSeries;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Making a pie chart with accurate data
     */
    public void update() {
        updatePieChart();
        updateLineChart();
        updateMoneyPieChart();
    }

    private void updatePieChart() {
        HashMap<Garage.CarType, Integer> carStats = simulation.getGarage().getMobilityStats();
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

    private void updateLineChart() {
        HashMap<Garage.CarType, Integer> carStats = simulation.getGarage().getMobilityStats();
        adhocSeries.getData().add(new XYChart.Data<>(adhocSeries.getData().size() + 1, carStats.get(Garage.CarType.AD_HOC)));
        passSeries.getData().add(new XYChart.Data<>(passSeries.getData().size()+1, carStats.get(Garage.CarType.PASS)));
        reservedSeries.getData().add(new XYChart.Data<>(reservedSeries.getData().size()+1, carStats.get(Garage.CarType.RESERVED)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPieCharts();
        initLineCharts();
    }

    private void initLineCharts() {
        //Prepare XYChart.Series objects by setting data
        adhocSeries = new XYChart.Series<>();
        passSeries = new XYChart.Series<>();
        reservedSeries = new XYChart.Series<>();

        adhocSeries.setName("AdHocCar");
        passSeries.setName("Pass");
        reservedSeries.setName("Reserved");
        //Setting the data to Line chart
        lchartCarStats.getData().add(adhocSeries);
        lchartCarStats.getData().add(passSeries);
        lchartCarStats.getData().add(reservedSeries);
    }

    private void initPieCharts() {
        pieCarStats.setAnimated(false);
        pieMoneyStats.setAnimated(false);
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

    private void updateMoneyPieChart() {
        HashMap<Garage.CarType, Double> carMoneyStats = simulation.getGarage().getMoneyStats();
        ObservableList<PieChart.Data> stats = FXCollections.observableArrayList(
                new PieChart.Data("AdHocCar", carMoneyStats.get(Garage.CarType.AD_HOC)),
                new PieChart.Data("Reserved", carMoneyStats.get(Garage.CarType.RESERVED))
        );
        pieMoneyStats.setData(stats);
        applyCustomColorSequence(stats, "red", "green", "blue");
        stats.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty())));
    }
}
