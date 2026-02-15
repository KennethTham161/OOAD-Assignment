package parking.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import parking.service.ReportService;

/**
 * ReportingPanel - Member 5's implementation (Enhanced Version)
 * Displays comprehensive reports about parking lot operations.
 */
public class ReportingPanel extends JPanel {

    // UI Components
    private JLabel lblOccupancyRate;
    private JTextArea txtOccupancyDetails;
    private JLabel lblTotalRevenue;
    private JLabel lblParkingFees;
    private JLabel lblFinesCollected;
    private JTable tblCurrentVehicles;
    private DefaultTableModel vehicleTableModel;
    private JLabel lblUnpaidFinesCount;
    private JLabel lblUnpaidFinesTotal;
    private JLabel lblViolationCount;
    private JLabel lblTotalPayments;
    private JButton btnRefreshAll;
    
    // Report generation service
    private ReportService reportService = new ReportService();

    public ReportingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title panel at top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel title = new JLabel("PARKING LOT REPORTS", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        titlePanel.add(title, BorderLayout.CENTER);
        
        btnRefreshAll = new JButton("Refresh All Reports");
        btnRefreshAll.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefreshAll.setPreferredSize(new Dimension(180, 35));
        btnRefreshAll.addActionListener(e -> refreshAllReports());
        titlePanel.add(btnRefreshAll, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content - using vertical layout for better organization
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Top row: Occupancy and Revenue (side by side)
        JPanel topRow = new JPanel(new GridLayout(1, 2, 15, 0));
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        topRow.add(createOccupancyReportPanel());
        topRow.add(createRevenueReportPanel());
        contentPanel.add(topRow);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Middle row: Current Vehicles table (full width)
        JPanel vehiclePanel = createCurrentVehiclesPanel();
        vehiclePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        contentPanel.add(vehiclePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Bottom row: Fines and Violations (side by side)
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        bottomRow.add(createUnpaidFinesPanel());
        bottomRow.add(createViolationsPanel());
        contentPanel.add(bottomRow);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initial data load
        refreshAllReports();
    }

    /**
     * Creates the occupancy report panel
     */
    private JPanel createOccupancyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), 
                           "Occupancy Report",
                           TitledBorder.LEFT,
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Overall occupancy at top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("Overall: "));
        lblOccupancyRate = new JLabel("0.0%");
        lblOccupancyRate.setFont(new Font("Arial", Font.BOLD, 18));
        lblOccupancyRate.setForeground(new Color(0, 120, 0));
        topPanel.add(lblOccupancyRate);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Detailed breakdown
        txtOccupancyDetails = new JTextArea();
        txtOccupancyDetails.setEditable(false);
        txtOccupancyDetails.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtOccupancyDetails.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(txtOccupancyDetails);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the revenue report panel
     */
    private JPanel createRevenueReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), 
                           "Revenue Report",
                           TitledBorder.LEFT,
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 10, 12));
        
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font valueFont = new Font("Arial", Font.BOLD, 13);
        
        // Number of payments
        JLabel lblPaymentsLabel = new JLabel("Total Payments:");
        lblPaymentsLabel.setFont(labelFont);
        gridPanel.add(lblPaymentsLabel);
        
        lblTotalPayments = new JLabel("0");
        lblTotalPayments.setFont(valueFont);
        gridPanel.add(lblTotalPayments);
        
        // Parking fees
        JLabel lblParkingFeesLabel = new JLabel("Parking Fees:");
        lblParkingFeesLabel.setFont(labelFont);
        gridPanel.add(lblParkingFeesLabel);
        
        lblParkingFees = new JLabel("RM 0.00");
        lblParkingFees.setFont(valueFont);
        lblParkingFees.setForeground(new Color(0, 100, 150));
        gridPanel.add(lblParkingFees);
        
        // Fines collected
        JLabel lblFinesCollectedLabel = new JLabel("Fines Collected:");
        lblFinesCollectedLabel.setFont(labelFont);
        gridPanel.add(lblFinesCollectedLabel);
        
        lblFinesCollected = new JLabel("RM 0.00");
        lblFinesCollected.setFont(valueFont);
        lblFinesCollected.setForeground(new Color(150, 100, 0));
        gridPanel.add(lblFinesCollected);
        
        // Total revenue
        JLabel lblTotalRevenueLabel = new JLabel("Total Revenue:");
        lblTotalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gridPanel.add(lblTotalRevenueLabel);
        
        lblTotalRevenue = new JLabel("RM 0.00");
        lblTotalRevenue.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalRevenue.setForeground(new Color(0, 130, 0));
        gridPanel.add(lblTotalRevenue);
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the current vehicles table panel
     */
    private JPanel createCurrentVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), 
                           "Currently Parked Vehicles",
                           TitledBorder.LEFT,
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table
        String[] columns = {"License Plate", "Type", "Spot", "Entry Time", "Duration", "Status"};
        vehicleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblCurrentVehicles = new JTable(vehicleTableModel);
        tblCurrentVehicles.setFont(new Font("Monospaced", Font.PLAIN, 10));
        tblCurrentVehicles.getTableHeader().setFont(new Font("Arial", Font.BOLD, 10));
        tblCurrentVehicles.setRowHeight(22);
        
        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblCurrentVehicles.getColumnCount(); i++) {
            tblCurrentVehicles.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(tblCurrentVehicles);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the unpaid fines summary panel
     */
    private JPanel createUnpaidFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), 
                           "Unpaid Fines",
                           TitledBorder.LEFT,
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font valueFont = new Font("Arial", Font.BOLD, 13);
        
        // Number of vehicles with unpaid fines
        JLabel lblCountLabel = new JLabel("Vehicles with Fines:");
        lblCountLabel.setFont(labelFont);
        gridPanel.add(lblCountLabel);
        
        lblUnpaidFinesCount = new JLabel("0");
        lblUnpaidFinesCount.setFont(valueFont);
        lblUnpaidFinesCount.setForeground(new Color(150, 50, 0));
        gridPanel.add(lblUnpaidFinesCount);
        
        // Separator
        gridPanel.add(new JLabel(""));
        gridPanel.add(new JLabel(""));
        
        // Total outstanding
        JLabel lblTotalLabel = new JLabel("Total Outstanding:");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gridPanel.add(lblTotalLabel);
        
        lblUnpaidFinesTotal = new JLabel("RM 0.00");
        lblUnpaidFinesTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblUnpaidFinesTotal.setForeground(new Color(180, 0, 0));
        gridPanel.add(lblUnpaidFinesTotal);
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        // Add info text at bottom
        JLabel infoLabel = new JLabel("<html><i>Fines from previous visits<br>that haven't been paid yet</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates the violations panel (NEW)
     */
    private JPanel createViolationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), 
                           "Current Violations",
                           TitledBorder.LEFT,
                           TitledBorder.TOP,
                           new Font("Arial", Font.BOLD, 13)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font valueFont = new Font("Arial", Font.BOLD, 13);
        
        // Number of vehicles currently violating
        JLabel lblViolationCountLabel = new JLabel("Vehicles Violating:");
        lblViolationCountLabel.setFont(labelFont);
        gridPanel.add(lblViolationCountLabel);
        
        lblViolationCount = new JLabel("0");
        lblViolationCount.setFont(valueFont);
        lblViolationCount.setForeground(new Color(180, 0, 0));
        gridPanel.add(lblViolationCount);
        
        // Separator
        gridPanel.add(new JLabel(""));
        gridPanel.add(new JLabel(""));
        
        // Violation types
        JLabel lblTypesLabel = new JLabel("Violation Types:");
        lblTypesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gridPanel.add(lblTypesLabel);
        
        JLabel lblTypesValue = new JLabel("<html>• Overstaying (>24h)<br>• Reserved w/o permit</html>");
        lblTypesValue.setFont(new Font("Arial", Font.PLAIN, 10));
        gridPanel.add(lblTypesValue);
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        // Add info text at bottom
        JLabel infoLabel = new JLabel("<html><i>Vehicles currently parked<br>that are violating rules</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Refreshes all reports with current data
     */
    private void refreshAllReports() {
        loadOccupancyReport();
        loadRevenueReport();
        loadCurrentVehicles();
        loadUnpaidFinesSummary();
        loadViolationsSummary();
    }

    /**
     * Loads and displays occupancy report using ReportService
     */
    private void loadOccupancyReport() {
        double occupancyRate = reportService.getOccupancyRate();
        lblOccupancyRate.setText(String.format("%.1f%%", occupancyRate));
        
        // Update color based on occupancy
        if (occupancyRate < 50) {
            lblOccupancyRate.setForeground(new Color(0, 150, 0));
        } else if (occupancyRate < 80) {
            lblOccupancyRate.setForeground(new Color(200, 100, 0));
        } else {
            lblOccupancyRate.setForeground(new Color(200, 0, 0));
        }
        
        // Get detailed breakdown from service
        txtOccupancyDetails.setText(reportService.getOccupancyDetails());
        txtOccupancyDetails.setCaretPosition(0);
    }

    /**
     * Loads and displays revenue report using ReportService
     */
    private void loadRevenueReport() {
        lblTotalPayments.setText(String.valueOf(reportService.getPaymentCount()));
        lblParkingFees.setText(String.format("RM %.2f", reportService.getTotalParkingFees()));
        lblFinesCollected.setText(String.format("RM %.2f", reportService.getTotalFinesCollected()));
        lblTotalRevenue.setText(String.format("RM %.2f", reportService.getTotalRevenue()));
    }

    /**
     * Loads and displays currently parked vehicles using ReportService
     */
    private void loadCurrentVehicles() {
        vehicleTableModel.setRowCount(0);
        
        for (String[] row : reportService.getCurrentVehiclesData()) {
            vehicleTableModel.addRow(row);
        }
    }

    /**
     * Loads and displays unpaid fines summary using ReportService
     */
    private void loadUnpaidFinesSummary() {
        lblUnpaidFinesCount.setText(String.valueOf(reportService.getUnpaidFinesVehicleCount()));
        lblUnpaidFinesTotal.setText(String.format("RM %.2f", reportService.getUnpaidFinesTotal()));
    }

    /**
     * Loads and displays violations summary using ReportService
     */
    private void loadViolationsSummary() {
        lblViolationCount.setText(String.valueOf(reportService.getViolationCount()));
    }

    /**
     * Public method to refresh the panel
     */
    public void refreshPanel() {
        refreshAllReports();
    }
}