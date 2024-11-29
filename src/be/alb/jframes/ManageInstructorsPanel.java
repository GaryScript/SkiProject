package be.alb.jframes;

import be.alb.models.Instructor;
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

        // Charger les instructeurs à partir de la base de données
        loadInstructors();

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

        table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Bouton pour créer un instructeur
        JButton createButton = new JButton("Créer un nouvel instructeur");
        createButton.addActionListener(e -> {
            CreateInstructorPanel createInstructorPanel = new CreateInstructorPanel(cardLayout, mainPanel);
            mainPanel.add(createInstructorPanel, "createInstructorPanel");
            cardLayout.show(mainPanel, "createInstructorPanel");
        });
        buttonPanel.add(createButton);

        // Bouton pour supprimer un instructeur
        JButton deleteButton = new JButton("Supprimer un instructeur");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int instructorId = (int) table.getValueAt(selectedRow, 0); // Récupérer l'ID de l'instructeur sélectionné
                Instructor instructor = getInstructorById(instructorId); // Récupérer l'objet Instructor
                if (instructor != null) {
                    deleteInstructor(instructor); // Supprimer l'instructeur
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un instructeur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);

        // Bouton pour revenir au menu principal
        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    // Méthode pour charger les instructeurs depuis la base de données
    private void loadInstructors() {
        this.instructors = Instructor.getAllInstructors();
    }

    // Méthode pour récupérer un instructeur par son ID
    private Instructor getInstructorById(int id) {
        for (Instructor instructor : instructors) {
            if (instructor.getId() == id) {
                return instructor;
            }
        }
        return null;
    }

    // Méthode pour supprimer un instructeur et mettre à jour la liste
    public void deleteInstructor(Instructor instructor) {
        if (instructor.deleteInstructor()) {
            // Si la suppression réussit, mettre à jour la liste
            instructors.remove(instructor); // Supprime l'instructeur de la liste
            JOptionPane.showMessageDialog(this, "Instructor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(); // Rafraîchit le tableau
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete instructor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour rafraîchir l'affichage du tableau
    private void refreshTable() {
        // Vider et réinitialiser le tableau avec la nouvelle liste d'instructeurs
        String[] columnNames = {"ID", "Nom", "Prénom", "Ville"};
        Object[][] data = new Object[instructors.size()][columnNames.length];

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            data[i][0] = instructor.getId();
            data[i][1] = instructor.getLastName();
            data[i][2] = instructor.getFirstName();
            data[i][3] = instructor.getCity();
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames)); // Mettre à jour le modèle de la table
        revalidate(); // Rafraîchit le layout
        repaint(); // Redessine le panneau
    }
}
