package parkinggarage.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by jandu on 19/01/2017.
 */
public class SettingsController {

    private Properties settings = new Properties();
    static final String settingsFile = "settings.ini";

    public Button btnApply, btnOk;
    public DatePicker dpDay;

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
}
