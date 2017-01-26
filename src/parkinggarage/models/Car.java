package parkinggarage.models;

import java.awt.*;

/**
 * Abstract Car class with all the information for a Car
 */
public abstract class Car {

    /**
     * Location of the car
     */
    protected Location location;

    /**
     * The amount of minutes before leaving the garage
     */
    protected int minutesLeft;

    /**
     * State if the Car is paying
     */
    private boolean isPaying;

    /**
     * Flag if the Car has to pay
     */
    private boolean hasToPay;

    /**
     * Constructor for objects of class parkinggarage.models.Car
     */
    public Car() {
        minutesLeft = this.startingMinutes();
    }

    /**
     * Retrieve the location of this car
     * @return The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location The new location of the Car
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Retrieve the minutes left for the Car in the garage
     * @return The minutes left
     */
    public int getMinutesLeft() {
        return minutesLeft;
    }

    /**
     * Return paying state
     * @return true if car is paying else false
     */
    public boolean getIsPaying() {
        return isPaying;
    }

    /**
     * Specify if the Car has to pay
     * @param isPaying True if car has to pay, if not false
     */
    public void setIsPaying(boolean isPaying) {
        this.isPaying = isPaying;
    }

    /**
     * Return if the Car has to pay
     * @return true if Car has to pay, false if not
     */
    public boolean getHasToPay() {
        return hasToPay;
    }

    /**
     * Specify if the Car has to pay
     * @param hasToPay true if Car has to pay, if not then false
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
     * @return The time the car has to stay in the garage in minutes
     */
    protected abstract int startingMinutes();

    /**
     * Retrieves the Color of the Car
     * @return Color of the Car
     */
    public abstract Color getColor();
}