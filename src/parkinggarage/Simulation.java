package parkinggarage;

import parkinggarage.models.Garage;
import parkinggarage.views.SimulationView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Simulation {

    // Settings for the simulation
    private Settings settings;

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
    private Double price_per_minute = 0.24;

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
     *
     */
    public Simulation(int iterations, Settings settings) {
        this(iterations);
        this.settings = settings;
        processSettings();
    }

    private void processSettings() {
        if(settings != null) {
            day     = settings.getSetting(Settings.DAY, day);
            hour    = settings.getSetting(Settings.HOUR, hour);
            minute  = settings.getSetting(Settings.MINUTE, minute);
            price_per_minute = settings.getSetting(Settings.PRICE_PER_MINUTE, price_per_minute);

            garage.setReservedFloor(settings.getSetting(Settings.RESERVED_FLOOR, garage.getReservedFloor()));

//        weekDayArrivals = (settings.getProperty("weekDayArrivals") != null) ? Integer.parseInt(settings.get("weekDayArrivals").toString()) : weekDayArrivals;
//        weekDayPassArrivals = (settings.getProperty("weekDayPassArrivals") != null) ? Integer.parseInt(settings.get("weekDayPassArrivals").toString()) : weekDayPassArrivals;
//        weekendArrivals = (settings.getProperty("weekendArrivals") != null) ? Integer.parseInt(settings.get("weekendArrivals").toString()) : weekendArrivals;
//        weekendPassArrivals = (settings.getProperty("weekendPassArrivals") != null) ? Integer.parseInt(settings.get("weekendPassArrivals").toString()) : weekendPassArrivals;
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
        System.out.println(String.format("SIMULATION DONE - time in minutes: %d:%d",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken)));
    }

    /**
     * All the logic for a single tick.
     * This will run every (virtual) minute.
     */
    private void tick() {
        advanceTime();
        garage.handleExit();
        updateViews();
        // Pause.
        try {
            Thread.sleep(tickPause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        carsArriving();
        garage.handleEntrance();
    }

    public void toggle() {
        this.running = !this.running;
    }

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

    private int getNumberOfCars(int weekDay, int weekend) {
        Random random = new Random();

        // Get the average number of cars that arrive per hour.
        int averageNumberOfCarsPerHour = day < 5 ? weekDay : weekend;

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
