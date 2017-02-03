package parkinggarage.model;

import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This Garage Class contains all information and logic for the Cars in the Garage.
 * It keeps track of the cars in the garage and has logic for entering and exiting the garage
 * Created by melle on 28-1-2017.
 */
public class Garage {

    public enum CarType {
        AD_HOC,
        PASS,
        RESERVED,
    }

    private int floors;
    private int rows;
    private int places;

    private double income;

    private int reservedFloor;

    private int openSpots;

    // The different speeds for the garage operations
    private Integer enterSpeed = 3; // number of cars that can enter per minute
    private Integer paymentSpeed = 7; // number of cars that can pay per minute
    private Integer exitSpeed = 5; // number of cars that can leave per minute

    // The queues in (and in-front of) the garage
    private LinkedList<Car> entranceCarQueue;
    private LinkedList<Car> entrancePassQueue;
    private LinkedList<Car> paymentCarQueue;
    private LinkedList<Car> exitCarQueue;

    /**
     * Locations currently in the Garage
     */
    private Location[][][] locations;

    public Garage(int floors, int rows, int places, int reservedFloor) {
        this.floors = floors;
        this.rows = rows;
        this.places = places;
        locations = new Location[floors][rows][places];
        initializeLocations();
        entranceCarQueue = new LinkedList<>();
        entrancePassQueue = new LinkedList<>();
        paymentCarQueue = new LinkedList<>();
        exitCarQueue = new LinkedList<>();
        this.openSpots = this.floors * this.rows * this.places;
        this.reservedFloor = reservedFloor;
    }

    private void initializeLocations() {
        // floors
        for(int f = 0; f < locations.length; f++) {
            // rows
            for(int r = 0; r < locations[f].length;r++) {
                // places
                for(int p = 0; p < locations[f][r].length; p++) {
                    locations[f][r][p] = new Location(f,r,p);
                    if(r == 0 && p % 2 == 0) locations[f][r][p].setReserved();
                }
            }
        }
    }

    public Location[][][] getLocations() {
        return locations;
    }

    public int getReservedFloor() {
        return reservedFloor;
    }

    public void setReservedFloor(int reservedFloor) {
        this.reservedFloor = reservedFloor;
    }

    public int getNumberOfFloors() {
        return floors;
    }

    public int getNumberOfRows() {
        return rows;
    }

    public int getNumberOfPlaces() {
        return places;
    }

    public int getOpenSpots() {
        return openSpots;
    }

    public double getIncome() { return income; }

    public void handleEntrance() {
        carsEntering(entrancePassQueue);
        carsEntering(entranceCarQueue);
    }

    public void handleExit() {
        carsReadyToLeave();
        carsPaying();
        carsLeaving();
    }

    public void addArrivingCars(int numberOfCars, CarType type) {
        // Add the cars to the back of the queue.
        switch (type) {
            case AD_HOC:
                for(int i = 0; i < numberOfCars; i++) {
                    entranceCarQueue.add(new AdHocCar());
                }
                break;
            case PASS:
                for(int i = 0; i < numberOfCars; i++) {
                    entrancePassQueue.add(new ParkingPassCar());
                }
                break;
            case RESERVED:
                for(int i = 0; i < numberOfCars; i++) {
                    entrancePassQueue.add(new ReservedCar());
                }
                break;
        }
    }

    /**
     * Assign parking space for every incoming Car
     * @param queue The incoming cars
     */
    private void carsEntering(Queue<Car> queue) {
        int i = 0;
        // Remove car from the front of the queue and assign to a parking space.
        while(queue.size() > 0 && i < enterSpeed) {
            Location freeLocation = getFirstFreeLocation((queue.peek() instanceof ParkingPassCar || queue.peek() instanceof  ReservedCar));
            if(freeLocation != null) {
                Car car = queue.poll();
                try {
                    locations[freeLocation.getFloor()][freeLocation.getRow()][freeLocation.getPlace()].occupyLocation(car);
                    i++;
                } catch (Location.LocationOccupiedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private void carsReadyToLeave() {
        // Add leaving cars to the payment queue.
        // TODO: check if the leave procedure is still the same
        Car car = getFirstLeavingCar();
        while (car != null) {
            if (car.getHasToPay()) {
                car.setIsPaying(true);
                paymentCarQueue.add(car);
            } else {
                exitCarQueue.add(car);
            }
            car = getFirstLeavingCar();
        }
    }

    private void carsPaying() {
        // Let cars pay.
        int i = 0;
        while (paymentCarQueue.size() > 0 && i < paymentSpeed) {
            Car car = paymentCarQueue.poll();
            // double check if car has to pay
            if(car.getHasToPay()) {
                BigDecimal roundOff = new BigDecimal(car.amountToPay()).setScale(2, BigDecimal.ROUND_HALF_UP);
                income += car.amountToPay();
                BigDecimal roundedIncome = new BigDecimal(income).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("Car is paying: €" + roundOff +"\tIncome: €"+ roundedIncome);
            }
            exitCarQueue.add(car);
            i++;
        }
    }

    private void carsLeaving() {
        // Let cars leave.
        int i = 0;
        while (exitCarQueue.size() > 0 && i < exitSpeed) {
            exitCarQueue.poll();
            i++;
        }
    }

    public Car getCarAt(Location location) {
        if (!locationIsValid(location)) {
            return null;
        }
        return locations[location.getFloor()][location.getRow()][location.getPlace()].getCar();
    }

    public boolean setCarAt(Location location, Car car) {
        if (!locationIsValid(location)) {
            return false;
        }
        Car oldCar = getCarAt(location);
        if (oldCar == null) {
            try {
                locations[location.getFloor()][location.getRow()][location.getPlace()].occupyLocation(car);
                openSpots--;
                return true;
            } catch (Location.LocationOccupiedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Removes the Car at a specific Location
     * @param location The Location from where the Car needs to be removed
     * @return The removed Car
     */
    public Car removeCarAt(Location location) {
        if (!locationIsValid(location)) {
            return null;
        }
        Car car = getCarAt(location);
        if (car == null) {
            return null;
        }
        locations[location.getFloor()][location.getRow()][location.getPlace()].removeCar();
        openSpots++;
        return car;
    }

    /**
     * Returns first free location in garage otherwise null
     * @return The first free location
     * @param includeReservedSpace boolean if reserved space should be included
     */
    @Nullable
    public Location getFirstFreeLocation(boolean includeReservedSpace) {
        for(int f = 0; f < locations.length; f++) {
            for (int r = 0; r < locations[f].length; r++) {
                for (int p = 0; p < locations[f][r].length; p++) {
                    Location location = locations[f][r][p];
                    // check if there is no car on this location
                    if(location.getCar() == null) {
                        // if reserved space should be included
                        if (!location.isReserved() || includeReservedSpace) {
                            return location;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Car getFirstLeavingCar() {
        for (int floor = 0; floor < getNumberOfFloors(); floor++) {
            for (int row = 0; row < getNumberOfRows(); row++) {
                for (int place = 0; place < getNumberOfPlaces(); place++) {
                    Car car = locations[floor][row][place].getCar();
                    if (car != null && car.getMinutesLeft() <= 0 && !car.getIsPaying()) {
                        locations[floor][row][place].removeCar();
                        return car;
                    }
                }
            }
        }
        return null;
    }

    public void tick() {
        for (int floor = 0; floor < getNumberOfFloors(); floor++) {
            for (int row = 0; row < getNumberOfRows(); row++) {
                for (int place = 0; place < getNumberOfPlaces(); place++) {
                    Location location = new Location(floor, row, place);
                    Car car = getCarAt(location);
                    if (car != null) {
                        car.tick();
                    }
                }
            }
        }
    }

    /**
     * Goes through all locations and calculates how many of each Car are in the Garage
     * @return HashMap with key CarType and Integer as value representing the amount of that car in the Garage
     */
    public HashMap<CarType, Integer> getCarStats() {
        HashMap<CarType, Integer> stats = new HashMap<>();
        int adhoc = 0, pass = 0, reserved = 0;
        // loop trough all cars in garage
        for(int i = 0; i < locations.length; i++) {
            for(int j = 0; j < locations[i].length; j++) {
                for (int k = 0; k < locations[i][j].length; k++) {
                    Car car = locations[i][j][k].getCar();
                    // check which class is Car is at current location
                    if(car instanceof AdHocCar) adhoc++;
                    if(car instanceof ParkingPassCar) pass++;
                    if(car instanceof ReservedCar) reserved++;
                }
            }
        }
        stats.put(CarType.AD_HOC, adhoc);
        stats.put(CarType.PASS, pass);
        stats.put(CarType.RESERVED, reserved);
        return stats;
    }

    public HashMap<String, Integer> getQueueStats() {
        return new HashMap<String, Integer>() {{
            put("Entrance Queue", entranceCarQueue.size());
            put("Entrance Pass Queue", entrancePassQueue.size());
            put("Payment Queue", paymentCarQueue.size());
            put("Exit Queue", exitCarQueue.size());
        }};
    }

    public int getEntranceQueueSize() {
        return entranceCarQueue.size();
    }

    public int getEntrancePassQueueSize() {
        return entrancePassQueue.size();
    }

    /**
     * Check if given location is an actual location in the garage
     * @param location the location to check
     * @return true if it is a location in garage otherwise false
     */
    private boolean locationIsValid(Location location) {
        int floor = location.getFloor();
        int row = location.getRow();
        int place = location.getPlace();
        return !(floor < 0 || floor >= floors || row < 0 || row > rows || place < 0 || place > places);
    }
}
