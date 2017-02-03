package parkinggarage.views;

import java.io.IOException;

/**
 * Created by melle on 27-1-2017.
 */

/**
 * Loads the mainScreen and opens Dashboard
 */
public class MainScreen extends BaseScreen {

    public MainScreen() throws IOException {
        super();
    }

    @Override
    protected boolean includeStyling() {
        return true;
    }

    @Override
    public String getLayoutFile() {
        return "MainScreen.fxml";
    }

    @Override
    public String getTitle() {
        return "Dashboard";
    }

    @Override
    public int getWidth() {
        return 850;
    }

    @Override
    public int getHeight() {
        return 650;
    }
}
