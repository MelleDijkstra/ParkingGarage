package parkinggarage;

import javafx.scene.control.Alert;

import java.io.*;
import java.util.Properties;

/**
 * This settings file has all the specifications for the simulation.
 * It is a Singleton class so that the settings are only loaded once!
 * Created by Melle/Bjorn on 2/1/2017.
 */
public class Settings {

    private static Settings instance;

    private Properties properties = new Properties();
    public static final String settingsFile = "./settings.ini";

    public static final String PRICE_PER_MINUTE = "price_per_minute";
    public static final String RESERVED_FLOOR = "reserved_floor";

    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    private Settings() throws IOException {
        properties = new Properties();
        File sFile = new File(settingsFile);
        if(!sFile.exists()) sFile.createNewFile();
        properties.load(new FileInputStream(sFile));
    }

    public void save() throws IOException {
        OutputStream out;
        out = new FileOutputStream(Settings.settingsFile);
        // save settings to file
        try {
            properties.store(out, null);
        } finally {
            try {
                out.close();
            } catch (IOException d) {
                new Alert(Alert.AlertType.ERROR, d.getMessage()).show();
                d.printStackTrace();
            }
        }
    }

    public static Settings Instance() throws IOException {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public Integer getSetting(String key, Integer defaultValue) {
        return (properties.getProperty(key) != null) ? Integer.parseInt(properties.get(key).toString()) : defaultValue;
    }

    public Double getSetting(String key, Double defaultValue) {
        return (properties.getProperty(key) != null) ? Double.parseDouble(properties.get(key).toString()) : defaultValue;
    }

    public String getSetting(String key, String defaultValue) {
        return (properties.getProperty(key) != null) ? properties.get(key).toString() : defaultValue;
    }

    public Properties getProperties() {
        return properties;
    }
}
