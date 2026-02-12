package parking.ui;

import javax.swing.*;
import java.awt.*;

public class ExitPanel extends JPanel {

    public ExitPanel() {
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Vehicle Exit Panel", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 18));
        add(placeholder, BorderLayout.CENTER);
    }
}
