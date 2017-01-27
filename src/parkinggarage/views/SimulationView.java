package parkinggarage.views;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import parkinggarage.controllers.SettingsController;
import parkinggarage.Simulation;
import parkinggarage.models.Car;
import parkinggarage.models.Location;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class SimulationView extends JFrame implements KeyListener {
    private CarParkView carParkView;

    // private StatisticsScreen statisticsScreen;

    private int numberOfFloors;
    private int numberOfRows;

    private int numberOfPlaces;
    public int reservedFloor;

    private int numberOfOpenSpots;

    private Car[][][] cars;
    private Simulation simulation;

    public SimulationView(Simulation simulation, int numberOfFloors, int numberOfRows, int reservedFloor, int numberOfPlaces) {
        this.simulation = simulation;
        this.numberOfFloors = numberOfFloors;
        this.numberOfRows = numberOfRows;
        this.reservedFloor = reservedFloor;
        this.numberOfPlaces = numberOfPlaces;
        this.numberOfOpenSpots = numberOfFloors * numberOfRows * numberOfPlaces;
        cars = new Car[numberOfFloors][numberOfRows][numberOfPlaces];

        carParkView = new CarParkView();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.add(carParkView, BorderLayout.CENTER);

        //JPanel bottomPanel = new JPanel(new SpringLayout());

        JSlider slider = new JSlider(100,1000,100);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setLabelTable(slider.createStandardLabels(50));
        slider.addChangeListener(changeListener);
        contentPane.add(slider, BorderLayout.SOUTH);
        //bottomPanel.add(slider);

        JButton btnStatistics = new JButton("Statistics");
        btnStatistics.addActionListener(e -> {
            // Open statistics screen
            // this is needed to open a JavaFX window in swing (it should be opened on JavaFX thread)
            Platform.runLater(() -> {
                try {
                    StatisticsScreen statisticsScreen = new StatisticsScreen();
                    statisticsScreen.show();
                } catch (NullPointerException i) {
                    System.out.println("Statistics file not found");
                    new Alert(Alert.AlertType.ERROR, "Layout file not found").show();
                    i.printStackTrace();
                } catch (IOException i) {
                    System.out.println("Something went wrong");
                    new Alert(Alert.AlertType.ERROR, "FXML not valid").show();
                    i.printStackTrace();
                }
            });
        });
        contentPane.add(btnStatistics, BorderLayout.NORTH);
        //bottomPanel.add(btnStatistics);

        //contentPane.add(bottomPanel, BorderLayout.SOUTH);

        pack();

        this.addKeyListener(this);
        this.addWindowListener(windowListener);

        setVisible(true);
        updateView();
    }

    private WindowListener windowListener = new WindowListener() {
        @Override
        public void windowOpened(WindowEvent e) {}

        @Override
        public void windowClosing(WindowEvent e) { simulation.close(); }

        @Override
        public void windowClosed(WindowEvent e) {}

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowActivated(WindowEvent e) {}

        @Override
        public void windowDeactivated(WindowEvent e) {}
    };

    private ChangeListener changeListener = e -> {
        JSlider slider = (JSlider)e.getSource();
        simulation.setTickPause(slider.getValue());
    };

    public void updateView() {
        carParkView.updateView();
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfPlaces() {
        return numberOfPlaces;
    }

    public int getNumberOfOpenSpots() {
        return numberOfOpenSpots;
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
            numberOfOpenSpots--;
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
        numberOfOpenSpots++;
        return car;
    }

    /**
     * Returns first free location in garage otherwise null
     * @return The first free location
     * @param includeReservedSpace boolean if reserved space should be included
     */
    @Nullable
    public Location getFirstFreeLocation(boolean includeReservedSpace) {
        // Check if we need to include reserved space
        if(includeReservedSpace) {
            for(int row = 0; row < getNumberOfRows(); row++) {
                for(int place = 0; place < getNumberOfPlaces(); place++) {
                    Location location = new Location(this.reservedFloor,row,place);
                    // check all rows and places of the reserved space for a free spot (car == null)
                    if(getCarAt(location) == null) {
                        return location;
                    }
                }
            }
        }
        for (int floor = 0; floor < getNumberOfFloors(); floor++) {
            // We have already been through the reserved space
            if(floor == this.reservedFloor) continue;
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

    /**
     * Check if given location is an actual location in the garage
     * @param location the location to check
     * @return true if it is a location in garage otherwise false
     */
    private boolean locationIsValid(Location location) {
        int floor = location.getFloor();
        int row = location.getRow();
        int place = location.getPlace();
        return !(floor < 0 || floor >= numberOfFloors || row < 0 || row > numberOfRows || place < 0 || place > numberOfPlaces);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("pressed");
        // Invoked when a key has been pressed.
        if (e.getKeyCode() == KeyEvent.VK_P) {
            System.out.println("P key pressed");
            simulation.toggle();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public void setReservedFloor(int reservedFloor) {
        this.reservedFloor = reservedFloor;
    }

    private class CarParkView extends JPanel {

        private Dimension size;
        private Image carParkImage;

        /**
         * Constructor for objects of class CarPark
         */
        public CarParkView() {
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 500);
        }

        /**
         * The car park view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        @Override
        public void paintComponent(Graphics g) {
            if (carParkImage == null) {
                return;
            }

            Dimension currentSize = getSize();
            if (size.equals(currentSize)) {
                g.drawImage(carParkImage, 0, 0, null);
            } else {
                // Rescale the previous image.
                g.drawImage(carParkImage, 0, 0, currentSize.width, currentSize.height, null);
            }
            drawDate(g);
        }

        public void updateView() {
            // Create a new car park image if the size has changed.
            if (!size.equals(getSize())) {
                size = getSize();
                carParkImage = createImage(size.width, size.height);
            }

            Graphics graphics = carParkImage.getGraphics();
            for (int floor = 0; floor < getNumberOfFloors(); floor++) {
                for (int row = 0; row < getNumberOfRows(); row++) {
                    for (int place = 0; place < getNumberOfPlaces(); place++) {
                        Location location = new Location(floor, row, place);
                        Color color;
                        Car car = getCarAt(location);
                        // Checks if there is a car, if so then the COLOR of that car is given.
                        if (car != null) {
                            color = car.getColor();
                        }
                        else {
                            // Checks if the location is reserved.
                            if (floor == SimulationView.this.reservedFloor) {
                                color = Color.CYAN;
                            } else {
                                color = Color.WHITE;
                            }

                        }

                        drawPlace(graphics, location, color);
                    }
                }
            }

            repaint();
        }

        /**
         * Draws the time and day of the simulation
         * @param graphics Graphics object
         */
        private void drawDate(Graphics graphics) {
            int[] time = simulation.getDate();
            String day = SettingsController.numToDay(time[0]);
            if(day == null) day = ""+time[0];
            String hour = (time[1] < 10) ? "0"+time[1] : Integer.toString(time[1]);
            String minute = (time[2] < 10) ? "0"+time[2] : Integer.toString(time[2]);
            graphics.drawString(day, 30,15);
            graphics.drawString(hour+":"+minute, 30, 30);
        }

        /**
         * Paint a place on this car park view in a given COLOR.
         */
        private void drawPlace(Graphics graphics, Location location, Color color) {
            graphics.setColor(color);
            graphics.fillRect(
                    location.getFloor() * 260 + (1 + (int) Math.floor(location.getRow() * 0.5)) * 75 + (location.getRow() % 2) * 20,
                    60 + location.getPlace() * 10,
                    20 - 1,
                    10 - 1); // TODO use dynamic size or constants
        }
    }

}
