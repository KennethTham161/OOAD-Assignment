package parking.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import parking.service.ExitService;
import parking.data.DataCenter;
import parking.model.Payment;
import parking.model.PaymentMethod;

// UI Panel for vehicle exit processing and payment.
 
public class ExitPanel extends JPanel {
    // UI components for interaction 
    private JTextField txtPlate = new JTextField(15);
    private JButton btnSearch = new JButton("Calculate Fees");
    
    // Billing detail labels 
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
    
    private ExitService exitService = new ExitService();
    private double currentTotal = 0;

    public ExitPanel() {
        // Layout and border settings 
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 1. Top Section: Input 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        top.add(new JLabel("License Plate:"));
        top.add(txtPlate); 
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // 2. Center Section: Professional Receipt Display 
        JPanel center = new JPanel(new GridBagLayout());
        TitledBorder tb = BorderFactory.createTitledBorder(" Payment Bill Details ");
        tb.setTitleFont(new Font("Arial", Font.BOLD, 18));
        center.setBorder(tb);
        
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST; 
        g.insets = new Insets(8, 30, 8, 10);
        Font receiptFont = new Font("Arial", Font.PLAIN, 18);

        int r = 0;
        g.gridy = r++; g.gridwidth = 2; center.add(Box.createVerticalStrut(40), g);
        g.gridwidth = 1;

        // Populate receipt rows 
        addReceiptRow(center, "Vehicle Type : ", lblVType, r++, g, receiptFont);
        addReceiptRow(center, "Spot Type    : ", lblSType, r++, g, receiptFont);
        addReceiptRow(center, "Entry Time   : ", lblInTime, r++, g, receiptFont);
        addReceiptRow(center, "Exit Time    : ", lblOutTime, r++, g, receiptFont);
        addReceiptRow(center, "Duration     : ", lblDuration, r++, g, receiptFont);
        addReceiptRow(center, "Hourly Rate  : ", lblRate, r++, g, receiptFont);
        
        g.gridy = r++; g.gridx = 0; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL;
        center.add(new JSeparator(), g);
        g.fill = GridBagConstraints.NONE; g.gridwidth = 1;

        addReceiptRow(center, "Parking Fee  : ", lblParkingFee, r++, g, receiptFont);
        addReceiptRow(center, "Violation Fine: ", lblCurrentFine, r++, g, receiptFont);
        addReceiptRow(center, "Unpaid Fines : ", lblOldFines, r++, g, receiptFont);

        g.weighty = 1.0; g.gridy = r; center.add(new JLabel(""), g);
        add(center, BorderLayout.CENTER);

        // 3. South Section: Grand Totals 
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        
        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JLabel tt = new JLabel("TOTAL DUE: "); 
        tt.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 36)); 
        lblTotal.setForeground(Color.RED);
        totalRow.add(tt); totalRow.add(lblTotal);
        
        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        payRow.add(new JLabel("Method: ")); 
        payRow.add(comboPayment);
        btnPay.setPreferredSize(new Dimension(240, 45));
        btnPay.setBackground(new Color(70, 130, 180)); 
        btnPay.setForeground(Color.WHITE);
        payRow.add(btnPay);

        south.add(totalRow); south.add(payRow);
        add(south, BorderLayout.SOUTH);

        setupEvents();
    }

    //Logic for aligning labels to the left.
    
    private void addReceiptRow(JPanel p, String title, JLabel val, int row, GridBagConstraints g, Font f) {
        g.gridy = row;
        g.gridx = 0; g.weightx = 0; 
        JLabel l = new JLabel(title); l.setFont(f); p.add(l, g);
        g.gridx = 1; g.weightx = 0; 
        val.setFont(f); p.add(val, g);
        g.gridx = 2; g.weightx = 1.0; 
        p.add(new JLabel(""), g); 
    }

    //Set up button action listeners.
     
     
    private void setupEvents() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Execute fee calculation preview 
        btnSearch.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            this.currentTotal = exitService.processExitCalculation(plate);
            
            if (currentTotal < 0) {
                JOptionPane.showMessageDialog(this, "Vehicle Not Found!");
            } else {
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

        // Finalize transaction and release parking spot
        
        btnPay.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            if (currentTotal <= 0) return;

            // Cache the spot object before finalizing exit status
           
            parking.model.ParkingSpot targetSpot = null;
            parking.model.Vehicle v = DataCenter.findVehicleByPlate(plate);
            
            if (v != null) {
                // Identify where the vehicle is parked 
                targetSpot = DataCenter.findSpotById(v.getSpotId());
            }

            // 1. Record official exit timestamp
            
            exitService.finalizeExit(plate); 
            
            // 2. Prepare Payment Data
           
            String ticketId = exitService.getSType(); 
            LocalDateTime inT = exitService.getIn();
            LocalDateTime outT = exitService.getOut();
            long hrs = exitService.getHours();
            double fee = exitService.getFee();
            double finesPaid = exitService.getFine() + DataCenter.getUnpaidFineTotal(plate);
            PaymentMethod method = (PaymentMethod) comboPayment.getSelectedItem();

            // 3. Save the payment session 
            Payment newPayment = new Payment(
                plate, ticketId, inT, outT, hrs, fee, finesPaid, currentTotal, method
            );
            DataCenter.addPayment(newPayment);
            
            // 4. Release the cached spot back to Vacant status
           
            if (targetSpot != null) {
                targetSpot.release(); 
                System.out.println("DEBUG: Spot " + targetSpot.getSpotId() + " has been released.");
            }

            // 5. Update data records: remove from active vehicles and clear fines
            
            DataCenter.removeVehicle(plate);
            DataCenter.markFinesPaid(plate);
            
            JOptionPane.showMessageDialog(this, "Payment Successful!");
            resetUI();
        });
    }

    // Resets UI labels to default state 
     
    private void resetUI() {
        txtPlate.setText(""); lblTotal.setText("RM 0.00");
        lblVType.setText("-"); lblSType.setText("-"); lblInTime.setText("-");
        lblOutTime.setText("-"); lblDuration.setText("-"); lblRate.setText("-");
        lblParkingFee.setText("RM 0.00"); lblCurrentFine.setText("RM 0.00"); lblOldFines.setText("RM 0.00");
    }
}