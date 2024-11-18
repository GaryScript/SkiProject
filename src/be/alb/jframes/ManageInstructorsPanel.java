package be.alb.jframes;

import be.alb.models.Instructor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageInstructorsPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ManageInstructorsPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        List<Instructor> instructors = Instructor.getAllInstructors();

        if (instructors == null || instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"ID", "Nom", "Prénom", "Ville"};
        Object[][] data = new Object[instructors.size()][columnNames.length];

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getLastName(); 
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();
        }
        
        
        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // button to create instructor 
        JButton createButton = new JButton("Créer un nouvel instructeur");
        createButton.addActionListener(e -> {
            CreateInstructorPanel createInstructorPanel = new CreateInstructorPanel(cardLayout, mainPanel);
            mainPanel.add(createInstructorPanel, "createInstructorPanel");
            cardLayout.show(mainPanel, "createInstructorPanel");
        });
        buttonPanel.add(createButton);

        // button to go back to main menu
        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
    }
}
