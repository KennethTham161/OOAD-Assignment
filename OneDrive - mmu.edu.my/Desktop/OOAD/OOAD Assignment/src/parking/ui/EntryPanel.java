package parking.ui;

import parking.model.ParkingSpot;
import parking.model.Ticket;
import parking.model.Vehicle;
import parking.service.EntryService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * EntryPanel provides the User Interface for the parking entrance.
 * It handles vehicle identification, spot searching, and the parking process.
 */
public class EntryPanel extends JPanel {

    // --- UI Components ---
    private JTextField txtPlate;            // Input for license plate number
    private JComboBox<String> cmbType;      // Dropdown for vehicle categories (Car, SUV, etc.)
    private JComboBox<String> cmbSpots;     // Dropdown to display and select available parking spots
    private JButton btnFind;                // Button to trigger search for available spots
    private JButton btnPark;                // Button to finalize the parking action
    private JTextArea txtLog;               // Console-like area for status messages and ticket printing
    
    // --- Business Logic & State ---
    private EntryService entryService;      // Service layer handling the core parking logic
    private Vehicle currentVehicle;         // Temporary storage for the vehicle currently being processed

    public EntryPanel() {
        this.entryService = new EntryService();
        initComponents();
    }

    /**
     * Initializes the UI layout and components.
     */
    private void initComponents() {
        // Use BorderLayout for the main panel with 10px gaps
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Control Area (Input Form) ---
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        topPanel.add(new JLabel("License Plate:"));
        txtPlate = new JTextField();
        topPanel.add(txtPlate);

        topPanel.add(new JLabel("Vehicle Type:"));
        cmbType = new JComboBox<>(new String[]{"Car", "Motorcycle", "SUV", "Handicapped"});
        topPanel.add(cmbType);

        topPanel.add(new JLabel("Step 1:"));
        btnFind = new JButton("Find Available Spots");
        topPanel.add(btnFind);
        
        topPanel.add(new JLabel("Step 2 (Select Spot):"));
        cmbSpots = new JComboBox<>();
        cmbSpots.setEnabled(false); // Disabled until spots are found
        topPanel.add(cmbSpots);

        topPanel.add(new JLabel("Step 3:"));
        btnPark = new JButton("Confirm Park");
        btnPark.setEnabled(false); // Disabled until a spot is selected
        topPanel.add(btnPark);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Log Area (System Feedback) ---
        txtLog = new JTextArea("Welcome. System Ready.\n");
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced font for aligned ticket printing
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        // --- Event Listeners ---
        btnFind.addActionListener(e -> handleFind());
        btnPark.addActionListener(e -> handlePark());
    }

    /**
     * Handles the logic for Step 1: Validating input and searching for compatible spots.
     */
    private void handleFind() {
        try {
            txtLog.setText(""); // Clear previous logs
            String plate = txtPlate.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            
            // Validate input
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a plate number!");
                return;
            }

            txtLog.append("Creating vehicle object (" + type + ")...\n");
            
            // Request service to instantiate the specific vehicle type
            currentVehicle = entryService.createVehicle(plate, type);
            if (currentVehicle == null) {
                txtLog.append("Error: Failed to create vehicle.\n");
                return;
            }

            txtLog.append("Searching DataCenter for spots...\n");

            // Fetch available spots compatible with the vehicle type
            List<ParkingSpot> spots = entryService.findAvailableSpots(currentVehicle);
            
            cmbSpots.removeAllItems();
            
            if (spots == null || spots.isEmpty()) {
                txtLog.append("RESULT: No spots found for " + type + ".\n");
                cmbSpots.setEnabled(false);
                btnPark.setEnabled(false);
            } else {
                txtLog.append("RESULT: Found " + spots.size() + " spots.\n");
                
                // Populate the dropdown with found spots
                for (ParkingSpot s : spots) {
                    String displayText = s.getSpotId() + " (" + s.getType() + ")";
                    cmbSpots.addItem(displayText);
                }
                
                cmbSpots.setEnabled(true);
                btnPark.setEnabled(true);
            }
        } catch (Exception e) {
            txtLog.append("\n!!! CRITICAL ERROR !!!\n");
            txtLog.append(e.toString() + "\n");
        }
    }

    /**
     * Handles the logic for Step 3: Finalizing the parking transaction and printing the ticket.
     */
    private void handlePark() {
        try {
            String selectedItem = (String) cmbSpots.getSelectedItem();
            if (selectedItem == null) return;

            // Extract the Spot ID from the display string (e.g., "A1 (Car)" -> "A1")
            String spotId = selectedItem.split(" ")[0]; 

            txtLog.append("Parking vehicle at " + spotId + "...\n");
            
            // Execute parking logic via service
            Ticket ticket = entryService.parkVehicle(currentVehicle, spotId);
            
            if (ticket != null) {
                // Generate and display the ASCII receipt
                StringBuilder sb = new StringBuilder();
                sb.append("\n=========================================\n");
                sb.append("            OFFICIAL PARKING TICKET       \n");
                sb.append("=========================================\n");
                
                sb.append(" PARKED AT :  ").append(selectedItem).append("\n"); 
                sb.append("-----------------------------------------\n");
                sb.append(" Plate No  :  ").append(currentVehicle.getLicensePlate()).append("\n");
                
                // Reflection used here to show the specific Subclass name (e.g., Car, SUV)
                sb.append(" Type      :  ").append(currentVehicle.getClass().getSimpleName()).append("\n");
                
                // Format the entry timestamp
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                sb.append(" Entry Time:  ").append(currentVehicle.getEntryTime().format(fmt)).append("\n");
                
                sb.append("=========================================\n");
                
                txtLog.append(sb.toString());
                
                JOptionPane.showMessageDialog(this, "Parked Successfully!");
                
                // Reset the UI for the next vehicle
                btnPark.setEnabled(false);
                cmbSpots.setEnabled(false);
                cmbSpots.removeAllItems();
                currentVehicle = null;
            } else {
                txtLog.append("Error: Spot occupation failed.\n");
            }
        } catch (Exception e) {
            txtLog.append("Error during parking: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}