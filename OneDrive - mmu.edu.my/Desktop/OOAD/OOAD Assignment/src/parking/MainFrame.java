package parking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import parking.ui.*;

/**
 * MainFrame - The main application window.
 * Uses CardLayout to switch between Entry, Exit, Admin, and Reporting panels.
 * This is the central hub that ties all panels together.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Navigation buttons
    private JButton entryButton;
    private JButton exitButton;
    private JButton adminButton;
    private JButton reportButton;

    // Panels
    private EntryPanel entryPanel;
    private ExitPanel exitPanel;
    private AdminPanel adminPanel;
    private ReportingPanel reportingPanel;

    public MainFrame() {
        setTitle("University Parking Lot Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());

        // Create the navigation bar at the top
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);

        // Create the card layout panel (holds all 4 panels)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create the 4 panels
        entryPanel = new EntryPanel();
        exitPanel = new ExitPanel();
        adminPanel = new AdminPanel();
        reportingPanel = new ReportingPanel();

        // Add panels to the card layout with names
        cardPanel.add(entryPanel, "Entry");
        cardPanel.add(exitPanel, "Exit");
        cardPanel.add(adminPanel, "Admin");
        cardPanel.add(reportingPanel, "Reports");

        add(cardPanel, BorderLayout.CENTER);

        // Show the Entry panel by default
        cardLayout.show(cardPanel, "Entry");
        highlightButton(entryButton);
    }

    // Creates the navigation bar with 4 buttons
    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new GridLayout(1, 4, 5, 0));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navBar.setBackground(new Color(50, 50, 50));

        entryButton = createNavButton("Vehicle Entry");
        exitButton = createNavButton("Vehicle Exit");
        adminButton = createNavButton("Admin Panel");
        reportButton = createNavButton("Reports");

        navBar.add(entryButton);
        navBar.add(exitButton);
        navBar.add(adminButton);
        navBar.add(reportButton);

        // Button click actions - switch panels
        entryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Entry");
                highlightButton(entryButton);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Exit");
                highlightButton(exitButton);
            }
        });

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Admin");
                highlightButton(adminButton);
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Reports");
                highlightButton(reportButton);
            }
        });

        return navBar;
    }

    // Helper: create a styled navigation button
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Helper: highlight the active button
    private void highlightButton(JButton activeButton) {
        // Reset all buttons to default color
        JButton[] buttons = {entryButton, exitButton, adminButton, reportButton};
        for (JButton btn : buttons) {
            btn.setBackground(new Color(70, 70, 70));
        }
        // Highlight the active one
        activeButton.setBackground(new Color(0, 120, 215));
    }
}
