package parkinggarage.models;

import java.awt.*;
import java.util.Random;

public class AdHocCar extends Car {
    private static final Color COLOR = Color.red;

    public AdHocCar() {
        Random random = new Random();
        this.setMinutesLeft((int) (15 + random.nextFloat() * 3 * 60));
        this.setHasToPay(true);
    }

    public Color getColor() {
        return COLOR;
    }
}
