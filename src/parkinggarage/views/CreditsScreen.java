package parkinggarage.views;

import java.io.IOException;

/**
 * Created by Silvan on 19-1-2017.
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
}
