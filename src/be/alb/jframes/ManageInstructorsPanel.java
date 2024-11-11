package be.alb.jframes;

import be.alb.controllers.InstructorController;
import be.alb.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageInstructorsPanel extends JPanel { // Change JFrame to JPanel
    private InstructorController instructorController;

    public ManageInstructorsPanel() { // Correct constructor for ManageInstructorsPanel
        instructorController = new InstructorController();
        setLayout(new BorderLayout());


        List<Instructor> instructors = instructorController.getAllInstructors();

        if (instructors == null || instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"ID", "Nom", "Prénom", "Ville"};
        Object[][] data = new Object[instructors.size()][columnNames.length];

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getName();
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();
        }


        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton createButton = new JButton("Créer un nouvel instructeur");
        createButton.addActionListener(e -> {
        		// todo
        });
        add(createButton, BorderLayout.NORTH);
    }
}
