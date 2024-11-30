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

        JLabel titleLabel = new JLabel("Manage Lessons", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());

        JPanel lessonChoicePanel = new JPanel();
        privateLessonButton = new JRadioButton("Private Lessons");
        publicLessonButton = new JRadioButton("Group Lessons");
        lessonTypeGroup = new ButtonGroup();
        lessonTypeGroup.add(privateLessonButton);
        lessonTypeGroup.add(publicLessonButton);
        lessonChoicePanel.add(privateLessonButton);
        lessonChoicePanel.add(publicLessonButton);
        contentPanel.add(lessonChoicePanel, BorderLayout.NORTH);

        lessonTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(lessonTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton deleteButton = new JButton("Delete Lesson");
        JButton createLessonButton = new JButton("Create Lesson");

        buttonPanel.add(backButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(createLessonButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadLessonData(false); 
  
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));

        deleteButton.addActionListener(e -> deleteSelectedLesson());

        createLessonButton.addActionListener(e -> openCreateLessonPage());

        privateLessonButton.addActionListener(e -> loadLessonData(true));
        publicLessonButton.addActionListener(e -> loadLessonData(false));
    }

    private void loadLessonData(boolean isPrivate) {
        try {

            String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date", "Age group"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            if (isPrivate) {
                lessons = Lesson.getAllPrivateLessons(); 
                for (Lesson lesson : lessons) {
                    String lessonTypeName = lesson.getLessonType().getName();
                    String instructorName = lesson.getInstructor() != null
                            ? lesson.getInstructor().getFirstName() + " " + lesson.getInstructor().getLastName()
                            : "None";

                    String startTime = dateTimeFormat.format(lesson.getStartDate());
                    String endTime = dateTimeFormat.format(lesson.getEndDate());
                    String ageGroup = lesson.getLessonType().getAgeGroup();

                    tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime, ageGroup});
                }
            } else {
                lessons = Lesson.getAllPublicLessons();
                Map<Integer, List<Lesson>> groupedLessons = lessons.stream()
                        .collect(Collectors.groupingBy(Lesson::getLessonGroupId));  

                for (Map.Entry<Integer, List<Lesson>> entry : groupedLessons.entrySet()) {
                    List<Lesson> groupLessons = entry.getValue();
                    Lesson firstLesson = groupLessons.get(0);  
                    String lessonTypeName = firstLesson.getLessonType().getName();
                    String instructorName = firstLesson.getInstructor() != null
                            ? firstLesson.getInstructor().getFirstName() + " " + firstLesson.getInstructor().getLastName()
                            : "None";

                    String startTime = dateTimeFormat.format(firstLesson.getStartDate());
                    String endTime = dateTimeFormat.format(groupLessons.get(groupLessons.size() - 1).getEndDate());  

                    tableModel.addRow(new Object[]{lessonTypeName, instructorName, startTime, endTime});
                }
            }

            lessonTable.setModel(tableModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lessons: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedLesson() {
        int selectedRow = lessonTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Lesson selectedLesson = lessons.get(selectedRow);

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this lesson?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            if (selectedLesson.deleteLesson()) {
                JOptionPane.showMessageDialog(this, "Lesson deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLessonData(privateLessonButton.isSelected()); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete lesson", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openCreateLessonPage() {
        CreateLessonPanel createLessonPanel = new CreateLessonPanel(cardLayout, mainPanel);
        mainPanel.add(createLessonPanel, "createLessonPanel");
        cardLayout.show(mainPanel, "createLessonPanel");
    }
}
