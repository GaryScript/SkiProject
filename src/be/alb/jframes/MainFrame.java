package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        setTitle("Gestion de l'école de ski");
        setSize(717, 563);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // layout to change page
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // pannel for main menu
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 1));

        // buttons 
        JButton manageInstructorButton = new JButton("Manage Instructor");
        JButton manageSkierButton = new JButton("Manage Skier");
        JButton createLessonButton = new JButton("Create Lesson");
        JButton createBookingButton = new JButton("Create Booking");

        manageInstructorButton.addActionListener(e -> cardLayout.show(cardPanel, "InstructorPanel"));

        mainMenuPanel.add(manageInstructorButton);
        mainMenuPanel.add(manageSkierButton);
        mainMenuPanel.add(createLessonButton);
        mainMenuPanel.add(createBookingButton);

        // adding main panel to frame
        cardPanel.add(mainMenuPanel, "MainMenu");

        // adding panel to manage all instructors
        JPanel instructorPanel = new ManageInstructorsPanel();
        cardPanel.add(instructorPanel, "InstructorPanel");

        getContentPane().add(cardPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
