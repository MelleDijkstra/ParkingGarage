package parkinggarage;

import parkinggarage.db.DatabaseConnection;
import parkinggarage.model.Garage;
import parkinggarage.model.Location;
import parkinggarage.model.Reservation;
import parkinggarage.views.SimulationView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Simulation {

    /**
     * The Garage object to use for ths simulation
     */
    private Garage garage;

    /**
     * The database connection to load specific simulation data
     */
    DatabaseConnection db;

    /**
     * The Simulation View which displays the actual simulation
     */
    private SimulationView simulationView;

    /**
     * The iteration where the simulation is currently at
     */
    private int currentIteration = 1;

    // Current day and time of the simulation
    private Integer day = 0;
    private Integer hour = 0;
    private Integer minute = 0;
    private Double price_per_minute = 0.24;

    // average number of arriving cars per hour
    // TODO: store in more efficient way (List maybe?)
    private Integer weekDayArrivals = 100;
    private Integer weekendArrivals = 200;
    private Integer weekDayPassArrivals = 50;
    private Integer weekendPassArrivals = 15;
    private Integer weekDayReservedArrivals = 33;
    private Integer weekendReservedArrivals = 50;

    /**
     * The amount of waiting time for each iteration
     */
    private Integer tickPause = 100;

    /**
     * Specifies if the simulation is running or paused
     */
    private boolean running = true;

    /**
     * Specifies if the simulation should stop
     */
    private boolean stop = false;

    /**
     * The amount of iterations the simulator should run
     */
    private final int iterationCount;

    /**
     * The reservations made for the garage
     */
    private ArrayList<Reservation> reservations;

    /**
     * Creates a parking garage simulation
     * @param iterations The amount of iteration to run
     */
    public Simulation(int iterations) {
        iterationCount = iterations;
        garage = new Garage(3, 6, 28);
        simulationView = new SimulationView(this);
        reservations = new ArrayList<>();
        processSettings();
        retrieveReservationsFromDatabase();
    }

    /**
     * Retrieves the reservations from the database
     */
    private void retrieveReservationsFromDatabase() {
        // Make connection in a new thread so the simulation can keep running
        new Thread(() -> {
            db = new DatabaseConnection();
            reservations = db.getReservations();
            db.close();
        }).start();
    }

    /**
     * Makes sure all settings are set which are given
     */
    private void processSettings() {
        try{
            Settings settings = Settings.Instance();

            day              = settings.getSetting(Settings.DAY, day);
            hour             = settings.getSetting(Settings.HOUR, hour);
            minute           = settings.getSetting(Settings.MINUTE, minute);
            price_per_minute = settings.getSetting(Settings.PRICE_PER_MINUTE, price_per_minute);

//        weekDayArrivals = (settings.getProperty("weekDayArrivals") != null) ? Integer.parseInt(settings.get("weekDayArrivals").toString()) : weekDayArrivals;
//        weekDayPassArrivals = (settings.getProperty("weekDayPassArrivals") != null) ? Integer.parseInt(settings.get("weekDayPassArrivals").toString()) : weekDayPassArrivals;
//        weekendArrivals = (settings.getProperty("weekendArrivals") != null) ? Integer.parseInt(settings.get("weekendArrivals").toString()) : weekendArrivals;
//        weekendPassArrivals = (settings.getProperty("weekendPassArrivals") != null) ? Integer.parseInt(settings.get("weekendPassArrivals").toString()) : weekendPassArrivals;
        } catch (IOException e) {
            System.out.println("Simulation could not load settings");
        }
    }



    /**
     * Runs the simulation for a specified amount of iterations
     */
    public void run() {
        long startTime = System.currentTimeMillis();
        while(this.currentIteration <= iterationCount) {
            //System.out.println("current iteration: "+this.currentIteration);
            tick();
            this.currentIteration++;
            if(this.stop) break;
        }
        long timeTaken = (System.currentTimeMillis() - startTime);
        System.out.println(String.format("SIMULATION DONE - time in minutes: %d:%d - earned: €%s",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken),
                new BigDecimal(garage.getIncome()).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    /**
     * All the logic for a single tick.
     * This will run every (virtual) minute.
     */
    private void tick() {
        advanceTime();
        checkReservations();
        carsArriving();
        garage.handleEntrance();
        garage.handleExit();
        updateViews();
        // Pause.
        try {
            Thread.sleep(tickPause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles the application from running state to paused
     */
    public void toggle() {
        this.running = !this.running;
    }

    /**
     * Advances 1 minute in time
     */
    private void advanceTime() {
        // Advance the time by one minute.
        minute++;
        while (minute > 59) {
            minute -= 60;
            hour++;
        }
        while (hour > 23) {
            hour -= 24;
            day++;
        }
        while (day > 6) {
            day -= 7;
        }
    }

    /**
     * Checks if spots should be reserved before any reservations come in
     */
    public void checkReservations() {
        for(Reservation reservation : reservations) {
            System.out.println(reservation.toString());
            if(reservation.getDay() == day && reservation.getHour() == hour && reservation.getMinute() == minute) {
                Location location = garage.getFirstFreeLocation(false);
                if(location != null) {
                    // TODO: Locations should be reserved for an amount of time not unlimited
                    garage.reserveLocation(location.getFloor(), location.getRow(), location.getPlace());
                }
            }
        }
    }

    /**
     * Retrieve reservations currently in simulation
     * @return The reservations
     */
    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    private void carsArriving() {
        int numberOfCars = getNumberOfCars(weekDayArrivals, weekendArrivals);
        garage.addArrivingCars(numberOfCars, Garage.CarType.AD_HOC);
        numberOfCars = getNumberOfCars(weekDayPassArrivals, weekendPassArrivals);
        garage.addArrivingCars(numberOfCars, Garage.CarType.PASS);
        numberOfCars = getNumberOfCars(weekDayReservedArrivals,weekendReservedArrivals);
        garage.addArrivingCars(numberOfCars, Garage.CarType.RESERVED);
    }

    private int getNumberOfCars(int avgCarsWeekDay, int avgCarsWeekend) {
        Random random = new Random();

        // Get the average number of cars that arrive per hour.
        int averageNumberOfCarsPerHour;

        if((day == 7 && hour >= 12 && hour < 18) || (day >= 4 && hour > 18)) {
            averageNumberOfCarsPerHour = avgCarsWeekend;
        } else {
            averageNumberOfCarsPerHour = avgCarsWeekDay;
        }

        // Calculate the number of cars that arrive this minute.
        double standardDeviation = averageNumberOfCarsPerHour * 0.3;
        double numberOfCarsPerHour = averageNumberOfCarsPerHour + random.nextGaussian() * standardDeviation;
        return (int) Math.round(numberOfCarsPerHour / 60);
    }

    /**
     * Get the current time and day in int[day,hour,min] format
     * @return integer array format: int[day,hour,min]
     */
    public int[] getDate() {
        return new int[] {day,hour,minute};
    }

    public void setTickPause(int tickPause) {
        this.tickPause = tickPause;
    }

    public Garage getGarage() {
        return garage;
    }

    private void updateViews() {
        garage.tick();
        // Update the car park view.
        simulationView.updateView();
    }

    public void close() {
        this.stop = true;
        if(simulationView.isShowing()) {
            simulationView.setVisible(false);
        }
    }

    /**
     * Retrieve the current iterationCount
     * @return The current iteration
     */
    public int getCurrentIteration() {
        return currentIteration;
    }

    public void setWeekDayArrivals(Integer weekDayArrivals) {
        this.weekDayArrivals = weekDayArrivals;
    }

    public void setWeekendArrivals(Integer weekendArrivals) {
        this.weekendArrivals = weekendArrivals;
    }

    public void setWeekDayPassArrivals(Integer weekDayPassArrivals) {
        this.weekDayPassArrivals = weekDayPassArrivals;
    }

    public void setWeekendPassArrivals(Integer weekendPassArrivals) {
        this.weekendPassArrivals = weekendPassArrivals;
    }

    public void setWeekDayReservedArrivals(Integer weekDayReservedArrivals) {
        this.weekDayReservedArrivals = weekDayReservedArrivals;
    }

    public void setWeekendReservedArrivals(Integer weekendReservedArrivals) {
        this.weekendReservedArrivals = weekendReservedArrivals;
    }
}
