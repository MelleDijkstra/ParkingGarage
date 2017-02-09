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
    private int hour;

    /**
     * Minute of arrival
     */
    private int minute;

    /**
     * Duration in minute
     */
    private int duration;

    public Reservation(int day, int hour, int minute, int duration) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.name = "";
    }

    public Reservation(String name, int day, int hour, int minute, int duration) {
        this(day, hour, minute, duration);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return String.format("reservation{name:%s,day:%d,hour:%d,minute:%d,duration:%d}",name,day,hour,minute,duration);
    }
}
