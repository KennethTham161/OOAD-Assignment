package parking.ui;

import javax.swing.*;
import java.awt.*;

public class ReportingPanel extends JPanel {

    public ReportingPanel() {
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Reporting Panel", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 18));
        add(placeholder, BorderLayout.CENTER);
    }
}
