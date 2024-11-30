package be.alb.jframes;

import be.alb.models.Lesson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageLessonsPanel extends JPanel {

    private JTable lessonTable;
    private List<Lesson> lessons;
    private JRadioButton privateLessonButton;
    private JRadioButton publicLessonButton;
    private ButtonGroup lessonTypeGroup;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ManageLessonsPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        // Titre du panel
        JLabel titleLabel = new JLabel("Manage Lessons", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Boutons pour choisir entre leçons privées et publiques
        JPanel lessonChoicePanel = new JPanel();
        privateLessonButton = new JRadioButton("Private Lessons");
        publicLessonButton = new JRadioButton("Group Lessons");
        lessonTypeGroup = new ButtonGroup();
        lessonTypeGroup.add(privateLessonButton);
        lessonTypeGroup.add(publicLessonButton);
        lessonChoicePanel.add(privateLessonButton);
        lessonChoicePanel.add(publicLessonButton);
        contentPanel.add(lessonChoicePanel, BorderLayout.NORTH);

        // Tableau pour afficher les leçons
        lessonTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(lessonTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Boutons en bas
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton deleteButton = new JButton("Delete Lesson");
        JButton createLessonButton = new JButton("Create Lesson");

        buttonPanel.add(backButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(createLessonButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les données initiales
        loadLessonData(false); // Afficher les leçons groupées par défaut

        // Action pour revenir à l'accueil
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));

        // Action pour supprimer une leçon
        deleteButton.addActionListener(e -> deleteSelectedLesson());

        // Action pour créer une nouvelle leçon
        createLessonButton.addActionListener(e -> openCreateLessonPage());

        // Actions pour changer entre les leçons privées et publiques
        privateLessonButton.addActionListener(e -> loadLessonData(true));
        publicLessonButton.addActionListener(e -> loadLessonData(false));
    }

    // Charger les leçons privées ou publiques
    private void loadLessonData(boolean isPrivate) {
        try {
            // Création du modèle de table
            String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date", "Age group"};
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
                    String ageGroup = lesson.getLessonType().getAgeGroup();

                    tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime, ageGroup});
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

    // Supprimer la leçon sélectionnée
 // Supprimer la leçon sélectionnée
    private void deleteSelectedLesson() {
        int selectedRow = lessonTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer la leçon sélectionnée
        Lesson selectedLesson = lessons.get(selectedRow);

        // Demander confirmation avant de supprimer
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this lesson?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            // Suppression de la leçon
            if (selectedLesson.deleteLesson()) {
                JOptionPane.showMessageDialog(this, "Lesson deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLessonData(privateLessonButton.isSelected());  // Recharger les données après la suppression
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete lesson", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Ouvrir le panneau pour créer une nouvelle leçon
    private void openCreateLessonPage() {
        CreateLessonPanel createLessonPanel = new CreateLessonPanel(cardLayout, mainPanel);
        mainPanel.add(createLessonPanel, "createLessonPanel");
        cardLayout.show(mainPanel, "createLessonPanel");
    }
}
