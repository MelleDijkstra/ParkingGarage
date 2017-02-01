package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    public DoubleTextField ntfPricePerMinute;
    public Button btnApply, btnOk;
    public ComboBox<String> cbDays;
    public Slider sldTime;
    public Label lblTimeVal;

    private Properties settings = new Properties();
    static final String settingsFile = "settings.ini";

    private boolean dirtySettings = false;

    /**
     * Button for applying the changes which are made
     */
    public void btnApplyOnClick(ActionEvent actionEvent) {
        saveSettings();
    }

    private void saveSettings() {
        OutputStream out = null;
        try {
            out = new FileOutputStream(settingsFile);
            // save settings to file
            settings.store(out, null);
            dirtySettings = false;
            btnApply.setDisable(true);
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
     *
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
            setSetting(Setting.HOUR, hour.toString());
            setSetting(Setting.MINUTE, minute.toString());
        });

        ntfReservedFloor.textProperty().addListener((observable, oldValue, newValue) -> setSetting(Setting.RESERVED_FLOOR, newValue));
    }

    /**
     *
     */
    private void loadSettings() {
        settings = new Properties();
        try {
            // loads the settings file
            settings.load(new FileInputStream(settingsFile));
            btnApply.setDisable(true);

            cbDays.getSelectionModel().select(Integer.parseInt((String) settings.getOrDefault(Setting.DAY, "0")));
            Integer hour = Integer.parseInt((String) settings.getOrDefault(Setting.HOUR,"0"));
            Integer minutes = Integer.parseInt((String) settings.getOrDefault(Setting.MINUTE,"0"));
            sldTime.setValue((double)hour * 60 + minutes);
            lblTimeVal.setText((hour < 10 ? "0"+hour : hour)+":"+(minutes < 10 ? "0"+minutes : minutes));
            ntfReservedFloor.setText(settings.getOrDefault(Setting.RESERVED_FLOOR, "0").toString());
        } catch(FileNotFoundException e) {
            System.out.println("Settings file does not exist");
        } catch (IOException e) {
            System.out.println("Failed to load current settings");
            e.printStackTrace();
        }
    }

    /**
     * List of options for the day
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
     * Convert the name of the days to numbers
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
        setSetting(Setting.DAY, dayToNum(cbDays.getSelectionModel().getSelectedItem()).toString());
    }

    private void setSetting(String setting, Object value) {
        settings.put(setting, value);
        dirtySettings = true;
        btnApply.setDisable(false);
    }

    public static class Setting {
        public static final String DAY = "day";
        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";

        public static final String RESERVED_FLOOR = "reserved_floor";
    }

}
