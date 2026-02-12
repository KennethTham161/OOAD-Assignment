package parking.ui;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel {

    public EntryPanel() {
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Vehicle Entry Panel", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 18));
        add(placeholder, BorderLayout.CENTER);
    }
}
