package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import be.alb.models.LessonType;
import be.alb.enums.LessonTypeEnum;
import be.alb.models.Instructor;
import be.alb.models.Lesson;

public class CreateLessonPanel extends JPanel {
    private JComboBox<String> lessonTypeComboBox;
    private JComboBox<String> durationComboBox;
    private JSpinner dateSpinner;
    private JList<String> instructorList;
    private List<Instructor> availableInstructors;
    private List<LessonType> allLessonTypes;

    public CreateLessonPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title
        JLabel lessonLabel = new JLabel("Create a new lesson");
        lessonLabel.setFont(new Font("Arial", Font.BOLD, 18));
        lessonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lessonLabel);

        // Lesson Type Selection
        JLabel typeLabel = new JLabel("Select Lesson Type:");
        add(typeLabel);

        lessonTypeComboBox = new JComboBox<>();
        add(lessonTypeComboBox);

        // Duration Selection
        JLabel durationLabel = new JLabel("Select Duration:");
        add(durationLabel);

        durationComboBox = new JComboBox<>(new String[]{"Private - 1 Hour", "Private - 2 Hours", "Group - 6 Sessions"});
        add(durationComboBox);

        // Date Selection
        JLabel dateLabel = new JLabel("Select Start Date:");
        add(dateLabel);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        add(dateSpinner);

        // Instructor Selection
        JLabel instructorLabel = new JLabel("Select Available Instructor:");
        add(instructorLabel);

        instructorList = new JList<>();
        JScrollPane instructorScrollPane = new JScrollPane(instructorList);
        add(instructorScrollPane);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitLesson());
        add(submitButton);
    }

    public void populateData(List<Instructor> instructors, List<LessonType> lessonTypes) {
        this.availableInstructors = instructors;
        this.allLessonTypes = lessonTypes;

        // Populate lesson type combo box
        lessonTypeComboBox.removeAllItems();
        for (LessonType lessonType : lessonTypes) {
            lessonTypeComboBox.addItem(lessonType.getName());
        }

        // Populate instructor list
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Instructor instructor : instructors) {
            model.addElement(instructor.getFirstName() + " " + instructor.getLastName());
        }
        instructorList.setModel(model);
    }

    private void submitLesson() {
        // Collect and validate input data
        String selectedLessonTypeName = (String) lessonTypeComboBox.getSelectedItem();
        Date selectedDate = (Date) dateSpinner.getValue();
        String selectedInstructorName = instructorList.getSelectedValue();
        String selectedDuration = (String) durationComboBox.getSelectedItem();

        if (selectedLessonTypeName == null || selectedDate == null || selectedInstructorName == null || selectedDuration == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LessonType lessonType = allLessonTypes.stream()
            .filter(lt -> lt.getName().equals(selectedLessonTypeName))
            .findFirst()
            .orElse(null);
        if (lessonType == null) {
            JOptionPane.showMessageDialog(this, "Invalid lesson type selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Instructor selectedInstructor = availableInstructors.stream()
            .filter(i -> (i.getFirstName() + " " + i.getLastName()).equals(selectedInstructorName))
            .findFirst()
            .orElse(null);
        if (selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Invalid instructor selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isPrivate = selectedDuration.contains("Private");

        // If group lesson, create 6 lessons
        if (!isPrivate) {
            createGroupLessons(selectedDate, selectedInstructor, lessonType);
        } else {
            // Create single private lesson
            java.sql.Date startDate = new java.sql.Date(selectedDate.getTime());
            java.sql.Date endDate = calculatePrivateLessonEndDate(selectedDate, selectedDuration);

            boolean success = Lesson.createLesson(startDate, endDate, selectedInstructor, lessonType, true, LessonTypeEnum.valueOf(lessonType.getName().toUpperCase()));
            showCreationMessage(success, "Private");
        }
    }

    private void createGroupLessons(Date selectedDate, Instructor instructor, LessonType lessonType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        boolean success = true;

        for (int i = 0; i < 3; i++) { // 3 days
            // Morning session
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            java.sql.Date startMorning = new java.sql.Date(calendar.getTimeInMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            java.sql.Date endMorning = new java.sql.Date(calendar.getTimeInMillis());

            success &= Lesson.createLesson(startMorning, endMorning, instructor, lessonType, false, LessonTypeEnum.valueOf(lessonType.getName().toUpperCase()));

            // Afternoon session
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            java.sql.Date startAfternoon = new java.sql.Date(calendar.getTimeInMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            java.sql.Date endAfternoon = new java.sql.Date(calendar.getTimeInMillis());

            success &= Lesson.createLesson(startAfternoon, endAfternoon, instructor, lessonType, false, LessonTypeEnum.valueOf(lessonType.getName().toUpperCase()));

            calendar.add(Calendar.DATE, 1); // Next day
        }

        showCreationMessage(success, "Group");
    }

    private java.sql.Date calculatePrivateLessonEndDate(Date startDate, String selectedDuration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 12); // Default starting hour for private lessons

        if (selectedDuration.contains("1 Hour")) {
            calendar.add(Calendar.HOUR, 1);
        } else if (selectedDuration.contains("2 Hours")) {
            calendar.add(Calendar.HOUR, 2);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    private void showCreationMessage(boolean success, String lessonType) {
        if (success) {
            JOptionPane.showMessageDialog(this, lessonType + " lesson(s) created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create " + lessonType + " lesson(s).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
