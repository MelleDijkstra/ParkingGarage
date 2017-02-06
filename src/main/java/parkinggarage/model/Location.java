package parkinggarage.model;

/**
 * This class represents the location of vehicles of this application
 * It stores the floor row and place
 */
public class Location {

    private int floor;
    private int row;
    private int place;
    private Car car;
    private boolean reserved;

    /**
     * Constructor for objects of class parkinggarage.model.Location
     */
    public Location(int floor, int row, int place) {
        this.floor = floor;
        this.row = row;
        this.place = place;
    }

    public Location(int floor, int row, int place, Car car) {
        this(floor, row, place);
        this.car = car;
    }

    /**
     * Implement content equality.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location other = (Location) obj;
            return floor == other.getFloor() && row == other.getRow() && place == other.getPlace();
        } else {
            return false;
        }
    }

    /**
     * Return a string of the form floor,row,place.
     *
     * @return A string representation of the location.
     */
    public String toString() {
        return floor + "," + row + "," + place;
    }

    /**
     * Use the 10 bits for each of the floor, row and place
     * values. Except for very big car parks, this should give
     * a unique hash code for each (floor, row, place) tupel.
     *
     * @return A hashcode for the location.
     */
    public int hashCode() {
        return (floor << 20) + (row << 10) + place;
    }

    /**
     * @return The floor.
     */
    public int getFloor() {
        return floor;
    }

    /**
     * @return The row.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The place.
     */
    public int getPlace() {
        return place;
    }

    /**
     * @return if this location is reserved
     */
    public boolean isReserved() { return reserved; }

    /**
     * Makes this Location a reserved location
     */
    public void setReserved() {
        this.reserved = true;
    }

    /**
     * Get the car at this location
     * @return The car or null if there is no Car at this Location
     */
    public Car getCar() { return car; }

    /**
     * Place a Car at this location
     * @param car The new car to be placed on the spots or null to remove car
     * @throws LocationOccupiedException if there already is a car on this Location
     */
    public void occupyLocation(Car car) throws LocationOccupiedException {
       if(this.car != null) throw new LocationOccupiedException();
       this.car = car;
    }

    /**
     * Removes the Car at this Location
     */
    public void removeCar() {
        this.car = null;
    }

    /**
     * The class LocationOccupiedException is thrown when spot or location is already occupied
     */
    class LocationOccupiedException extends Exception {
        LocationOccupiedException() {}

        LocationOccupiedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}