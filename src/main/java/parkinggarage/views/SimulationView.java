package parkinggarage.views;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import parkinggarage.Simulation;
import parkinggarage.controllers.SettingsController;
import parkinggarage.model.Location;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The view where the simulation takes place
 */


public class SimulationView extends JFrame implements KeyListener {
    private StatisticsScreen statisticsScreen;
    private CarParkView carParkView;

    // private StatisticsScreen statisticsScreen;
    private Simulation simulation;


    public SimulationView(Simulation simulation) {
        this.simulation = simulation;

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        carParkView = new CarParkView();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.add(carParkView, BorderLayout.CENTER);

        Container rightPane = getContentPane();
        rightPane.setLayout(new BorderLayout(5,5));
        rightPane.add(carParkView, BorderLayout.LINE_START);

        JPanel sliderPane = new JPanel();
        sliderPane.setLayout(new BoxLayout(sliderPane, BoxLayout.Y_AXIS));
        sliderPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 50));

        /*
         * Make multiple JSliders for settings
         * tickSlider is the slider for the amount of ticks
         * carsSLider is the slider fot the amount of cars entering
         */

        Map<String, JSlider> sliders = new LinkedHashMap<String, JSlider>() {{
            put("Adhoc slider",                 new JSlider(0,100));
            put("ParkingPassCar slider",        new JSlider(0,100));
            put("ReservedCar slider",           new JSlider(0,100));
            put("Adhoc weekend slider",         new JSlider(0,100));
            put("ParkingPass weekend slider",   new JSlider(0,100));
            put("Reserved weekend slider",      new JSlider(0,100));
        }};

        int j = 0;

        for(Map.Entry<String, JSlider> entry : sliders.entrySet()) {
            JSlider slider = entry.getValue();
            // Generate label
            JLabel label = new JLabel(entry.getKey());
            // Initialize JSlider
            int finalJ = j;
            slider.addChangeListener(e -> {
                switch(finalJ) {
                    case 0:
                        simulation.setWeekDayArrivals(slider.getValue());
                        break;
                    case 1:
                        simulation.setWeekDayPassArrivals(slider.getValue());
                        break;
                    case 2:
                        simulation.setWeekDayReservedArrivals(slider.getValue());
                        break;
                    case 3:
                        simulation.setWeekendArrivals(slider.getValue());
                        break;
                    case 4:
                        simulation.setWeekendPassArrivals(slider.getValue());
                        break;
                    case 5:
                        simulation.setWeekendReservedArrivals(slider.getValue());
                        break;
                }
            });
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setLabelTable(slider.createStandardLabels(50));

            sliderPane.add(label);
            sliderPane.add(entry.getValue());
            j++;
        }


        JSlider tickSlider = new JSlider(100,1000,100);
        tickSlider.setMinorTickSpacing(1);
        tickSlider.setMajorTickSpacing(5);
        tickSlider.setPaintTicks(true);
        tickSlider.setPaintLabels(true);
        tickSlider.setLabelTable(tickSlider.createStandardLabels(50));
        tickSlider.addChangeListener(tickChangeListener);

        contentPane.add(tickSlider, BorderLayout.SOUTH);
        rightPane.add(sliderPane);



        JButton btnStatistics = new JButton("Statistics");
        Platform.runLater(() -> {
            try {
                statisticsScreen = new StatisticsScreen(simulation);
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
        btnStatistics.addActionListener(e -> {
            // Open statistics screen
            // this is needed to open a JavaFX window in swing (it should be opened on JavaFX thread)
            Platform.runLater(() -> {
                // TODO: refactor that the view doesn't open a new view, but let a controller do the work
                statisticsScreen.show();
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
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            simulation.close();
            if (statisticsScreen != null) {
                Platform.runLater(() -> statisticsScreen.close());
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    };

    private ChangeListener tickChangeListener = e -> {
        JSlider slider = (JSlider)e.getSource();
        simulation.setTickPause(slider.getValue());
    };

    public void updateView() {
        carParkView.updateView();
        if (statisticsScreen != null) {
            Platform.runLater(() -> statisticsScreen.updateView());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

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
    public void keyReleased(KeyEvent e) {
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

            // Draw the garage
            Dimension currentSize = getSize();
            if (size.equals(currentSize)) {
                g.drawImage(carParkImage, 0, 0, null);
            } else {
                // Rescale the previous image.
                g.drawImage(carParkImage, 0, 0, currentSize.width, currentSize.height, null);
            }
            // Draw the current date
            drawDate(g);
            g.drawString("Iteration: " + simulation.getCurrentIteration(), 90, 20);
            // Draw the queues
            drawQueues(g);
        }

        public void updateView() {
            // Create a new car park image if the size has changed.
            if (!size.equals(getSize())) {
                size = getSize();
                carParkImage = createImage(size.width, size.height);
            }

            Location[][][] locations = simulation.getGarage().getLocations();

            Graphics graphics = carParkImage.getGraphics();
            for (int f = 0; f < locations.length; f++) {
                for (int r = 0; r < locations[f].length; r++) {
                    for (int p = 0; p < locations[f][r].length; p++) {
                        Color color;
                        Location location = locations[f][r][p];
                        // Checks if there is a car, if so then the COLOR of that car is given.
                        if (location.getCar() != null) {
                            color = location.getCar().getColor();
                        } else {
                            // Checks if the location is reserved.
                            if (location.isReserved()) {
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
         *
         * @param graphics Graphics object
         */
        private void drawDate(Graphics graphics) {
            int[] time = simulation.getDate();
            String day = SettingsController.numToDay(time[0]);
            if (day == null) day = "" + time[0];
            String hour = (time[1] < 10) ? "0" + time[1] : Integer.toString(time[1]);
            String minute = (time[2] < 10) ? "0" + time[2] : Integer.toString(time[2]);
            graphics.drawString(day, 30, 15);
            graphics.drawString(hour + ":" + minute, 30, 30);
        }

        /**
         * Draws the different queues of the garage
         */
        private void drawQueues(Graphics g) {
            HashMap<String, Integer> queueStats = simulation.getGarage().getQueueStats();
            int i = 1;
            for (Map.Entry<String, Integer> item : queueStats.entrySet()) {
                g.drawString(item.getKey() + ": " + Integer.toString(item.getValue()), 20, getHeight() - (20 * i));
                i++;
            }
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
                    8); // TODO use dynamic size or constants
        }
    }

}
