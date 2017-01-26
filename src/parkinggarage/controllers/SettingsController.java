package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsController implements Initializable {

    public Button btnApply, btnOk;
    public ComboBox cbDays;
    public Slider sldTime;
    public Label lblTimeVal;

    public void btnApplyOnClick(ActionEvent actionEvent) {
        // TODO: read all values from UI form

        // TODO: store every value to file
    }

    public void btnOkOnClick(ActionEvent actionEvent) {
        // TODO: check if settings are saved

        // TODO: if not save settings

        // TODO: close screen after saving settings
    }


    public void setComboBox(ActionEvent actionEvent) {
        // TODO: read value
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sldTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            int hour = newValue.intValue() / 60;
            int minute = newValue.intValue() % 60;
            String strHour = (hour < 10) ? "0"+Integer.toString(hour) : Integer.toString(hour);
            String strMinute = (minute < 10) ? "0"+Integer.toString(minute) : Integer.toString(minute);
            lblTimeVal.setText(strHour+":"+strMinute);
        });
    }
}
