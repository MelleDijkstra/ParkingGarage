package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsController implements Initializable {

    private Properties settings = new Properties();
    static final String settingsFile = "settings.ini";

    public Button btnApply, btnOk;
    public ComboBox cbDays;
    public Slider sldTime;
    public Label lblTimeVal;

    public void btnApplyOnClick(ActionEvent actionEvent) {
        // TODO: read all values from UI form

        // TODO: store every value to file
        OutputStream out = null;
        try {
            out = new FileOutputStream(settingsFile);
            // set all properties
            settings.put("day", "0");
            settings.put("hour", "10");
            settings.put("minute", "40");

            // save settings to file
            settings.store(out, null);
        } catch(IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save settings! try again");
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
