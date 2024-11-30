package be.alb.jframes;

import be.alb.models.Instructor;
import be.alb.models.Accreditation;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageInstructorsPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private List<Instructor> instructors; 
    private JTable table; 

    public ManageInstructorsPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        loadInstructors();

        if (instructors == null || instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"ID", "Nom", "Prénom", "Ville", "Accréditations"};
        Object[][] data = new Object[instructors.size()][columnNames.length];

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getLastName();
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();

            StringBuilder accreditations = new StringBuilder();
            for (Accreditation accreditation : instructor.getAccreditations()) {
                if (accreditations.length() > 0) {
                    accreditations.append(", ");
                }
                accreditations.append(accreditation.getName());
            }
            data[i][4] = accreditations.toString(); 
        }

        table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("Créer un nouvel instructeur");
        createButton.addActionListener(e -> {
            CreateInstructorPanel createInstructorPanel = new CreateInstructorPanel(cardLayout, mainPanel);
            mainPanel.add(createInstructorPanel, "createInstructorPanel");
            cardLayout.show(mainPanel, "createInstructorPanel");
        });
        buttonPanel.add(createButton);

 
        JButton deleteButton = new JButton("Supprimer un instructeur");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int instructorId = (int) table.getValueAt(selectedRow, 0); 
                Instructor instructor = getInstructorById(instructorId); 
                if (instructor != null) {
                    deleteInstructor(instructor); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un instructeur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void loadInstructors() {
        this.instructors = Instructor.getAllInstructors();
    }

    private Instructor getInstructorById(int id) {
        for (Instructor instructor : instructors) {
            if (instructor.getId() == id) {
                return instructor;
            }
        }
        return null;
    }

    public void deleteInstructor(Instructor instructor) {
        if (instructor.deleteInstructor()) {
            instructors.remove(instructor);
            JOptionPane.showMessageDialog(this, "Instructor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(); 
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete instructor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        String[] columnNames = {"ID", "Nom", "Prénom", "Ville", "Accréditations"};
        Object[][] data = new Object[instructors.size()][columnNames.length];

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getLastName();
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();

            StringBuilder accreditations = new StringBuilder();
            for (Accreditation accreditation : instructor.getAccreditations()) {
                if (accreditations.length() > 0) {
                    accreditations.append(", ");
                }
                accreditations.append(accreditation.getName());
            }
            data[i][4] = accreditations.toString(); 
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames)); 
        revalidate(); 
        repaint(); 
    }
}
