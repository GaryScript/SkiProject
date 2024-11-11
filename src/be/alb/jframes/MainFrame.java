package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestion de l'école de ski");
        setSize(717, 563);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); 

        // buttons for redirection 
        JButton manageInstructorButton = new JButton("Manage Instructor");
        JButton manageSkierButton = new JButton("Manage Skier");
        JButton createLessonButton = new JButton("Create Lesson");
        JButton createBookingButton = new JButton("Create Booking");

        manageInstructorButton.addActionListener(e -> openManageInstructorPage());

       
        // manageSkierButton.addActionListener(e -> openManageSkierPage());
        // createLessonButton.addActionListener(e -> openCreateLessonPage());
        // createBookingButton.addActionListener(e -> openCreateBookingPage());

        panel.add(manageInstructorButton);
        panel.add(manageSkierButton);
        panel.add(createLessonButton);
        panel.add(createBookingButton);

        getContentPane().add(panel);

        setVisible(true);
    }

    private void openManageInstructorPage() {
        ManageInstructorsFrame manageInstructorsFrame = new ManageInstructorsFrame();
        manageInstructorsFrame.setVisible(true); 
        this.dispose(); 
    }


    // 
    // Méthodes pour les autres pages (en fonction de leur implémentation)
    // private void openManageSkierPage() {
    //     new SkierManagementFrame();
    //     this.dispose();
    // }
    // private void openCreateLessonPage() {
    //     new CreateLessonFrame();
    //     this.dispose(); 
    // }
    // private void openCreateBookingPage() {
    //     new CreateBookingFrame();
    //     this.dispose();
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame()); 
    }
}
