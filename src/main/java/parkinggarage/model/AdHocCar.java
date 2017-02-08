package parkinggarage.model;

import java.awt.*;
import java.util.Random;

/**
 * This is a normal arriving car and goes through the normal procedure
 */
public class AdHocCar extends Car {
    private static final Color COLOR = Color.red;

    public AdHocCar(boolean skewedParker) {
        super(skewedParker);
        this.setHasToPay(true);
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
