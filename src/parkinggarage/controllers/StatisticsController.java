package parkinggarage.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

/**
 * Created by jandu on 26/01/2017.
 */
public class StatisticsController {

    public PieChart chart;

    /*public void StatisticsController(Statistics) {

        Statistics.getDailyIncome();
        Statistics.getIncomeFromeRemainingCars();

    }
    */
    @FXML
    public final void setData(ObservableList<PieChart.Data> value) {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Adhoc", 13),
                        new PieChart.Data("Subscription", 20),
                        new PieChart.Data("Reserved", 67));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Division of the cars");
    }
}
