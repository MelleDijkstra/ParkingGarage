package parkinggarage.views;

import java.io.IOException;

/**
 * Created by jandu on 19/01/2017.
 */

/**
 * Loads the settingsScreen
 */
public class SettingsScreen extends BaseScreen {

    public SettingsScreen() throws IOException {
        super();
    }

    @Override
    protected boolean includeStyling() {
        return false;
    }

    @Override
    public String getLayoutFile() {
        return "SettingsScreen.fxml";
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}

