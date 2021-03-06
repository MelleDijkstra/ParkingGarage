package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import parkinggarage.Settings;
import parkinggarage.views.control.NumberTextField;
import parkinggarage.views.control.DoubleTextField;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsController extends BaseController implements Initializable {

    public NumberTextField ntfReservedFloor;
    public DoubleTextField dtfPricePerMinute;
    public Button btnApply, btnOk;
    public ComboBox<String> cbDays;
    public Slider sldTime;
    public Label lblTimeVal;

    private Settings settings;

    private boolean dirtySettings = false;

    /**
     * Button for applying the changes which are made
     */
    public void btnApplyOnClick(ActionEvent actionEvent) {
        saveSettings();
    }

    private void saveSettings() {
        try {
            settings.save();
        } catch(IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save settings! try again");
        }
        // save settings to file
        dirtySettings = false;
        btnApply.setDisable(true);
    }

    /**
     * Button for leaving the settings screen
     */
    public void btnOkOnClick(ActionEvent actionEvent) {
        if(dirtySettings) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"You have unsaved settings, save now?", ButtonType.NO, ButtonType.YES);
            alert.showAndWait();
            if(alert.getResult() == ButtonType.YES) {
                saveSettings();
            }
        }

        ((Stage)btnApply.getScene().getWindow()).close();
    }

    /**
     * Initialize this controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSettings();
        sldTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            // minutes to hour
            Integer hour = newValue.intValue() / 60;
            // remove hours to remaining minutes
            Integer minute = newValue.intValue() % 60;
            String strHour = (hour < 10) ? "0"+Integer.toString(hour) : Integer.toString(hour);
            String strMinute = (minute < 10) ? "0"+Integer.toString(minute) : Integer.toString(minute);
            // display current hour, minute
            lblTimeVal.setText(strHour+":"+strMinute);
            // stage them for saving to file
            setSetting(Settings.HOUR, hour.toString());
            setSetting(Settings.MINUTE, minute.toString());
        });

        ntfReservedFloor.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                setSetting(Settings.RESERVED_FLOOR, String.valueOf(Integer.parseInt(newValue) - 1));
            }
        });
        dtfPricePerMinute.textProperty().addListener((observable, oldValue, newValue) -> setSetting(Settings.PRICE_PER_MINUTE, newValue));
    }

    /**
     * Checking for (and loading) the Settings file
     */
    private void loadSettings() {
        try {
            // loads the settings file
            settings = Settings.Instance();
            btnApply.setDisable(true);

            cbDays.getSelectionModel().select(Integer.parseInt(settings.getSetting(Settings.DAY, "0")));
            Integer hour = settings.getSetting(Settings.HOUR,0);
            Integer minutes = settings.getSetting(Settings.MINUTE,0);
            sldTime.setValue((double)hour * 60 + minutes);
            lblTimeVal.setText((hour < 10 ? "0"+hour : hour)+":"+(minutes < 10 ? "0"+minutes : minutes));
            ntfReservedFloor.setText(String.valueOf(settings.getSetting(Settings.RESERVED_FLOOR, 0) + 1));
            dtfPricePerMinute.setText(settings.getSetting(Settings.PRICE_PER_MINUTE, 0.24).toString());
        } catch(FileNotFoundException e) {
            System.out.println("Settings file does not exist");
        } catch (IOException e) {
            System.out.println("Failed to load current settings");
            e.printStackTrace();
        }
    }

    /**
     * Convert the number of the day to a String
     */
    public static String numToDay(int daynum) {
        switch (daynum) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
        }
        return null;
    }

    /**
     * Convert the name of the day to a number
     */
    public static Integer dayToNum(String day) {
        switch (day) {
            case "Monday":
                return 0;
            case "Tuesday":
                return 1;
            case "Wednesday":
                return 2;
            case "Thursday":
                return 3;
            case "Friday":
                return 4;
            case "Saturday":
                return 5;
            case "Sunday":
                return 6;
        }
        return 0;
    }

    public void cbDayOnChange(ActionEvent actionEvent) {
        setSetting(Settings.DAY, dayToNum(cbDays.getSelectionModel().getSelectedItem()).toString());
    }

    /**
     * Applying the settings to be saved to file (doesn't save to file yet)
     * @param Settings The settings key
     * @param value THe settings value for in the file
     */
    private void setSetting(String Settings, Object value) {
        settings.getProperties().put(Settings, value);
        dirtySettings = true;
        btnApply.setDisable(false);
    }

}
