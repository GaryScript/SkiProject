package be.alb.jframes;

import be.alb.models.Lesson;
import be.alb.models.Skier;
import be.alb.dao.SkierDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CreateBookingPanel extends JPanel {

    private JTable lessonTable;
    private JTable skierTable;
    private List<Skier> skiers;
    private List<Lesson> lessons;
    private JRadioButton privateLessonButton;
    private JRadioButton publicLessonButton;
    private ButtonGroup lessonTypeGroup;

    public CreateBookingPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Titre du panel
        JLabel titleLabel = new JLabel("Create Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Panel pour choisir entre leçon privée et publique
        JPanel lessonChoicePanel = new JPanel();
        privateLessonButton = new JRadioButton("Private Lesson");
        publicLessonButton = new JRadioButton("Public Lesson");
        lessonTypeGroup = new ButtonGroup();
        lessonTypeGroup.add(privateLessonButton);
        lessonTypeGroup.add(publicLessonButton);

        lessonChoicePanel.add(privateLessonButton);
        lessonChoicePanel.add(publicLessonButton);

        add(lessonChoicePanel, BorderLayout.NORTH);

        // Tableau pour afficher les leçons
        lessonTable = new JTable();
        JScrollPane scrollPaneLessons = new JScrollPane(lessonTable);
        add(scrollPaneLessons, BorderLayout.WEST);

        // Tableau pour afficher les élèves
        skierTable = new JTable();
        JScrollPane scrollPaneSkiers = new JScrollPane(skierTable);
        add(scrollPaneSkiers, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton submitButton = new JButton("Submit");
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les données des leçons et élèves
        loadSkierData();

        // Action du bouton Back
        backButton.addActionListener(e -> {
            // Remettre à l'écran principal ou le panneau précédent
            cardLayout.show(mainPanel, "MainScreen"); // Adaptez selon le nom du panel
        });

        // Action du bouton Submit
        submitButton.addActionListener(e -> {
            // Récupérer les leçons et les skieurs sélectionnés
            int lessonRow = lessonTable.getSelectedRow();
            int skierRow = skierTable.getSelectedRow();

            if (lessonRow == -1 || skierRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select both a lesson and a skier", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Récupérer les objets Lesson et Skier sélectionnés
            Lesson selectedLesson = lessons.get(lessonRow);
            Skier selectedSkier = skiers.get(skierRow);

            // Ici, vous pouvez effectuer les actions nécessaires (par exemple, associer le skieur à la leçon)
            JOptionPane.showMessageDialog(this, "Booking created for " + selectedSkier.getFirstName() + " " + selectedSkier.getLastName() + " for the lesson " + selectedLesson.getLessonType().getName(), "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
        });

        // Action de changement de sélection de type de leçon
        privateLessonButton.addActionListener(e -> loadLessonData(true)); // Load private lessons
        publicLessonButton.addActionListener(e -> loadLessonData(false)); // Load public lessons
    }

    // Charger les leçons privées ou publiques en fonction de l'option sélectionnée
    private void loadLessonData(boolean isPrivate) {
        try {
            // Si la leçon est privée, utiliser getAllPrivateLessons, sinon getAllPublicLessons
            if (isPrivate) {
                lessons = Lesson.getAllPrivateLessons();
            } else {
                lessons = Lesson.getAllPublicLessons();
            }

            // Configuration des colonnes du tableau des leçons
            String[] columnNames = {"Lesson Type", "Instructor", "Start Time", "End Time", "Private"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Ajouter les données au modèle
            for (Lesson lesson : lessons) {
                String lessonTypeName = lesson.getLessonType().getName();
                String instructorName = lesson.getInstructor() != null
                        ? lesson.getInstructor().getFirstName() + " " + lesson.getInstructor().getLastName()
                        : "None";
                String startTime = lesson.getStartDate().toString();
                String endTime = lesson.getEndDate().toString();
                String isItPrivate = lesson.isPrivate() ? "Yes" : "No";

                tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime, isItPrivate});
            }

            // Appliquer le modèle au tableau
            lessonTable.setModel(tableModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lessons: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Charger les skieurs depuis la méthode getAllSkiers
    private void loadSkierData() {
        try {
            // Appel à la méthode getAllSkiers pour récupérer les skieurs
            skiers = Skier.getAllSkiers();

            // Configuration des colonnes du tableau des skieurs
            String[] columnNames = {"First Name", "Last Name", "City", "Postal Code"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Ajouter les données au modèle
            for (Skier skier : skiers) {
                tableModel.addRow(new Object[]{skier.getFirstName(), skier.getLastName(), skier.getCity(), skier.getPostalCode()});
            }

            // Appliquer le modèle au tableau
            skierTable.setModel(tableModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading skiers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
