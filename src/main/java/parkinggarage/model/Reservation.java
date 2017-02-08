package parkinggarage.model;

/**
 * This class represents a reservation made for the Garage
 * Created by melle on 8-2-2017.
 */
public class Reservation {

    /**
     * Name of user who reserved a spot in the Garage
     */
    private String name;

    /**
     * Day of arrival
     */
    private int day;

    /**
     * Hour of arrival
     */
    private int hours;

    /**
     * Minute of arrival
     */
    private int minutes;

    /**
     * Duration in minutes
     */
    private int duration;

    public Reservation(int day, int hours, int minutes, int duration) {
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
        this.duration = duration;
    }

    public Reservation(String name, int day, int hours, int minutes, int duration) {
        this(day, hours, minutes, duration);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return day;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getDuration() {
        return duration;
    }
}
