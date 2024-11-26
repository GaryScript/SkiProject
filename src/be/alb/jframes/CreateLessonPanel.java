package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import be.alb.models.LessonType;
import be.alb.enums.LessonTypeEnum;
import be.alb.models.Instructor;
import be.alb.models.Lesson;

public class CreateLessonPanel extends JPanel {
    private JComboBox<String> lessonTypeComboBox;
    private JComboBox<String> durationComboBox;
    private JSpinner dateSpinner;
    private JList<String> instructorList;
    private List<Instructor> availableInstructors; // Passée comme paramètre à submitLesson
    private List<LessonType> allLessonTypes; // Passée comme paramètre à submitLesson

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
        
        loadLessonTypes();

        // Duration Selection
        JLabel durationLabel = new JLabel("Select Duration:");
        add(durationLabel);

        durationComboBox = new JComboBox<>(new String[]{"Private - 1 Hour", "Private - 2 Hours", "Group - 6 Sessions"});
        add(durationComboBox);

        // Date Selection
        JLabel dateLabel = new JLabel("Select Date:");
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
        submitButton.addActionListener(e -> submitLesson(availableInstructors, allLessonTypes));
        add(submitButton);
       
        JButton loadInstructorsButton = new JButton("Load Available Instructors");
        loadInstructorsButton.addActionListener(e -> loadInstructors());
        add(loadInstructorsButton);
    }
    
    private void loadLessonTypes() {
        try {
            // Dynamically load lesson types from the model
            allLessonTypes = LessonType.getAllLessonTypes();

            // Populate the combo box with lesson type names
            lessonTypeComboBox.removeAllItems();
            for (LessonType lessonType : allLessonTypes) {
                lessonTypeComboBox.addItem(lessonType.getName());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load lesson types: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadInstructors() {
        try {
            // Get the selected lesson type and date
            String selectedLessonTypeName = (String) lessonTypeComboBox.getSelectedItem();
            if (selectedLessonTypeName == null) {
                JOptionPane.showMessageDialog(this, "Please select a lesson type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retrieve lesson type ID based on name
            LessonType lessonType = LessonType.getAllLessonTypes().stream()
                .filter(lt -> lt.getName().equals(selectedLessonTypeName))
                .findFirst().orElse(null);

            if (lessonType == null) {
                JOptionPane.showMessageDialog(this, "Invalid lesson type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

         // Get the date from the spinner
            Date selectedDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
            // Assuming a duration of 2 hours for simplicity (can be dynamic based on other fields)
            Date endDate = new Date(selectedDate.getTime() + (2L * 60 * 60 * 1000)); // Add 2 hours

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlStartDate = new java.sql.Date(selectedDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            // Fetch available instructors based on the selected date and lesson type
            availableInstructors = Instructor.getAvailableInstructors(sqlStartDate, sqlEndDate, lessonType.getLessonTypeId());


            // Populate instructor list
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Instructor instructor : availableInstructors) {
                model.addElement(instructor.getFirstName() + " " + instructor.getLastName());
            }
            instructorList.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load instructors: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitLesson(List<Instructor> availableInstructors, List<LessonType> lessonTypes) {
    	if (lessonTypes == null) {
    	    JOptionPane.showMessageDialog(this, "Lesson types are not loaded correctly.", "Error", JOptionPane.ERROR_MESSAGE);
    	    return;
    	  }
    	
    	// Collect and validate input data
        String selectedLessonTypeName = (String) lessonTypeComboBox.getSelectedItem();
        Date selectedDate = (Date) dateSpinner.getValue();
        String selectedInstructorName = instructorList.getSelectedValue();
        String selectedDuration = (String) durationComboBox.getSelectedItem();

        if (selectedLessonTypeName == null || selectedDate == null || selectedInstructorName == null || selectedDuration == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find LessonType object
        LessonType lessonType = lessonTypes.stream()
            .filter(lt -> lt.getName().equals(selectedLessonTypeName))
            .findFirst()
            .orElse(null);
        if (lessonType == null) {
            JOptionPane.showMessageDialog(this, "Invalid lesson type selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find Instructor object
        Instructor selectedInstructor = availableInstructors.stream()
            .filter(i -> (i.getFirstName() + " " + i.getLastName()).equals(selectedInstructorName))
            .findFirst()
            .orElse(null);
        if (selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Invalid instructor selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate startDate and endDate
        java.sql.Date startDate = new java.sql.Date(selectedDate.getTime());
        java.sql.Date endDate;

        if (selectedDuration.contains("Group")) {
            endDate = new java.sql.Date(selectedDate.getTime() + 3 * 24 * 60 * 60 * 1000L); // Add 3 days
        } else {
            endDate = calculatePrivateLessonEndDate(selectedDate, selectedDuration);
        }

        boolean success = Lesson.createLesson(
            startDate,
            endDate,
            selectedInstructor,
            lessonType,
            selectedDuration.contains("Private")
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Lesson created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create lesson.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private java.sql.Date calculatePrivateLessonEndDate(Date startDate, String selectedDuration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (selectedDuration.contains("1 Hour")) {
            calendar.add(Calendar.HOUR, 1); // End at 13h
        } else if (selectedDuration.contains("2 Hours")) {
            calendar.add(Calendar.HOUR, 2); // End at 14h
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }
    
 

}  