package parkinggarage.views;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import parkinggarage.controllers.SettingsController;
import parkinggarage.Simulation;
import parkinggarage.models.Car;
import parkinggarage.models.Garage;
import parkinggarage.models.Location;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**
 * The view where the simulation takes place
 */
public class SimulationView extends JFrame implements KeyListener {
    private StatisticsScreen statisticsScreen = null;
    private CarParkView carParkView;

    // private StatisticsScreen statisticsScreen;
    private Simulation simulation;

    public SimulationView(Simulation simulation) {
        this.simulation = simulation;

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
                // TODO: refactor that the view doesn't open a new view, but let a controller do the work
                try {
                    statisticsScreen = new StatisticsScreen(simulation);
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
        public void windowClosing(WindowEvent e) {
            simulation.close();
            if(statisticsScreen != null) {
                Platform.runLater(() -> statisticsScreen.close());
            }
        }

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
        if(statisticsScreen != null) {
            Platform.runLater(() -> statisticsScreen.updateView());
        }
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
            //g.drawRect(0,0,getWidth() -1,getHeight() -1);
        }

        public void updateView() {
            // Create a new car park image if the size has changed.
            if (!size.equals(getSize())) {
                size = getSize();
                carParkImage = createImage(size.width, size.height);
            }

            Garage garage = simulation.getGarage();

            Graphics graphics = carParkImage.getGraphics();
            for (int floor = 0; floor < garage.getNumberOfFloors(); floor++) {
                for (int row = 0; row < garage.getNumberOfRows(); row++) {
                    for (int place = 0; place < garage.getNumberOfPlaces(); place++) {
                        Location location = new Location(floor, row, place);
                        Color color;
                        Car car = garage.getCarAt(location);
                        // Checks if there is a car, if so then the COLOR of that car is given.
                        if (car != null) {
                            color = car.getColor();
                        } else {
                            // Checks if the location is reserved.
                            if (floor == garage.getReservedFloor()) {
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
