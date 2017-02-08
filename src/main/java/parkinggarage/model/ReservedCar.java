package parkinggarage.model;

import java.awt.*;
import java.util.Random;

/**
 * This car has reserved before entering
 * Created by melle on 26-1-2017.
 */
public class ReservedCar extends Car {
    public static final Color COLOR = Color.GREEN;

    public ReservedCar(boolean skewedParker) {
        super(skewedParker);
    }

    @Override
    protected int startingMinutes() {
        Random random = new Random();
        return (int) (20 + random.nextFloat() * 3 * 60);
    }

    @Override
    public double amountToPay() {
        return super.amountToPay() + 4.32;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

}
