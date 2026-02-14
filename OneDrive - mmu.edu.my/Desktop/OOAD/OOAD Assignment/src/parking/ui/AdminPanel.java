package parking.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import parking.data.DataCenter;
import parking.strategy.*;
import parking.model.*;

/**
 * AdminPanel - Member 5's implementation (Enhanced with Time Simulation)
 * Allows admin to configure system settings, primarily the fine scheme.
 * Includes live clock and time simulation for testing.
 */
public class AdminPanel extends JPanel {

    // UI Components for fine scheme selection
    private JRadioButton rbFixed;
    private JRadioButton rbProgressive;
    private JRadioButton rbHourly;
    private ButtonGroup fineSchemeGroup;
    private JButton btnApplyScheme;
    
    // Status display components
    private JLabel lblCurrentScheme;
    private JLabel lblTotalSpots;
    private JLabel lblOccupied;
    private JLabel lblAvailable;
    private JLabel lblOccupancyRate;
    private JButton btnRefresh;
    
    // Time simulation components
    private JLabel lblCurrentTime;
    private JLabel lblCurrentDate;
    private JLabel lblSimulationOffset;
    private JButton btnSkipHour;
    private JButton btnSkipDay;
    private JButton btnResetTime;
    private Timer clockTimer;
    
    // Time simulation offset (in hours)
    private static int timeOffsetHours = 0;
    
    // Date formatters
    private static final DateTimeFormatter CLOCK_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy");

    public AdminPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Top: Title and Clock
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Center: Main content in scroll pane
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Start the clock timer
        startClock();
        
        // Initialize display
        refreshDisplay();
    }

    /**
     * Creates the title panel with clock
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("ADMIN CONTROL PANEL", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(title, BorderLayout.CENTER);
        
        // Clock panel
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
        
        lblCurrentTime = new JLabel("00:00:00");
        lblCurrentTime.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblCurrentTime.setForeground(new Color(0, 100, 200));
        lblCurrentTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        lblCurrentDate = new JLabel(LocalDateTime.now().format(DATE_FORMATTER));
        lblCurrentDate.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCurrentDate.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        clockPanel.add(lblCurrentTime);
        clockPanel.add(lblCurrentDate);
        panel.add(clockPanel, BorderLayout.EAST);
        
        return panel;
    }

    /**
     * Creates the main content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Add sections
        panel.add(createTimeSimulationPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createFineSchemePanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createSystemStatusPanel());
        
        return panel;
    }

    /**
     * Creates the time simulation panel
     */
    private JPanel createTimeSimulationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 100, 0), 2),
            "Time Simulation (Testing Only)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        // Info text
        JTextArea info = new JTextArea(
            "Simulate time progression to test fine calculations.\n" +
            "This will modify entry times of all currently parked vehicles.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("Arial", Font.PLAIN, 11));
        info.setForeground(Color.GRAY);
        info.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.add(info, BorderLayout.NORTH);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        btnSkipHour = new JButton("Skip +1 Hour");
        btnSkipHour.setFont(new Font("Arial", Font.BOLD, 12));
        btnSkipHour.setPreferredSize(new Dimension(140, 35));
        btnSkipHour.addActionListener(e -> skipTime(1));
        
        btnSkipDay = new JButton("Skip +1 Day");
        btnSkipDay.setFont(new Font("Arial", Font.BOLD, 12));
        btnSkipDay.setPreferredSize(new Dimension(140, 35));
        btnSkipDay.addActionListener(e -> skipTime(24));
        
        btnResetTime = new JButton("Reset Time");
        btnResetTime.setFont(new Font("Arial", Font.PLAIN, 11));
        btnResetTime.setPreferredSize(new Dimension(120, 35));
        btnResetTime.addActionListener(e -> resetTime());
        
        buttonPanel.add(btnSkipHour);
        buttonPanel.add(btnSkipDay);
        buttonPanel.add(btnResetTime);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        // Offset display
        JPanel offsetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        offsetPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
        offsetPanel.add(new JLabel("Time Offset: "));
        lblSimulationOffset = new JLabel("0 hours (Real Time)");
        lblSimulationOffset.setFont(new Font("Arial", Font.BOLD, 12));
        lblSimulationOffset.setForeground(new Color(0, 120, 0));
        offsetPanel.add(lblSimulationOffset);
        panel.add(offsetPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates the fine scheme selection panel
     */
    private JPanel createFineSchemePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Fine Scheme Configuration",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        
        // Description
        JLabel desc = new JLabel("Select the fine calculation method for overstaying vehicles:");
        desc.setFont(new Font("Arial", Font.PLAIN, 12));
        desc.setBorder(new EmptyBorder(10, 15, 5, 15));
        panel.add(desc, BorderLayout.NORTH);
        
        // Radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(3, 1, 5, 8));
        radioPanel.setBorder(new EmptyBorder(5, 25, 10, 15));
        
        rbFixed = new JRadioButton("Fixed Fine Scheme (RM 50 flat for any overstay)");
        rbProgressive = new JRadioButton("Progressive Fine Scheme (RM 50-200 based on duration)");
        rbHourly = new JRadioButton("Hourly Fine Scheme (RM 20 per hour after 24 hours)");
        
        rbFixed.setFont(new Font("Arial", Font.PLAIN, 12));
        rbProgressive.setFont(new Font("Arial", Font.PLAIN, 12));
        rbHourly.setFont(new Font("Arial", Font.PLAIN, 12));
        
        fineSchemeGroup = new ButtonGroup();
        fineSchemeGroup.add(rbFixed);
        fineSchemeGroup.add(rbProgressive);
        fineSchemeGroup.add(rbHourly);
        
        radioPanel.add(rbFixed);
        radioPanel.add(rbProgressive);
        radioPanel.add(rbHourly);
        panel.add(radioPanel, BorderLayout.CENTER);
        
        // Bottom: current scheme and apply button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        
        lblCurrentScheme = new JLabel("Current: Fixed RM 50 Scheme");
        lblCurrentScheme.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentScheme.setForeground(new Color(0, 120, 0));
        
        btnApplyScheme = new JButton("Apply Fine Scheme");
        btnApplyScheme.setFont(new Font("Arial", Font.BOLD, 12));
        btnApplyScheme.setPreferredSize(new Dimension(170, 35));
        btnApplyScheme.addActionListener(e -> applyFineScheme());
        
        bottomPanel.add(lblCurrentScheme, BorderLayout.WEST);
        bottomPanel.add(btnApplyScheme, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates the system status display panel
     */
    private JPanel createSystemStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Current System Status",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Grid for stats
        JPanel grid = new JPanel(new GridLayout(4, 2, 25, 5));
        grid.setBorder(new EmptyBorder(20, 25, 15, 25));
        
        Font labelFont = new Font("Arial", Font.PLAIN, 13);
        Font valueFont = new Font("Arial", Font.BOLD, 14);
        
        // Total Spots
        JLabel lbl1 = new JLabel("Total Parking Spots:");
        lbl1.setFont(labelFont);
        grid.add(lbl1);
        lblTotalSpots = new JLabel("0");
        lblTotalSpots.setFont(valueFont);
        grid.add(lblTotalSpots);
        
        // Occupied
        JLabel lbl2 = new JLabel("Occupied Spots:");
        lbl2.setFont(labelFont);
        grid.add(lbl2);
        lblOccupied = new JLabel("0");
        lblOccupied.setFont(valueFont);
        lblOccupied.setForeground(new Color(200, 100, 0));
        grid.add(lblOccupied);
        
        // Available
        JLabel lbl3 = new JLabel("Available Spots:");
        lbl3.setFont(labelFont);
        grid.add(lbl3);
        lblAvailable = new JLabel("0");
        lblAvailable.setFont(valueFont);
        lblAvailable.setForeground(new Color(0, 150, 0));
        grid.add(lblAvailable);
        
        // Occupancy Rate
        JLabel lbl4 = new JLabel("Occupancy Rate:");
        lbl4.setFont(labelFont);
        grid.add(lbl4);
        lblOccupancyRate = new JLabel("0.0%");
        lblOccupancyRate.setFont(new Font("Arial", Font.BOLD, 16));
        lblOccupancyRate.setForeground(new Color(0, 100, 200));
        grid.add(lblOccupancyRate);
        
        panel.add(grid, BorderLayout.CENTER);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 15));
        btnRefresh = new JButton("Refresh Status");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setPreferredSize(new Dimension(150, 35));
        btnRefresh.addActionListener(e -> refreshDisplay());
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Starts the live clock timer
     */
    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    /**
     * Updates the clock display
     */
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        lblCurrentTime.setText(now.format(CLOCK_FORMATTER));
        lblCurrentDate.setText(now.format(DATE_FORMATTER));
    }

    /**
     * Skips time forward by specified hours (for testing)
     */
    private void skipTime(int hours) {
        var parkedVehicles = DataCenter.getAllParkedVehicles();
        
        if (parkedVehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No vehicles are currently parked.\nPark some vehicles first to test time simulation.",
                "No Vehicles",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String timeWord = hours == 1 ? "hour" : "hours";
        String vehicleWord = parkedVehicles.size() == 1 ? "vehicle" : "vehicles";
        
        String message = String.format(
            "Skip forward %d %s?\n\n" +
            "This will:\n" +
            "- Subtract %d %s from entry times of %d parked %s\n" +
            "- Simulate vehicles staying longer\n" +
            "- Help test fine calculations\n\n" +
            "Note: This is for TESTING only!",
            hours, timeWord, hours, timeWord, parkedVehicles.size(), vehicleWord
        );
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Time Skip",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Modify entry times
            for (Vehicle vehicle : parkedVehicles) {
                if (vehicle.getEntryTime() != null) {
                    LocalDateTime newEntryTime = vehicle.getEntryTime().minusHours(hours);
                    vehicle.setEntryTime(newEntryTime);
                }
            }
            
            timeOffsetHours += hours;
            updateOffsetDisplay();
            
            JOptionPane.showMessageDialog(this,
                String.format("Time skipped forward %d %s!\n\n" +
                             "All parked vehicles now appear to have been parked %d %s longer.",
                             hours, timeWord, hours, timeWord),
                "Time Skip Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshDisplay();
        }
    }

    /**
     * Resets the time simulation
     */
    private void resetTime() {
        if (timeOffsetHours == 0) {
            JOptionPane.showMessageDialog(this,
                "Time offset is already at 0 (Real Time).",
                "Already Reset",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset time simulation?\n\n" +
            "WARNING: This will reset entry times to real time.\n" +
            "If vehicles have already exited, their data cannot be restored.",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            var parkedVehicles = DataCenter.getAllParkedVehicles();
            
            for (Vehicle vehicle : parkedVehicles) {
                if (vehicle.getEntryTime() != null) {
                    LocalDateTime realEntryTime = vehicle.getEntryTime().plusHours(timeOffsetHours);
                    vehicle.setEntryTime(realEntryTime);
                }
            }
            
            timeOffsetHours = 0;
            updateOffsetDisplay();
            
            JOptionPane.showMessageDialog(this,
                "Time simulation reset to real time!",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshDisplay();
        }
    }

    /**
     * Updates the offset display label
     */
    private void updateOffsetDisplay() {
        if (timeOffsetHours == 0) {
            lblSimulationOffset.setText("0 hours (Real Time)");
            lblSimulationOffset.setForeground(new Color(0, 120, 0));
        } else {
            lblSimulationOffset.setText(String.format("+%d hours (Simulated)", timeOffsetHours));
            lblSimulationOffset.setForeground(new Color(200, 100, 0));
        }
    }

    /**
     * Applies the selected fine scheme
     */
    private void applyFineScheme() {
        FineStrategy newStrategy = null;
        String schemeName = "";
        
        if (rbFixed.isSelected()) {
            newStrategy = new FixedFineStrategy();
            schemeName = "Fixed Fine Scheme";
        } else if (rbProgressive.isSelected()) {
            newStrategy = new ProgressiveFineStrategy();
            schemeName = "Progressive Fine Scheme";
        } else if (rbHourly.isSelected()) {
            newStrategy = new HourlyFineStrategy();
            schemeName = "Hourly Fine Scheme";
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a fine scheme first.",
                "No Scheme Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int parkedCount = DataCenter.getAllParkedVehicles().size();
        String vehicleWord = parkedCount == 1 ? "vehicle" : "vehicles";
        
        String message;
        if (parkedCount > 0) {
            message = String.format(
                "There are currently %d %s parked.\n\n" +
                "The new fine scheme will apply to:\n" +
                "- All FUTURE vehicle entries\n" +
                "- Current vehicles when they exit (if they overstay)\n\n" +
                "Change to: %s?",
                parkedCount, vehicleWord, schemeName);
        } else {
            message = String.format(
                "Change fine scheme to: %s?\n\n" +
                "This will apply to all future vehicle entries.",
                schemeName);
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Fine Scheme Change",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            DataCenter.setActiveFineStrategy(newStrategy);
            refreshDisplay();
            
            JOptionPane.showMessageDialog(this,
                "Fine scheme successfully changed to:\n" + schemeName,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Refreshes all displayed data
     */
    private void refreshDisplay() {
        // Update fine scheme display
        String currentScheme = DataCenter.getActiveFineSchemeName();
        lblCurrentScheme.setText("Current: " + currentScheme);
        
        // Select the appropriate radio button
        if (currentScheme.contains("Fixed")) {
            rbFixed.setSelected(true);
        } else if (currentScheme.contains("Progressive")) {
            rbProgressive.setSelected(true);
        } else if (currentScheme.contains("Hourly")) {
            rbHourly.setSelected(true);
        }
        
        // Update system status
        int total = DataCenter.getTotalSpots();
        int occupied = DataCenter.getTotalOccupied();
        int available = total - occupied;
        double occupancyRate = DataCenter.getOccupancyRate();
        
        lblTotalSpots.setText(String.valueOf(total));
        lblOccupied.setText(String.valueOf(occupied));
        lblAvailable.setText(String.valueOf(available));
        lblOccupancyRate.setText(String.format("%.1f%%", occupancyRate));
        
        // Update occupancy rate color
        if (occupancyRate < 50) {
            lblOccupancyRate.setForeground(new Color(0, 150, 0));
        } else if (occupancyRate < 80) {
            lblOccupancyRate.setForeground(new Color(200, 100, 0));
        } else {
            lblOccupancyRate.setForeground(new Color(200, 0, 0));
        }
        
        // Update offset display
        updateOffsetDisplay();
    }

    /**
     * Public method to refresh the panel
     */
    public void refreshPanel() {
        refreshDisplay();
    }
    
    /**
     * Cleanup when panel is removed
     */
    public void cleanup() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
    }
}