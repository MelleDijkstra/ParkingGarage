package parkinggarage;

import parkinggarage.controllers.SettingsController;
import parkinggarage.model.Garage;
import parkinggarage.views.SimulationView;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Simulation {

    // Settings for the simulation
    private Properties settings = new Properties();

    protected Garage garage;

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

    // average number of arriving cars per hour
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
     * Creates a parking garage simulation
     * @param iterations The amount of iteration to run
     */
    public Simulation(int iterations) {
        iterationCount = iterations;
        garage = new Garage(3, 6, 30, 1);
        simulationView = new SimulationView(this);
    }

    /**
     * Creates a parking garage simulation with given settings
     * @param iterations The amount of iteration you want the simulation to run
     * @param settings The settings for the simulation
     */
    public Simulation(int iterations, Properties settings) {
        this(iterations);
        this.settings = settings;
        processSettings();
    }

    /**
     * Makes sure all settings are set which are given
     */
    private void processSettings() {
        day = getSetting(SettingsController.Setting.DAY, day);
        hour = getSetting(SettingsController.Setting.HOUR, hour);
        minute = getSetting(SettingsController.Setting.MINUTE, minute);

        garage.setReservedFloor(getSetting(SettingsController.Setting.RESERVED_FLOOR, garage.getReservedFloor()));

//        weekDayArrivals = (settings.getProperty("weekDayArrivals") != null) ? Integer.parseInt(settings.get("weekDayArrivals").toString()) : weekDayArrivals;
//        weekDayPassArrivals = (settings.getProperty("weekDayPassArrivals") != null) ? Integer.parseInt(settings.get("weekDayPassArrivals").toString()) : weekDayPassArrivals;
//        weekendArrivals = (settings.getProperty("weekendArrivals") != null) ? Integer.parseInt(settings.get("weekendArrivals").toString()) : weekendArrivals;
//        weekendPassArrivals = (settings.getProperty("weekendPassArrivals") != null) ? Integer.parseInt(settings.get("weekendPassArrivals").toString()) : weekendPassArrivals;
    }

    private Integer getSetting(String key, Integer defaultValue) {
        return (settings.getProperty(key) != null) ? Integer.parseInt(settings.get(key).toString()) : defaultValue;
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
        System.out.println(String.format("SIMULATION DONE - time in minutes: %d:%d - earned: %f",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken),
                garage.getIncome()));
    }

    /**
     * All the logic for a single tick.
     * This will run every (virtual) minute.
     */
    private void tick() {
        advanceTime();
        garage.handleExit();
        // Pause.
        carsArriving();
        garage.handleEntrance();
        updateViews();
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

        if(day == 7 && hour >= 12 && hour < 18) {
            averageNumberOfCarsPerHour = avgCarsWeekend;
        }
        else if(day >= 4 && hour > 18) {
            averageNumberOfCarsPerHour = avgCarsWeekend;
        }
        else {
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

}
