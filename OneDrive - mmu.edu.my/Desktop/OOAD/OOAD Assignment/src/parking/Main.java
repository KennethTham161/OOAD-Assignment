package parking;

import javax.swing.*;

/**
 * Main - Entry point of the Parking Lot Management System.
 * Launches the MainFrame GUI.
 */
public class Main {

    public static void main(String[] args) {
        // Run the GUI on the Swing event thread (best practice)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
