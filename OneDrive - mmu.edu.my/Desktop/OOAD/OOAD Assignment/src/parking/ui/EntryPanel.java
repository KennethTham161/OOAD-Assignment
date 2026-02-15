package parking.ui;

import parking.model.ParkingSpot;
import parking.model.Ticket;
import parking.model.Vehicle;
import parking.service.EntryService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntryPanel extends JPanel {

    // --- UI Components ---
    private JTextField txtPlate;            // Input for license plate number
    private JComboBox<String> cmbType;      // Dropdown for vehicle categories
    private JCheckBox chkVip;               // Checkbox for VIP/Reservation status
    private JComboBox<String> cmbSpots;     // Dropdown for spots
    private JButton btnFind;                // Button to search
    private JButton btnPark;                // Button to park
    private JTextArea txtLog;               // Console log
    
    // --- Business Logic & State ---
    private EntryService entryService;      
    private Vehicle currentVehicle;         

    public EntryPanel() {
        this.entryService = new EntryService();
        initComponents();
    }

    /**
     * Initializes the UI layout and components.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Control Area (Input Form) ---
        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        topPanel.add(new JLabel("License Plate:"));
        txtPlate = new JTextField();
        topPanel.add(txtPlate);

        topPanel.add(new JLabel("Vehicle Type:"));
        cmbType = new JComboBox<>(new String[]{"Car", "Motorcycle", "SUV", "Handicapped"});
        topPanel.add(cmbType);

        // --- [NEW] VIP Checkbox UI ---
        topPanel.add(new JLabel("Reservation / VIP:"));
        chkVip = new JCheckBox("Has Valid Reservation");
        topPanel.add(chkVip);
        // -----------------------------

        topPanel.add(new JLabel("Step 1:"));
        btnFind = new JButton("Find Available Spots");
        topPanel.add(btnFind);
        
        topPanel.add(new JLabel("Step 2 (Select Spot):"));
        cmbSpots = new JComboBox<>();
        cmbSpots.setEnabled(false); 
        topPanel.add(cmbSpots);

        topPanel.add(new JLabel("Step 3:"));
        btnPark = new JButton("Confirm Park");
        btnPark.setEnabled(false); 
        topPanel.add(btnPark);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Log Area ---
        txtLog = new JTextArea("Welcome. System Ready.\n");
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        // --- Event Listeners ---
        btnFind.addActionListener(e -> handleFind());
        btnPark.addActionListener(e -> handlePark());
    }

    //Step 1: Create vehicle and find spots
    private void handleFind() {
        try {
            txtLog.setText(""); 
            String plate = txtPlate.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a plate number!");
                return;
            }

            // Get the actual VIP status selected by the user
            boolean isUserClaimingVip = chkVip.isSelected();
            
            txtLog.append("Processing: " + type + " | Plate: " + plate + "\n");
            if (isUserClaimingVip) txtLog.append("Note: Customer claims VIP Reservation.\n");
            
            // Create the vehicle object
            currentVehicle = entryService.createVehicle(plate, type);
            
            if (currentVehicle == null) {
                txtLog.append("Error: Failed to create vehicle.\n");
                return;
            }
            
            // 1. To show Reserved spots in the list, we temporarily force VIP status to true.
            // This ensures the DataCenter includes Reserved spots in the return list.
            currentVehicle.setVip(true);

            txtLog.append("Searching for spots...\n");

            // Fetch spots (includes Reserved spots due to temporary VIP status)
            List<ParkingSpot> spots = entryService.findAvailableSpots(currentVehicle);
            
            // 2. After searching, immediately revert to the user's actual status!
            // This is crucial so that handlePark() can correctly apply fines/logic later.
            currentVehicle.setVip(isUserClaimingVip);
            
            cmbSpots.removeAllItems();
            
            if (spots == null || spots.isEmpty()) {
                txtLog.append("RESULT: No suitable spots found.\n");
                cmbSpots.setEnabled(false);
                btnPark.setEnabled(false);
            } else {
                txtLog.append("RESULT: Found " + spots.size() + " spots.\n");
                
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
            e.printStackTrace(); // Recommended for debugging
        }
    }

    //Step 3: Finalize parking with Logic Check
    private void handlePark() {
        try {
            String selectedItem = (String) cmbSpots.getSelectedItem();
            if (selectedItem == null) return;

            String spotId = selectedItem.split(" ")[0]; 
            
            //  Check for Reserved Spot Violation 
            boolean isSpotReserved = selectedItem.toLowerCase().contains("reserved");
            boolean hasReservation = chkVip.isSelected();

            // Logic: If spot is Reserved AND User does not have Reservation
            if (isSpotReserved && !hasReservation) {
                // Show Warning Dialog
                int choice = JOptionPane.showConfirmDialog(this, 
                        "WARNING: You are selecting a RESERVED spot without a Reservation.\n" +
                        "This is a violation and will incur a FINE upon exit.\n\n" +
                        "Do you want to proceed?", 
                        "Violation Warning", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                
                if (choice == JOptionPane.NO_OPTION) {
                    txtLog.append("Cancelled: User avoided Reserved spot violation.\n");
                    return; // Stop the parking process
                } else {
                    txtLog.append("ALERT: User proceeded with violation!\n");
                    // [IMPORTANT FIXED] Record the violation
                    currentVehicle.setViolation(true); 
                }
            }
            
            txtLog.append("Parking vehicle at " + spotId + "...\n");
            
            Ticket ticket = entryService.parkVehicle(currentVehicle, spotId);
            
            if (ticket != null) {
                // Generate Receipt
                StringBuilder sb = new StringBuilder();
                sb.append("\n=========================================\n");
                sb.append("            OFFICIAL PARKING TICKET       \n");
                sb.append("=========================================\n");
                sb.append(" PARKED AT :  ").append(selectedItem).append("\n"); 
                sb.append("-----------------------------------------\n");
                sb.append(" Plate No  :  ").append(currentVehicle.getLicensePlate()).append("\n");
                sb.append(" Type      :  ").append(currentVehicle.getClass().getSimpleName()).append("\n");
                
                // Show VIP status on ticket if checked
                if (hasReservation) {
                    sb.append(" Status    :  VIP / RESERVED\n");
                }
                // Show Violation status on ticket
                if (currentVehicle.hasViolation()) {
                    sb.append(" Note      :  ** RESERVED VIOLATION **\n");
                }
                
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                sb.append(" Entry Time:  ").append(currentVehicle.getEntryTime().format(fmt)).append("\n");
                sb.append("=========================================\n");
                
                txtLog.append(sb.toString());
                
                JOptionPane.showMessageDialog(this, "Parked Successfully!");
                
                // Reset UI
                btnPark.setEnabled(false);
                cmbSpots.setEnabled(false);
                cmbSpots.removeAllItems();
                txtPlate.setText("");
                chkVip.setSelected(false); // Reset checkbox
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
