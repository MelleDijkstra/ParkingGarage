package parkinggarage.views;

import java.io.IOException;

/**
 * Created by jandu on 26/01/2017.
 */
public class StatisticsScreen extends BaseScreen {

    public StatisticsScreen() throws IOException {
        super();
    }

    @Override
    public String getLayoutFile() {
        return "StatisticsScreen.fxml";
    }

    @Override
    public String getTitle() { return "Statistics"; }

}
