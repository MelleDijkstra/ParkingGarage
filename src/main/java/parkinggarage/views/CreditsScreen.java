package parkinggarage.views;

import java.io.IOException;

/**
 * Created by Silvan on 19-1-2017.
 */

/**
 * Loads the creditsScreen
 */
public class CreditsScreen extends BaseScreen {

    public CreditsScreen() throws IOException {
        super();
    }

    @Override
    protected boolean includeStyling() {
        return true;
    }

    @Override
    public String getLayoutFile() {
        return "CreditsScreen.fxml";
    }

    @Override
    public String getTitle() {
        return "Credits";
    }

    @Override
    public int getWidth() {
        return 500;
    }

    @Override
    public int getHeight() {
        return 500;
    }
}
