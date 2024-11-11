package be.alb.jframes;

import be.alb.controllers.InstructorController;
import be.alb.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageInstructorsFrame extends JFrame {
    private InstructorController instructorController;

    public ManageInstructorsFrame() {
    	InstructorController instructorController = new InstructorController();

        setTitle("Manage Instructors");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // get all the instructors
        List<Instructor> instructors = instructorController.getAllInstructors();

        if (instructors == null || instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;  // If there are no instructors, exit the method
        }

        // Prepare the array to display all the instructors
        String[] columnNames = {"ID", "Nom", "Prénom", "Ville"};
        
        // Create an array to hold the data for the JTable
        Object[][] data = new Object[instructors.size()][columnNames.length];

        // Fill the data array
        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getName();
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();
        }

        // Create the table to display the instructors
        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button for creating a new instructor
        JButton createButton = new JButton("Créer un nouvel instructeur");
        createButton.addActionListener(e -> {
            // Logic to create a new instructor
        });
        add(createButton, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
