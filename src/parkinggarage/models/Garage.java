package parkinggarage.models;

import com.sun.istack.internal.Nullable;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.math.BigDecimal;
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

    private double totalIncome;
    private double adhocincome;
    private double passincome;
    private double reservedincome;

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
     * Cars currently in the Garage
     */
    private Car[][][] cars;

    public Garage(int floors, int rows, int places, int reservedFloor) {
        this.floors = floors;
        this.rows = rows;
        this.places = places;
        cars = new Car[floors][rows][places];
        entranceCarQueue = new LinkedList<>();
        entrancePassQueue = new LinkedList<>();
        paymentCarQueue = new LinkedList<>();
        exitCarQueue = new LinkedList<>();
        this.openSpots = this.floors * this.rows * this.places;
        this.reservedFloor = reservedFloor;
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
                for (int i = 0; i < numberOfCars; i++) {
                    entranceCarQueue.add(new AdHocCar());
                }
                break;
            case PASS:
                for (int i = 0; i < numberOfCars; i++) {
                    entrancePassQueue.add(new ParkingPassCar());
                }
                break;
            case RESERVED:
                for (int i = 0; i < numberOfCars; i++) {
                    entrancePassQueue.add(new ReservedCar());
                }
                break;
        }
    }

    private void carsEntering(Queue<Car> queue) {
        int i = 0;
        // Remove car from the front of the queue and assign to a parking space.
        while (queue.size() > 0 && i < enterSpeed) {
            Location freeLocation = getFirstFreeLocation((queue.peek() instanceof ParkingPassCar || queue.peek() instanceof ReservedCar));
            if (freeLocation != null) {
                Car car = queue.poll();
                setCarAt(freeLocation, car);
                i++;
            } else {
                break;
            }
        }
    }

    private void carsReadyToLeave() {
        // Add leaving cars to the payment queue.
        Car car = getFirstLeavingCar();
        while (car != null) {
            if (car.getHasToPay()) {
                car.setIsPaying(true);
                paymentCarQueue.add(car);
            } else {
                carLeavesSpot(car);
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
                if (car instanceof AdHocCar) {
                    adhocincome += car.amountToPay();
                } else if (car instanceof ReservedCar) {
                    reservedincome += car.amountToPay();
                }
                BigDecimal roundOff = new BigDecimal(car.amountToPay()).setScale(2, BigDecimal.ROUND_HALF_UP);
                totalIncome += car.amountToPay();
                BigDecimal roundedIncome = new BigDecimal(totalIncome).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("Car is paying: €" + roundOff + "\tIncome: €" + roundedIncome);
            }
            carLeavesSpot(car);
            i++;
        }
    }

    private void carLeavesSpot(Car car) {
        removeCarAt(car.getLocation());
        exitCarQueue.add(car);
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
        return cars[location.getFloor()][location.getRow()][location.getPlace()];
    }

    public boolean setCarAt(Location location, Car car) {
        if (!locationIsValid(location)) {
            return false;
        }
        Car oldCar = getCarAt(location);
        if (oldCar == null) {
            cars[location.getFloor()][location.getRow()][location.getPlace()] = car;
            car.setLocation(location);
            openSpots--;
            return true;
        }
        return false;
    }

    /**
     * Removes the Car at a specific
     * @param location
     * @return
     */
    public Car removeCarAt(Location location) {
        if (!locationIsValid(location)) {
            return null;
        }
        Car car = getCarAt(location);
        if (car == null) {
            return null;
        }
        cars[location.getFloor()][location.getRow()][location.getPlace()] = null;
        car.setLocation(null);
        openSpots++;
        return car;
    }

    /**
     * Returns first free location in garage otherwise null
     *
     * @param includeReservedSpace boolean if reserved space should be included
     * @return The first free location
     */
    @Nullable
    public Location getFirstFreeLocation(boolean includeReservedSpace) {
        // Check if we need to include reserved space
        if (includeReservedSpace) {
            for (int row = 0; row < getNumberOfRows(); row++) {
                for (int place = 0; place < getNumberOfPlaces(); place++) {
                    Location location = new Location(this.reservedFloor, row, place);
                    // check all rows and places of the reserved space for a free spot (car == null)
                    if (getCarAt(location) == null) {
                        return location;
                    }
                }
            }
        }
        for (int floor = 0; floor < getNumberOfFloors(); floor++) {
            // We have already been through the reserved space
            if (floor == this.reservedFloor) continue;
            for (int row = 0; row < getNumberOfRows(); row++) {
                for (int place = 0; place < getNumberOfPlaces(); place++) {
                    Location location = new Location(floor, row, place);
                    if (getCarAt(location) == null && (this.reservedFloor != floor || includeReservedSpace)) {
                        return location;
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
                    Location location = new Location(floor, row, place);
                    Car car = getCarAt(location);
                    if (car != null && car.getMinutesLeft() <= 0 && !car.getIsPaying()) {
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

    public HashMap<CarType, Integer> getMobilityStats() {
        HashMap<CarType, Integer> stats = new HashMap<>();
        int adhoc = 0, pass = 0, reserved = 0;
        // loop trough all cars in garage
        for (int i = 0; i < cars.length; i++) {
            for (int j = 0; j < cars[i].length; j++) {
                for (int k = 0; k < cars[i][j].length; k++) {
                    Car car = cars[i][j][k];
                    // check which class is Car is at current location
                    if (car instanceof AdHocCar) adhoc++;
                    if (car instanceof ParkingPassCar) pass++;
                    if (car instanceof ReservedCar) reserved++;
                }
            }
        }
        stats.put(CarType.AD_HOC, adhoc);
        stats.put(CarType.PASS, pass);
        stats.put(CarType.RESERVED, reserved);
        return stats;
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

    public HashMap<CarType, Double> getMoneyStats() {
        HashMap<CarType, Double> moneyStats = new HashMap<>();
        moneyStats.put(CarType.AD_HOC, adhocincome);
        moneyStats.put(CarType.RESERVED, reservedincome);
        return moneyStats;
    }
}
