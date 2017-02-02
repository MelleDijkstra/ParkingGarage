package parkinggarage.model;

import java.awt.*;
import java.util.Random;

/**
 * This a car with a subscription
 */
public class ParkingPassCar extends Car {
    private static final Color COLOR = Color.blue;

    public ParkingPassCar() {
        this.setHasToPay(false);
    }

    @Override
    protected int startingMinutes() {
        Random random = new Random();
        return (int) (15 + random.nextFloat() * 3 * 60);
    }

    public Color getColor() {
        return COLOR;
    }
}
