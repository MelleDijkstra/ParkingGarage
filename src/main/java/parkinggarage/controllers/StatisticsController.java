package parkinggarage.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import parkinggarage.Simulation;
import parkinggarage.model.Garage;

import java.math.BigDecimal;
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
    public LineChart<Number, Number> lchartMoneyStats;

    @FXML
    public PieChart pieMoneyStats;

    @FXML
    public Text txtMobilityAdhoc;

    @FXML
    public Text txtMobilityPass;

    @FXML
    public Text txtMobilityReserved;

    @FXML
    public Text txtMoneyAdhoc;

    @FXML
    public Text txtMoneyReserved;

    @FXML
    public Text txtPotentialAdhocQueueMoney;

    @FXML
    public Text txtPotentialReservedQueueMoney;

    private Simulation simulation;

    // All the different line chart series data
    private XYChart.Series<Number, Number> adhocSeries;
    private XYChart.Series<Number, Number> passSeries;
    private XYChart.Series<Number, Number> reservedSeries;

    private XYChart.Series<Number, Number> adhocMoneySeries;
    private XYChart.Series<Number, Number> reservedMoneySeries;

    /**
     * Sets the simulation for this controller
     * @param simulation The simulation instance
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Making a pie chart with accurate data
     */
    public void update() {
        updateMobilityPieChart();
        updateMobilityLineChart();
        updateMoneyLineChart();
        updateMoneyPieChart();
        updateTextStatistics();
    }

    /**
     * Update pie chart
     */
    private void updateMobilityPieChart() {
        HashMap<Garage.CarType, Integer> carStats = simulation.getGarage().getMobilityStats();
        ObservableList<PieChart.Data> stats = FXCollections.observableArrayList(
                new PieChart.Data("AdHocCar", carStats.get(Garage.CarType.AD_HOC)),
                new PieChart.Data("Pass", carStats.get(Garage.CarType.PASS)),
                new PieChart.Data("Reserved", carStats.get(Garage.CarType.RESERVED)
                ));
        pieCarStats.setData(stats);
        applyCustomColorSequence(stats, "red", "blue", "green");
        stats.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty())));
    }

    /**
     * Update line chart
     */
    private void updateMoneyLineChart() {
        HashMap<Garage.CarType, Double> carMoneyLineChartStats = simulation.getGarage().getMoneyStats();
        adhocMoneySeries.getData().add(new XYChart.Data<>(adhocMoneySeries.getData().size()+1, carMoneyLineChartStats.get(Garage.CarType.AD_HOC)));
        reservedMoneySeries.getData().add(new XYChart.Data<>(reservedMoneySeries.getData().size()+1, carMoneyLineChartStats.get(Garage.CarType.RESERVED)));
    }

    /**
     * Update line chart
     */
    private void updateMobilityLineChart() {
        HashMap<Garage.CarType, Integer> carStats = simulation.getGarage().getMobilityStats();
        adhocSeries.getData().add(new XYChart.Data<>(adhocSeries.getData().size()+1, carStats.get(Garage.CarType.AD_HOC)));
        passSeries.getData().add(new XYChart.Data<>(passSeries.getData().size()+1, carStats.get(Garage.CarType.PASS)));
        reservedSeries.getData().add(new XYChart.Data<>(reservedSeries.getData().size()+1, carStats.get(Garage.CarType.RESERVED)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPieCharts();
        initLineCharts();
    }

    /**
     * Initializes the charts
     */
    private void initLineCharts() {
        //Prepare XYChart.Series objects by setting data
        adhocSeries = new XYChart.Series<>();
        passSeries = new XYChart.Series<>();
        reservedSeries = new XYChart.Series<>();

        adhocMoneySeries = new XYChart.Series<>();
        reservedMoneySeries = new XYChart.Series<>();

        adhocSeries.setName("AdHocCar");
        passSeries.setName("Pass");
        reservedSeries.setName("Reserved");

        adhocMoneySeries.setName("Adhoc");
        reservedMoneySeries.setName("Reserved");
        //Setting the data to Line chart
        lchartCarStats.getData().add(adhocSeries);
        lchartCarStats.getData().add(passSeries);
        lchartCarStats.getData().add(reservedSeries);

        lchartMoneyStats.getData().add(adhocMoneySeries);
        lchartMoneyStats.getData().add(reservedMoneySeries);
    }

    private void initPieCharts() {
        // initialize the pie charts
    }

    /**
     * Set the colors for the pieChart data
     * @param pieChartData The data
     * @param pieColors The colors
     */
    private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle(
                    "-fx-pie-color: " + pieColors[i % pieColors.length] + ";"
            );
            i++;
        }
    }

    /**
     * Updates the money chart
     */
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

    private void updateTextStatistics() {
        HashMap<Garage.CarType, Double> moneyStats = simulation.getGarage().getMoneyStats();
        txtMoneyAdhoc.setText("€" + new BigDecimal(moneyStats.get(Garage.CarType.AD_HOC)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        txtMoneyReserved.setText("€" + new BigDecimal(moneyStats.get(Garage.CarType.RESERVED)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        HashMap<Garage.CarType, Integer> mobilityStats = simulation.getGarage().getMobilityStats();
        txtMobilityAdhoc.setText(Integer.toString(mobilityStats.get(Garage.CarType.AD_HOC)));
        txtMobilityPass.setText(Integer.toString(mobilityStats.get(Garage.CarType.PASS)));
        txtMobilityReserved.setText(Integer.toString(mobilityStats.get(Garage.CarType.RESERVED)));
        HashMap<Garage.CarType, Double> potentialQueueCosts = simulation.getGarage().getPotentialQueueCosts();
        txtPotentialAdhocQueueMoney.setText("€" + new BigDecimal(potentialQueueCosts.get(Garage.CarType.AD_HOC)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        txtPotentialReservedQueueMoney.setText("€" + new BigDecimal(potentialQueueCosts.get(Garage.CarType.RESERVED)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
}
