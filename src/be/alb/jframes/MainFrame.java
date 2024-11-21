package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Gestion de l'école de ski");
        setSize(717, 563);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // init the cardlayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // main page with main buttons
        JPanel menuPanel = new JPanel(new GridLayout(4, 1));
        JButton manageInstructorButton = new JButton("Manage Instructor");
        JButton manageSkierButton = new JButton("Manage Skier");
        JButton createLessonButton = new JButton("Create Lesson");
        JButton createBookingButton = new JButton("Create Booking");

        manageInstructorButton.addActionListener(e -> openManageInstructorPage());
        manageSkierButton.addActionListener(e -> openManageSkierPage());
        menuPanel.add(manageInstructorButton);
        menuPanel.add(manageSkierButton);
        menuPanel.add(createLessonButton);
        menuPanel.add(createBookingButton);

        // add panels
        mainPanel.add(menuPanel, "menuPanel");

        getContentPane().add(mainPanel);

        setVisible(true);
    }

    private void openManageInstructorPage() {
        ManageInstructorsPanel manageInstructorsPanel = new ManageInstructorsPanel(cardLayout, mainPanel);
        mainPanel.add(manageInstructorsPanel, "manageInstructorsPanel");
        cardLayout.show(mainPanel, "manageInstructorsPanel");
    }
    
    private void openManageSkierPage() {
        ManageSkiersPanel manageSkiersPanel = new ManageSkiersPanel(cardLayout, mainPanel);
        mainPanel.add(manageSkiersPanel, "manageSkiersPanel");
        cardLayout.show(mainPanel, "manageSkiersPanel");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
