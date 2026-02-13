package parking.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime; // Added for Payment constructor 
import parking.service.ExitService;
import parking.data.DataCenter;
import parking.model.Payment;
import parking.model.PaymentMethod;

// Vehicle Exit and Payment Panel - Member 4
 
 
public class ExitPanel extends JPanel {
    // UI Components for input and action 
    
    private JTextField txtPlate = new JTextField(15);
    private JButton btnSearch = new JButton("Calculate Fees");
    
    // Labels to display billing details
   
    private JLabel lblVType = new JLabel("-");
    private JLabel lblSType = new JLabel("-");
    private JLabel lblInTime = new JLabel("-");
    private JLabel lblOutTime = new JLabel("-");
    private JLabel lblDuration = new JLabel("-");
    private JLabel lblRate = new JLabel("-");
    private JLabel lblParkingFee = new JLabel("RM 0.00");
    private JLabel lblCurrentFine = new JLabel("RM 0.00");
    private JLabel lblOldFines = new JLabel("RM 0.00");
    private JLabel lblTotal = new JLabel("RM 0.00");
    
    private JComboBox<PaymentMethod> comboPayment = new JComboBox<>(PaymentMethod.values());
    private JButton btnPay = new JButton("Confirm Payment & Exit");
    
    // Logic and calculation service 
    
    private ExitService exitService = new ExitService();
    private double currentTotal = 0;

    public ExitPanel() {
        // Main layout settings 
        
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 1. Top Section: License Plate Input 
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        top.add(new JLabel("License Plate:"));
        top.add(txtPlate); 
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // 2. Center Section: Bill Details (Receipt Style)
        
        JPanel center = new JPanel(new GridBagLayout());
        TitledBorder tb = BorderFactory.createTitledBorder(" Payment Bill Details ");
        tb.setTitleFont(new Font("Arial", Font.BOLD, 18));
        center.setBorder(tb);
        
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST; // Align content to the left 
        g.insets = new Insets(8, 30, 8, 10); // Standard spacing 
        
        // Font used for billing information
        
        Font receiptFont = new Font("Arial", Font.PLAIN, 18);

        int r = 0;
        g.gridy = r++; g.gridwidth = 2; center.add(Box.createVerticalStrut(40), g);
        g.gridwidth = 1;

        // Adding detail rows using helper method
        
        addReceiptRow(center, "Vehicle Type : ", lblVType, r++, g, receiptFont);
        addReceiptRow(center, "Spot Type    : ", lblSType, r++, g, receiptFont);
        addReceiptRow(center, "Entry Time   : ", lblInTime, r++, g, receiptFont);
        addReceiptRow(center, "Exit Time    : ", lblOutTime, r++, g, receiptFont);
        addReceiptRow(center, "Duration     : ", lblDuration, r++, g, receiptFont);
        addReceiptRow(center, "Hourly Rate  : ", lblRate, r++, g, receiptFont);
        
        // Visual separator 
       
        g.gridy = r++; g.gridx = 0; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        center.add(new JSeparator(), g);
        g.fill = GridBagConstraints.NONE; g.gridwidth = 1;

        addReceiptRow(center, "Parking Fee  : ", lblParkingFee, r++, g, receiptFont);
        addReceiptRow(center, "Violation Fine: ", lblCurrentFine, r++, g, receiptFont);
        addReceiptRow(center, "Unpaid Fines : ", lblOldFines, r++, g, receiptFont);

        // Weighty glue to push content to the top 
        
        g.weighty = 1.0; g.gridy = r; center.add(new JLabel(""), g);
        add(center, BorderLayout.CENTER);

        // 3. South Section: Grand Total and Payment 
        
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        
        // Grand Total row 
       
        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JLabel tt = new JLabel("TOTAL DUE: "); 
        tt.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 36)); 
        lblTotal.setForeground(Color.RED); // Total in Red 
        totalRow.add(tt); totalRow.add(lblTotal);
        
        // Payment buttons and options 
        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        payRow.add(new JLabel("Method: ")); 
        payRow.add(comboPayment);
        btnPay.setPreferredSize(new Dimension(240, 45));
        btnPay.setBackground(new Color(70, 130, 180)); 
        btnPay.setForeground(Color.WHITE);
        payRow.add(btnPay);

        south.add(totalRow); south.add(payRow);
        add(south, BorderLayout.SOUTH);

        setupEvents(); // Initialize listeners 
    }

    
    private void addReceiptRow(JPanel p, String title, JLabel val, int row, GridBagConstraints g, Font f) {
        g.gridy = row;
        // Column 0: Descriptive title 
        g.gridx = 0; g.weightx = 0; 
        JLabel l = new JLabel(title); l.setFont(f); p.add(l, g);
        
        // Column 1: Calculated value 
        g.gridx = 1; g.weightx = 0; 
        val.setFont(f); p.add(val, g);
        
        g.gridx = 2; g.weightx = 1.0; 
        p.add(new JLabel(""), g); 
    }

    //Set up listeners for the Search and Pay buttons.
     
     
    private void setupEvents() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Action when "Calculate Fees" is clicked 
       
        btnSearch.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            // Calculation only (Preview) 
            this.currentTotal = exitService.processExitCalculation(plate);
            
            if (currentTotal < 0) {
                JOptionPane.showMessageDialog(this, "Vehicle Not Found!");
            } else {
                // Update UI labels with service data 
                lblVType.setText(exitService.getVType());
                lblSType.setText(exitService.getSType());
                lblInTime.setText(exitService.getIn().format(dtf));
                lblOutTime.setText(exitService.getOut().format(dtf));
                lblDuration.setText(exitService.getMins() + " mins (" + exitService.getHours() + "h)");
                lblRate.setText("RM " + String.format("%.2f", exitService.getRate()));
                lblParkingFee.setText("RM " + String.format("%.2f", exitService.getFee()));
                lblCurrentFine.setText("RM " + String.format("%.2f", exitService.getFine()));
                lblOldFines.setText("RM " + String.format("%.2f", DataCenter.getUnpaidFineTotal(plate)));
                lblTotal.setText("RM " + String.format("%.2f", currentTotal));
            }
        });

        // Action when "Confirm Payment" is clicked 
        
        btnPay.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            if (currentTotal <= 0) return;

            // 1. Permanently record exit timestamp 
            
            exitService.finalizeExit(plate); 
            
            // 2. Map data to the 9 constructor parameters of Payment.java
            
            String ticketId = exitService.getSType(); // Using Spot Type as Ticket ID
            LocalDateTime inT = exitService.getIn();
            LocalDateTime outT = exitService.getOut();
            long hrs = exitService.getHours();
            double fee = exitService.getFee();
            double finesPaid = exitService.getFine() + DataCenter.getUnpaidFineTotal(plate);
            PaymentMethod method = (PaymentMethod) comboPayment.getSelectedItem();

            // 3. Save payment record using the 9-parameter constructor
            
            Payment newPayment = new Payment(
                plate,        // 1. licensePlate
                ticketId,     // 2. ticketId
                inT,          // 3. entryTime
                outT,         // 4. exitTime
                hrs,          // 5. hoursParked
                fee,          // 6. parkingFee
                finesPaid,    // 7. finesPaid
                currentTotal, // 8. totalAmount
                method        // 9. paymentMethod
            );
            
            DataCenter.addPayment(newPayment);
            
            // 4. Update system: remove vehicle and clear fines 
            DataCenter.removeVehicle(plate);
            DataCenter.markFinesPaid(plate);
            
            JOptionPane.showMessageDialog(this, "Payment Successful!");
            resetUI();
        });
    }

    // Clears all fields and resets labels after success.
    private void resetUI() {
        txtPlate.setText(""); lblTotal.setText("RM 0.00");
        lblVType.setText("-"); lblSType.setText("-"); lblInTime.setText("-");
        lblOutTime.setText("-"); lblDuration.setText("-"); lblRate.setText("-");
        lblParkingFee.setText("RM 0.00"); lblCurrentFine.setText("RM 0.00"); lblOldFines.setText("RM 0.00");
    }
}
