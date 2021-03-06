package parkinggarage.model;

import parkinggarage.Settings;

import java.awt.*;
import java.io.IOException;

/**
 * Abstract Car class with all the information for a Car
 */
public abstract class Car {

    /**
     * The amount of minutes the Car is going to stay in the Garage
     */
    protected final int stayingMinutes;

    /**
     * The price per minute for this Car
     */
    protected double pricePerMinute = 0.24;

    /**
     * The amount of minutes before leaving the garage
     */
    protected int minutesLeft;

    /**
     * State if the Car is paying
     */
    private boolean isPaying;

    /**
     * Flag if the Car has to amountToPay
     */
    private boolean hasToPay = true;

    /**
     * If the car is parked a little skewed (then it should be charged more)
     * if this is true it's probably a woman ;p
     */
    private boolean skewedParker;

    public Car() {
        stayingMinutes = minutesLeft = this.startingMinutes();
        processSettings();
    }

    /**
     * Creates a Car
     * @param skewedParker specify if this car is parked wrong
     */
    public Car(boolean skewedParker) {
        this();
        this.skewedParker = skewedParker;
    }

    /**
     * Process the settings for every Car
     */
    protected void processSettings() {
        try {
            pricePerMinute = Settings.Instance().getSetting(Settings.PRICE_PER_MINUTE, pricePerMinute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the minutes left for the Car in the garage
     *
     * @return The minutes left
     */
    public int getMinutesLeft() {
        return minutesLeft;
    }

    /**
     * Return paying state
     *
     * @return true if car is paying else false
     */
    public boolean getIsPaying() {
        return isPaying;
    }

    /**
     * Specify if the Car has to amountToPay
     *
     * @param isPaying True if car has to amountToPay, if not false
     */
    public void setIsPaying(boolean isPaying) {
        this.isPaying = isPaying;
    }

    /**
     * Return if the Car has to amountToPay
     *
     * @return true if Car has to amountToPay, false if not
     */
    public boolean getHasToPay() {
        return hasToPay;
    }

    /**
     * Specify if the Car has to amountToPay
     *
     * @param hasToPay true if Car has to amountToPay, if not then false
     */
    public void setHasToPay(boolean hasToPay) {
        this.hasToPay = hasToPay;
    }

    /**
     * All Car related stuff when a update happens
     */
    public void tick() {
        minutesLeft--;
    }

    /**
     * Specify the starting minutes
     *
     * @return The time the car has to stay in the garage in minutes
     */
    protected abstract int startingMinutes();

    /**
     * Calculates the amount this Car has to amountToPay for his stay
     * ! It doesn't check if it actually has to amountToPay !
     *
     * @return The price this Car has to amountToPay
     */
    public double amountToPay() {
        return stayingMinutes * pricePerMinute + (this.isSkewedParker() ? 2.30 : 0);
    }

    /**
     * Retrieves the Color of the Car
     *
     * @return Color of the Car
     */
    public abstract Color getColor();

    /**
     * Returns if this car is parked wrong
     * @return If this car is parked wrong
     */
    public boolean isSkewedParker() {
        return skewedParker;
    }
}