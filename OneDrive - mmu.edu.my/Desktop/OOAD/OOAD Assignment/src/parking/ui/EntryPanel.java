package parking.ui;

import parking.model.ParkingSpot;
import parking.model.Ticket;
import parking.model.Vehicle;
import parking.service.EntryService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntryPanel extends JPanel {

    // 界面组件
    private JTextField txtPlate;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbSpots;
    private JButton btnFind;
    private JButton btnPark;
    private JTextArea txtLog;
    
    // 逻辑服务
    private EntryService entryService;
    private Vehicle currentVehicle; // 暂存当前车辆

    public EntryPanel() {
        this.entryService = new EntryService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 顶部控制区 ---
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
        cmbSpots.setEnabled(false);
        topPanel.add(cmbSpots);

        topPanel.add(new JLabel("Step 3:"));
        btnPark = new JButton("Confirm Park");
        btnPark.setEnabled(false);
        topPanel.add(btnPark);

        add(topPanel, BorderLayout.NORTH);

        // --- 中间日志区 ---
        txtLog = new JTextArea("Welcome. System Ready.\n");
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        // --- 绑定按钮事件 ---
        btnFind.addActionListener(e -> handleFind());
        btnPark.addActionListener(e -> handlePark());
    }

    private void handleFind() {
        try {
            txtLog.setText(""); 
            String plate = txtPlate.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a plate number!");
                return;
            }

            txtLog.append("Creating vehicle object (" + type + ")...\n");
            
            currentVehicle = entryService.createVehicle(plate, type);
            if (currentVehicle == null) {
                txtLog.append("Error: Failed to create vehicle.\n");
                return;
            }

            txtLog.append("Searching DataCenter for spots...\n");

            List<ParkingSpot> spots = entryService.findAvailableSpots(currentVehicle);
            
            cmbSpots.removeAllItems();
            
            if (spots == null || spots.isEmpty()) {
                txtLog.append("RESULT: No spots found for " + type + ".\n");
                cmbSpots.setEnabled(false);
                btnPark.setEnabled(false);
            } else {
                txtLog.append("RESULT: Found " + spots.size() + " spots.\n");
                
                for (ParkingSpot s : spots) {
                    // 显示 ID 和 类型: F1-R1-S1 (COMPACT)
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

    private void handlePark() {
        try {
            String selectedItem = (String) cmbSpots.getSelectedItem();
            if (selectedItem == null) return;

            // 提取纯 ID
            String spotId = selectedItem.split(" ")[0]; 

            txtLog.append("Parking vehicle at " + spotId + "...\n");
            
            Ticket ticket = entryService.parkVehicle(currentVehicle, spotId);
            
            if (ticket != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n=========================================\n");
                sb.append("           OFFICIAL PARKING TICKET       \n");
                sb.append("=========================================\n");
                
                sb.append(" PARKED AT :  ").append(selectedItem).append("\n"); 
                sb.append("-----------------------------------------\n");
                sb.append(" Plate No  :  ").append(currentVehicle.getLicensePlate()).append("\n");
                
                // --- 关键修复在这里 ---
                // 使用 getClass().getSimpleName() 来获取 "Car", "SUV" 等
                sb.append(" Type      :  ").append(currentVehicle.getClass().getSimpleName()).append("\n");
                // ---------------------
                
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                sb.append(" Entry Time:  ").append(currentVehicle.getEntryTime().format(fmt)).append("\n");
                
                sb.append("=========================================\n");
                
                txtLog.append(sb.toString());
                
                JOptionPane.showMessageDialog(this, "Parked Successfully!");
                
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