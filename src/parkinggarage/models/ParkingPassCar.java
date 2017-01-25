package parkinggarage.models;

import java.awt.*;
import java.util.Random;

public class ParkingPassCar extends Car {
    private static final Color COLOR = Color.blue;

    public ParkingPassCar() {
        Random random = new Random();
        this.setMinutesLeft((int) (15 + random.nextFloat() * 3 * 60));
        this.setHasToPay(false);
    }

    public Color getColor() {
        return COLOR;
    }
}
