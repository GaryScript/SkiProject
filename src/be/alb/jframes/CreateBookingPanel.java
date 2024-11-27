package be.alb.jframes;

import be.alb.models.Lesson;
import be.alb.models.Skier;
import be.alb.dao.SkierDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Panel principal pour le choix entre leçon privée et publique et les tableaux
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Panel pour choisir entre leçon privée et publique
        JPanel lessonChoicePanel = new JPanel();
        privateLessonButton = new JRadioButton("Private Lesson");
        publicLessonButton = new JRadioButton("Public Lesson");
        lessonTypeGroup = new ButtonGroup();
        lessonTypeGroup.add(privateLessonButton);
        lessonTypeGroup.add(publicLessonButton);

        lessonChoicePanel.add(privateLessonButton);
        lessonChoicePanel.add(publicLessonButton);

        contentPanel.add(lessonChoicePanel, BorderLayout.NORTH);

        // Tableau pour afficher les leçons
        lessonTable = new JTable();
        JScrollPane scrollPaneLessons = new JScrollPane(lessonTable);
        contentPanel.add(scrollPaneLessons, BorderLayout.WEST);

        // Tableau pour afficher les élèves
        skierTable = new JTable();
        JScrollPane scrollPaneSkiers = new JScrollPane(skierTable);
        contentPanel.add(scrollPaneSkiers, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

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
       
            cardLayout.show(mainPanel, "MainScreen");
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
            // Création du modèle de table
            String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Création du formatteur pour la date et l'heure
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Charger les leçons en fonction de l'option privée ou publique
            if (isPrivate) {
                lessons = Lesson.getAllPrivateLessons();  // Charger les leçons privées
                for (Lesson lesson : lessons) {
                    String lessonTypeName = lesson.getLessonType().getName();
                    String instructorName = lesson.getInstructor() != null
                            ? lesson.getInstructor().getFirstName() + " " + lesson.getInstructor().getLastName()
                            : "None";

                    // Formatage de la date et de l'heure pour le début et la fin
                    String startTime = dateTimeFormat.format(lesson.getStartDate());
                    String endTime = dateTimeFormat.format(lesson.getEndDate());

                    tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime});
                }
            } else {
                // Charger les leçons publiques et les regrouper par groupId
                lessons = Lesson.getAllPublicLessons();
                Map<Integer, List<Lesson>> groupedLessons = lessons.stream()
                        .collect(Collectors.groupingBy(Lesson::getLessonGroupId));  // Regroupement par groupId

                // Ajouter les données au modèle de table
                for (Map.Entry<Integer, List<Lesson>> entry : groupedLessons.entrySet()) {
                    List<Lesson> groupLessons = entry.getValue();
                    // Récupérer les informations communes du groupe
                    Lesson firstLesson = groupLessons.get(0);  // Prendre la première leçon du groupe
                    String lessonTypeName = firstLesson.getLessonType().getName();
                    String instructorName = firstLesson.getInstructor() != null
                            ? firstLesson.getInstructor().getFirstName() + " " + firstLesson.getInstructor().getLastName()
                            : "None";

                    // Formatage de la date et de l'heure pour le début et la fin
                    String startTime = dateTimeFormat.format(firstLesson.getStartDate());
                    String endTime = dateTimeFormat.format(groupLessons.get(groupLessons.size() - 1).getEndDate());  // Dernière leçon du groupe

                    // Ajouter les données du groupe au tableau
                    tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime});
                }
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
