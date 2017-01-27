package parkinggarage.views;

import java.io.IOException;

/**
 * Created by melle on 27-1-2017.
 */
public class MainScreen extends BaseScreen {

    public MainScreen() throws IOException {
        super();
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
