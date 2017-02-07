package parkinggarage.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    // textual mobility controls
    @FXML
    public Text txtMobilityAdhoc, txtMobilityPass, txtMobilityReserved;

    // textual money controls
    @FXML
    public Text txtMoneyAdhoc, txtMoneyReserved, txtMoneyTotal;

    @FXML
    public Text txtPotentialReservedQueueMoney, txtPotentialAdhocQueueMoney, txtPotentialTotalQueueMoney;

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
        updateMoneyStats();
        updateMobilityStats();
    }

    /**
     * Updates all visual mobility statistics
     */
    private void updateMobilityStats() {
        // retrieve mobility statistics
        HashMap<Garage.CarType, Integer> mobilityStats = simulation.getGarage().getMobilityStats();
        // and use them for all different views
        updateMobilityPieChart(mobilityStats);
        updateMobilityLineChart(mobilityStats);
        updateMobilityTextStats(mobilityStats);
    }

    /**
     * Updates all visual money statistics
     */
    private void updateMoneyStats() {
        // retrieve money statistics
        HashMap<Garage.CarType, Double> carMoneyLineChartStats = simulation.getGarage().getMoneyStats();
        // and use them for all different views
        updateMoneyPieChart(carMoneyLineChartStats);
        updateMoneyLineChart(carMoneyLineChartStats);
        updateMoneyTextStats(carMoneyLineChartStats);
    }

    //////////////
    // MOBILITY //
    //////////////

    /**
     * Update pie chart
     */
    private void updateMobilityPieChart(HashMap<Garage.CarType, Integer> carStats) {
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
    private void updateMobilityLineChart(HashMap<Garage.CarType, Integer> carStats) {
        adhocSeries.getData().add(getLineChartData(adhocSeries.getData().size()+1, carStats.get(Garage.CarType.AD_HOC)));
        passSeries.getData().add(getLineChartData(passSeries.getData().size()+1, carStats.get(Garage.CarType.PASS)));
        reservedSeries.getData().add(getLineChartData(reservedSeries.getData().size()+1, carStats.get(Garage.CarType.RESERVED)));
    }

    /**
     * Update textual mobility statistics
     */
    private void updateMobilityTextStats(HashMap<Garage.CarType, Integer> mobilityStats) {
        txtMobilityAdhoc.setText(Integer.toString(mobilityStats.get(Garage.CarType.AD_HOC)));
        txtMobilityPass.setText(Integer.toString(mobilityStats.get(Garage.CarType.PASS)));
        txtMobilityReserved.setText(Integer.toString(mobilityStats.get(Garage.CarType.RESERVED)));
    }

    ///////////
    // MONEY //
    ///////////

    /**
     * Update line chart
     */
    private void updateMoneyLineChart(HashMap<Garage.CarType, Double> moneyStats) {
        adhocMoneySeries.getData().add(getLineChartData(adhocMoneySeries.getData().size()+1, moneyStats.get(Garage.CarType.AD_HOC)));
        reservedMoneySeries.getData().add(getLineChartData(reservedMoneySeries.getData().size()+1, moneyStats.get(Garage.CarType.RESERVED)));
    }

    /**
     * Updates the money chart
     */
    private void updateMoneyPieChart(HashMap<Garage.CarType, Double> moneyStats) {
        ObservableList<PieChart.Data> stats = FXCollections.observableArrayList(
                new PieChart.Data("AdHocCar", new BigDecimal(moneyStats.get(Garage.CarType.AD_HOC)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()),
                new PieChart.Data("Reserved", new BigDecimal(moneyStats.get(Garage.CarType.RESERVED)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue())
        );
        pieMoneyStats.setData(stats);
        applyCustomColorSequence(stats, "red", "green", "blue");
        stats.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty())));
    }

    /**
     * Update textual money statistics
     */
    private void updateMoneyTextStats(HashMap<Garage.CarType, Double> moneyStats) {
        txtMoneyAdhoc.setText(decimalToMoney(moneyStats.get(Garage.CarType.AD_HOC)));
        txtMoneyReserved.setText(decimalToMoney(moneyStats.get(Garage.CarType.RESERVED)));
        txtMoneyTotal.setText(decimalToMoney(moneyStats.get(Garage.CarType.AD_HOC)+moneyStats.get(Garage.CarType.RESERVED)));

        HashMap<Garage.CarType, Double> potentialQueueCosts = simulation.getGarage().getPotentialQueueCosts();
        txtPotentialAdhocQueueMoney.setText(decimalToMoney(potentialQueueCosts.get(Garage.CarType.AD_HOC)));
        txtPotentialReservedQueueMoney.setText(decimalToMoney(potentialQueueCosts.get(Garage.CarType.RESERVED)));
        txtPotentialTotalQueueMoney.setText(decimalToMoney(potentialQueueCosts.get(Garage.CarType.AD_HOC)+potentialQueueCosts.get(Garage.CarType.RESERVED)));
    }

    private String decimalToMoney(Double m) {
        return "€" + new BigDecimal(m).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * Generates a single XYChart.Data item
     * @param x The x value on the chart
     * @param y The y value on the chart
     * @return The XYChart.Data value to be used on a chart
     */
    private XYChart.Data<Number, Number> getLineChartData(Number x, Number y) {
        XYChart.Data<Number, Number> data = new XYChart.Data<>(x,y);
        Rectangle rect = new Rectangle(1,1);
        rect.setVisible(false);
        data.setNode(rect);
        return data;
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

        adhocSeries.setName("AdhocCar");
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
}
