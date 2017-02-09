package parkinggarage.model;

import parkinggarage.Settings;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
    private double adhocIncome;
    private double reservedIncome;

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

    /**
     * Creates a Garage object
     * @param floors The number of floors for the garage
     * @param rows Number of rows per floor
     * @param places Number of places for each floor
     */
    public Garage(int floors, int rows, int places) {
        this.floors = floors;
        this.rows = rows;
        this.places = places;
        processSettings();
        locations = new Location[floors][rows][places];
        initializeLocations();
        entranceCarQueue = new LinkedList<>();
        entrancePassQueue = new LinkedList<>();
        paymentCarQueue = new LinkedList<>();
        exitCarQueue = new LinkedList<>();
        this.openSpots = this.floors * this.rows * this.places;
    }

    private void processSettings() {
        try{
            Settings settings = Settings.Instance();
            reservedFloor = settings.getSetting(Settings.RESERVED_FLOOR, reservedFloor);
        } catch (IOException e) {
            System.out.println("Garage could not load settings");
        }
    }

    private void initializeLocations() {
        // floors
        for(int f = 0; f < locations.length; f++) {
            // rows
            for(int r = 0; r < locations[f].length; r++) {
                // places
                for(int p = 0; p < locations[f][r].length; p++) {
                    locations[f][r][p] = new Location(f,r,p);
                    if(f == reservedFloor) locations[f][r][p].setReserved();
                }
            }
        }
    }

    public Location[][][] getLocations() {
        return locations;
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

    public double getIncome() { return totalIncome; }

    /**
     * Handle all the operations which are needed for Cars entering
     */
    public void handleEntrance() {
        carsEntering(entrancePassQueue);
        carsEntering(entranceCarQueue);
    }

    /**
     * Handles all the operations which are needed for cars leaving and exiting the Garage
     */
    public void handleExit() {
        carsReadyToLeave();
        carsPaying();
        carsLeaving();
    }

    public void addArrivingCars(int numberOfCars, CarType type) {
        // Add the cars to the back of the queue.
        Random r = new Random();
        for(int i = 0; i < numberOfCars; i++) {
            // the change someone drives away because the queue is too long
            double entranceChange = 100 - (entranceCarQueue.size() * 1.10);
            double entrancePassChange = 100 - (entrancePassQueue.size() * 1.10);
            System.out.println("entrance change: "+Math.round(entranceChange)+"% - entrance pass: "+Math.round(entrancePassChange)+"%");
            switch (type) {
                case AD_HOC:
                    System.out.println("change for entering: "+entranceChange);
                    if(r.nextInt(100) <= entranceChange) {
                        // 3% change if the car parks wrong
                        entranceCarQueue.add(new AdHocCar(r.nextInt(100) <= 3));
                    } else {
                        // The car left because the queue was to big
                        // TODO: handle leaving cars when queue to big, (maybe add to statistics)
                        System.out.println("Car left :(");
                    }
                    break;
                case PASS:
                    if(r.nextInt(100) <= entrancePassChange)
                        entrancePassQueue.add(new ParkingPassCar());
                    break;
                case RESERVED:
                    if(r.nextInt(100) <= entrancePassChange)
                        entrancePassQueue.add(new ReservedCar(r.nextInt(100) <= 3));
                    break;
            }
        }
    }

    /**
     * Assign parking space for every incoming Car
     * @param queue The incoming cars
     */
    private void carsEntering(Queue<Car> queue) {
        int i = 0;
        // Remove car from the front of the queue and assign to a parking space.
        while (queue.size() > 0 && i < enterSpeed) {
            Location freeLocation = getFirstFreeLocation((queue.peek() instanceof ParkingPassCar || queue.peek() instanceof ReservedCar));
            if (freeLocation != null) {
                Car car = queue.poll();
                try {
                    locations[freeLocation.getFloor()][freeLocation.getRow()][freeLocation.getPlace()].occupyLocation(car);
                    openSpots--;
                    i++;
                } catch (Location.LocationOccupiedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    /**
     * Checks for cars which are leaving and sets them to the exit queue or payment queue
     */
    private void carsReadyToLeave() {
        // Add leaving cars to the payment queue.
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

    /**
     * Handles all the payment of cars in the payment queue
     */
    private void carsPaying() {
        // Let cars pay.
        int i = 0;

        while (paymentCarQueue.size() > 0 && i < paymentSpeed) {
            Car car = paymentCarQueue.poll();
            // double check if car has to pay
            if(car.getHasToPay()) {
                if (car instanceof AdHocCar) {
                    adhocIncome += car.amountToPay();
                } else if (car instanceof ReservedCar) {
                    reservedIncome += car.amountToPay();
                }
                BigDecimal roundOff = new BigDecimal(car.amountToPay()).setScale(2, BigDecimal.ROUND_HALF_UP);
                totalIncome += car.amountToPay();
                BigDecimal roundedIncome = new BigDecimal(totalIncome).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("Car is paying: €" + roundOff + "\tIncome: €" + roundedIncome);
            }
            exitCarQueue.add(car);
            i++;
        }
    }

    /**
     * Handles Cars which are in exit queue to leave
     */
    private void carsLeaving() {
        // Let cars leave.
        int i = 0;
        while (exitCarQueue.size() > 0 && i < exitSpeed) {
            exitCarQueue.poll();
            i++;
        }
    }

    /**
     * Get Car at specified location
     * @param location The location specified
     * @return The Car at this location or null
     */
    public Car getCarAt(Location location) {
        if (!locationIsValid(location)) {
            return null;
        }
        return locations[location.getFloor()][location.getRow()][location.getPlace()].getCar();
    }

    /**
     * Returns first free location in garage otherwise null
     *
     * @param includeReservedSpace boolean if reserved space should be included
     * @return The first free location
     */
    public Location getFirstFreeLocation(boolean includeReservedSpace) {
        // TODO: make only 1 loop instead of 2 loops (which is needed now so reserved space is chosen first before normal places)
        if(includeReservedSpace) {
            // If reserved space needs to be checked first
            for(int f = 0; f < locations.length; f++) {
                for (int r = 0; r < locations[f].length; r++) {
                    for (int p = 0; p < locations[f][r].length; p++) {
                        Location location = locations[f][r][p];
                        // check if there is no car on this location and it is reserved
                        if(location.getCar() == null && location.isReserved()) {
                            return location;
                        }
                    }
                }
            }
        }
        // go through garage again to check for normal places
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
        for (int floor = 0; floor < locations.length; floor++) {
            for (int row = 0; row < locations[floor].length; row++) {
                for (int place = 0; place < locations[floor][row].length; place++) {
                    Car car = locations[floor][row][place].getCar();
                    if (car != null && car.getMinutesLeft() <= 0 && !car.getIsPaying()) {
                        locations[floor][row][place].removeCar();
                        openSpots++;
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
    /**
     * Goes through all locations and calculates how many of each Car are in the Garage
     * @return HashMap with key CarType and Integer as value representing the amount of that car in the Garage
     */
        HashMap<CarType, Integer> stats = new HashMap<>();
        int adhoc = 0, pass = 0, reserved = 0;
        // loop trough all cars in garage
        for(int i = 0; i < locations.length; i++) {
            for(int j = 0; j < locations[i].length; j++) {
                for (int k = 0; k < locations[i][j].length; k++) {
                    Car car = locations[i][j][k].getCar();
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

    public HashMap<CarType, Double> getMoneyStats() {
        HashMap<CarType, Double> moneyStats = new HashMap<>();
        moneyStats.put(CarType.AD_HOC, adhocIncome);
        moneyStats.put(CarType.RESERVED, reservedIncome);
        return moneyStats;
    }

    public HashMap<CarType, Double> getPotentialQueueCosts() {
        HashMap<CarType, Double> queueCosts = new HashMap<>();
        double adhocTotal = 0, reservedTotal = 0;
        for (Car car : entranceCarQueue) {
            adhocTotal += car.amountToPay();
        }

        for(Car car : entrancePassQueue) {
            if(car instanceof ReservedCar) {
                reservedTotal += car.amountToPay();
            }
        }

        queueCosts.put(CarType.AD_HOC, adhocTotal);
        queueCosts.put(CarType.RESERVED, reservedTotal);
        return queueCosts;
    }
}
