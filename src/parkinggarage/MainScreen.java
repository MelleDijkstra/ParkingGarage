package parkinggarage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * The main screen of the application
 * Created by melle on 12-1-2017.
 */
public class MainScreen extends JFrame {

    private JPanel mainPanel;

    private JLabel lblTitle;
    private JButton btnSettings;
    private JButton btnCredits;
    private JSpinner spinner;

    public MainScreen() {
        initializeUI();
        this.setResizable(false);
        this.setTitle("Dashboard");
        this.setSize(500,500);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeUI() {
        BorderLayout borderLayout = new BorderLayout();
        mainPanel = new JPanel(borderLayout);
        mainPanel.setBackground(Color.BLUE);

        // TITLE
        lblTitle = new JLabel("Parking Garage Simulator");
        lblTitle.setFont(lblTitle.getFont().deriveFont(30f));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // SPINNER
        JLabel spinnerLabel = new JLabel("How many times do you want the simulator to run?");
        spinner = new JSpinner(new SpinnerNumberModel(1,1,100,1));
        spinnerLabel.setLabelFor(spinner);
        spinnerLabel.setForeground(Color.WHITE);
        JPanel centerPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        centerPanel.setLayout(new GridBagLayout());
        gbc.gridy = 0;
        centerPanel.add(spinnerLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(spinner, gbc);
        gbc.gridy = 2;
        JButton btnSimulate = new JButton("Simulate");
        centerPanel.add(btnSimulate, gbc);
        centerPanel.setBackground(Color.BLUE);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // BUTTONPANEL
        JPanel buttonPanel = new JPanel();
        btnSettings = new JButton("Settings");
        btnCredits = new JButton("Credits");
        buttonPanel.add(btnSettings);
        buttonPanel.add(btnCredits);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    public int getSpinnerValue() {
        return Integer.parseInt(spinner.getValue().toString());
    }

}
