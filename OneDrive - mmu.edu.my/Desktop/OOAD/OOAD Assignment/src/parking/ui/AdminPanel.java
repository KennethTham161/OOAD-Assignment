package parking.ui;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {

    public AdminPanel() {
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Admin Panel", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 18));
        add(placeholder, BorderLayout.CENTER);
    }
}
