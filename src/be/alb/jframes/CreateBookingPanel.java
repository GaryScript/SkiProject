package be.alb.jframes;

import be.alb.models.Lesson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CreateBookingPanel extends JPanel {

    private JTable lessonTable;

    public CreateBookingPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Titre du panel
        JLabel titleLabel = new JLabel("Create Lessons", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Tableau pour afficher les leçons
        lessonTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(lessonTable);
        add(scrollPane, BorderLayout.CENTER);

        // Charger les données des leçons
        loadLessonData();
    }

    private void loadLessonData() {
        try {
            // Appel à la méthode getAllLessons
            List<Lesson> lessons = Lesson.getAllLessons();

            // Vérifier si la liste est vide ou nulle
            if (lessons == null || lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No lessons found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Configuration des colonnes
            String[] columnNames = {"Lesson Type", "Instructor", "Start Time", "End Time", "Private"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Ajouter les données au modèle
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (Lesson lesson : lessons) {
                String lessonTypeName = lesson.getLessonType().getName();
                String instructorName = lesson.getInstructor() != null
                        ? lesson.getInstructor().getFirstName() + " " + lesson.getInstructor().getLastName()
                        : "None";
                String startTime = dateFormat.format(lesson.getStartDate());
                String endTime = dateFormat.format(lesson.getEndDate());
                String isPrivate = lesson.isPrivate() ? "Yes" : "No";

                tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime, isPrivate});
            }

            // Appliquer le modèle au tableau
            lessonTable.setModel(tableModel);

        } catch (Exception e) {
            // Afficher l'exception complète dans la console pour le débogage
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading lessons: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
