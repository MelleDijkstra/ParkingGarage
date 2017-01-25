package parkinggarage;

import parkinggarage.models.AdHocCar;
import parkinggarage.models.Car;
import parkinggarage.models.Location;
import parkinggarage.models.ParkingPassCar;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;

public class Simulation {

    private enum CarType {
        AD_HOC,
        PASS,
    }

    // Settings for the simulation
    private Properties settings = new Properties();

    // The queues in (and in-front of) the garage
    private LinkedList<Car> entranceCarQueue;
    private LinkedList<Car> entrancePassQueue;
    private LinkedList<Car> paymentCarQueue;
    private LinkedList<Car> exitCarQueue;

    /**
     * The Simulation View which displays the actual simulation
     */
    private SimulationView simulationView;

    /**
     * The iteration where the simulation is currently at
     */
    private int currentIteration = 1;

    private Integer day = 0;
    private Integer hour = 0;
    private Integer minute = 0;

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
    private boolean stop = true;

    private Integer weekDayArrivals = 100; // average number of arriving cars per hour
    private Integer weekendArrivals = 200; // average number of arriving cars per hour
    private Integer weekDayPassArrivals = 50; // average number of arriving cars per hour
    private Integer weekendPassArrivals = 5; // average number of arriving cars per hour

    private Integer enterSpeed = 3; // number of cars that can enter per minute
    private Integer paymentSpeed = 7; // number of cars that can pay per minute
    private Integer exitSpeed = 5; // number of cars that can leave per minute

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
        entranceCarQueue = new LinkedList<>();
        entrancePassQueue = new LinkedList<>();
        paymentCarQueue = new LinkedList<>();
        exitCarQueue = new LinkedList<>();
        simulationView = new SimulationView(this, 3, 6, 30);
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

    private void processSettings() {
        day = (settings.getProperty("day") != null) ? Integer.parseInt(settings.get("day").toString()) : day;
        hour = (settings.getProperty("hour") != null) ? Integer.parseInt(settings.get("hour").toString()) : hour;
        minute = (settings.getProperty("minute") != null) ? Integer.parseInt(settings.get("minute").toString()) : minute;
        weekDayArrivals = (settings.getProperty("weekDayArrivals") != null) ? Integer.parseInt(settings.get("weekDayArrivals").toString()) : weekDayArrivals;
        weekDayPassArrivals = (settings.getProperty("weekDayPassArrivals") != null) ? Integer.parseInt(settings.get("weekDayPassArrivals").toString()) : weekDayPassArrivals;
        weekendArrivals = (settings.getProperty("weekendArrivals") != null) ? Integer.parseInt(settings.get("weekendArrivals").toString()) : weekendArrivals;
        weekendPassArrivals = (settings.getProperty("weekendPassArrivals") != null) ? Integer.parseInt(settings.get("weekendPassArrivals").toString()) : weekendPassArrivals;

    }

    public void run() {
        while(this.running && this.currentIteration <= iterationCount) {
            System.out.println("current iteration: "+this.currentIteration);
            tick();
            this.currentIteration++;
        }
    }

    private void tick() {
        advanceTime();
        handleExit();
        updateViews();
        // Pause.
        try {
            Thread.sleep(tickPause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handleEntrance();
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

    /**
     * Get the current time in int[day,hour,min] format
     * @return integer array format: int[day,hour,min]
     */
    public int[] getTime() {
        return new int[] {day,hour,minute};
    }

    public void setTickPause(int tickPause) {
        this.tickPause = tickPause;
    }

    private void handleEntrance() {
        carsArriving();
        carsEntering(entrancePassQueue);
        carsEntering(entranceCarQueue);
    }

    private void handleExit() {
        carsReadyToLeave();
        carsPaying();
        carsLeaving();
    }

    private void updateViews() {
        simulationView.tick();
        // Update the car park view.
        simulationView.updateView();
    }

    private void carsArriving() {
        int numberOfCars = getNumberOfCars(weekDayArrivals, weekendArrivals);
        addArrivingCars(numberOfCars, CarType.AD_HOC);
        numberOfCars = getNumberOfCars(weekDayPassArrivals, weekendPassArrivals);
        addArrivingCars(numberOfCars, CarType.PASS);
    }

    private void carsEntering(Queue<Car> queue) {
        int i = 0;
        // Remove car from the front of the queue and assign to a parking space.
        while(queue.size() > 0 && i < enterSpeed) {
            Location freeLocation = simulationView.getFirstFreeLocation((queue.peek() instanceof ParkingPassCar));
            if(freeLocation != null) {
                Car car = queue.poll();
                simulationView.setCarAt(freeLocation, car);
                i++;
            } else {
                break;
            }
        }
    }

    private void carsReadyToLeave() {
        // Add leaving cars to the payment queue.
        Car car = simulationView.getFirstLeavingCar();
        while (car != null) {
            if (car.getHasToPay()) {
                car.setIsPaying(true);
                paymentCarQueue.add(car);
            } else {
                carLeavesSpot(car);
            }
            car = simulationView.getFirstLeavingCar();
        }
    }

    private void carsPaying() {
        // Let cars pay.
        int i = 0;
        while (paymentCarQueue.size() > 0 && i < paymentSpeed) {
            Car car = paymentCarQueue.poll();
            // TODO Handle payment.
            carLeavesSpot(car);
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

    private int getNumberOfCars(int weekDay, int weekend) {
        Random random = new Random();

        // Get the average number of cars that arrive per hour.
        int averageNumberOfCarsPerHour = day < 5 ? weekDay : weekend;

        // Calculate the number of cars that arrive this minute.
        double standardDeviation = averageNumberOfCarsPerHour * 0.3;
        double numberOfCarsPerHour = averageNumberOfCarsPerHour + random.nextGaussian() * standardDeviation;
        return (int) Math.round(numberOfCarsPerHour / 60);
    }

    private void addArrivingCars(int numberOfCars, CarType type) {
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
        }
    }

    private void carLeavesSpot(Car car) {
        simulationView.removeCarAt(car.getLocation());
        exitCarQueue.add(car);
    }

}
