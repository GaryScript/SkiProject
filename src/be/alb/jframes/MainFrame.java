package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    public MainFrame() {
        // Configuration main frame
        setTitle("Gestion de l'école de ski");
        setSize(717, 563);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centered window

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); 

        // buttons for redirection
        JButton manageInstructorButton = new JButton("Manage Instructor");
        JButton manageSkierButton = new JButton("Manage Skier");
        JButton createLessonButton = new JButton("Create Lesson");
        JButton createBookingButton = new JButton("Create Booking");

        manageInstructorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageInstructorPage();
            }
        });

        manageSkierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageSkierPage();
            }
        });

        createLessonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateLessonPage();
            }
        });

        createBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateBookingPage();
            }
        });

        panel.add(manageInstructorButton);
        panel.add(manageSkierButton);
        panel.add(createLessonButton);
        panel.add(createBookingButton);

        getContentPane().add(panel);

        setVisible(true);
    }

    // Methods open different pages
    private void openManageInstructorPage() {
        new InstructorManagementFrame();
        this.dispose();
    }

    private void openManageSkierPage() {
        new SkierManagementFrame();
        this.dispose();
    }

    private void openCreateLessonPage() {
        new CreateLessonFrame();
        this.dispose(); 
    }

    private void openCreateBookingPage() {
        new CreateBookingFrame();
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
