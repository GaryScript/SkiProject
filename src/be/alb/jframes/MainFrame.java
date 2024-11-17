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

        // Initialiser le CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panneau principal avec les boutons
        JPanel menuPanel = new JPanel(new GridLayout(4, 1));
        JButton manageInstructorButton = new JButton("Manage Instructor");
        JButton manageSkierButton = new JButton("Manage Skier");
        JButton createLessonButton = new JButton("Create Lesson");
        JButton createBookingButton = new JButton("Create Booking");

        manageInstructorButton.addActionListener(e -> openManageInstructorPage());
        menuPanel.add(manageInstructorButton);
        menuPanel.add(manageSkierButton);
        menuPanel.add(createLessonButton);
        menuPanel.add(createBookingButton);

        // Ajouter les panneaux au CardLayout
        mainPanel.add(menuPanel, "menuPanel");

        // Ajouter le panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        setVisible(true);
    }

    private void openManageInstructorPage() {
        // Ajouter ManageInstructorsPanel au CardLayout
        ManageInstructorsPanel manageInstructorsPanel = new ManageInstructorsPanel(cardLayout, mainPanel);
        mainPanel.add(manageInstructorsPanel, "manageInstructorsPanel");
        cardLayout.show(mainPanel, "manageInstructorsPanel");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
