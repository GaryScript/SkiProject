package be.alb.jframes;

import be.alb.models.Booking;
import be.alb.models.Instructor;
import be.alb.models.Lesson;
import be.alb.models.Skier;
import be.alb.dao.SkierDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
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

        JLabel titleLabel = new JLabel("Create Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // main panel to chose between private and public lessons
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JPanel lessonChoicePanel = new JPanel();
        privateLessonButton = new JRadioButton("Private Lesson");
        publicLessonButton = new JRadioButton("Public Lesson");
        lessonTypeGroup = new ButtonGroup();
        lessonTypeGroup.add(privateLessonButton);
        lessonTypeGroup.add(publicLessonButton);

        lessonChoicePanel.add(privateLessonButton);
        lessonChoicePanel.add(publicLessonButton);

        contentPanel.add(lessonChoicePanel, BorderLayout.NORTH);

        // table to display lessons
        lessonTable = new JTable();
        JScrollPane scrollPaneLessons = new JScrollPane(lessonTable);
        contentPanel.add(scrollPaneLessons, BorderLayout.WEST);

        // table to display lessons skiers
        skierTable = new JTable();
        JScrollPane scrollPaneSkiers = new JScrollPane(skierTable);
        contentPanel.add(scrollPaneSkiers, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton submitButton = new JButton("Submit");
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSkierData();
        
        backButton.addActionListener(e -> {
       
            cardLayout.show(mainPanel, "manageBookingsPanel");
        });

        submitButton.addActionListener(e -> {
            int lessonRow = lessonTable.getSelectedRow();
            int skierRow = skierTable.getSelectedRow();

            if (lessonRow == -1 || skierRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select both a lesson and a skier", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Lesson selectedLesson = lessons.get(lessonRow);
            Skier selectedSkier = skiers.get(skierRow);

            boolean isPrivateLesson = privateLessonButton.isSelected();
            Date bookingDate = new Date(System.currentTimeMillis());
            boolean bookingSuccess;

            if (isPrivateLesson) {
                bookingSuccess = Booking.createPrivateBooking(selectedSkier, selectedLesson, bookingDate);
            } else {
                int selectedGroupId = selectedLesson.getLessonGroupId();

                // filter all lessons with same group id
                List<Lesson> groupLessons = lessons.stream()
                        .filter(lesson -> lesson.getLessonGroupId() == selectedGroupId)
                        .collect(Collectors.toList());

                if (groupLessons.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No lessons found for the selected group", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                bookingSuccess = Booking.createGroupBookings(selectedSkier, groupLessons, bookingDate);
            }

            if (bookingSuccess) {
                JOptionPane.showMessageDialog(this,
                    (isPrivateLesson ? "Private booking" : "Group booking") + " created for "
                    + selectedSkier.getFirstName() + " " + selectedSkier.getLastName() + " for the lesson " 
                    + selectedLesson.getLessonType().getName(),
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Booking creation failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // to load private or public lessons
        privateLessonButton.addActionListener(e -> loadLessonData(true)); 
        publicLessonButton.addActionListener(e -> loadLessonData(false));
    }

    private void loadLessonData(boolean isPrivate) {
        try {
            String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date", "Age group"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // formatter 
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
                        .collect(Collectors.groupingBy(Lesson::getLessonGroupId));  // regroup by lessongroupid thanks to a stream

                // display in the table
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

    private void loadSkierData() {
        try {
            skiers = Skier.getAllSkiers();

            String[] columnNames = {"First Name", "Last Name", "City", "DOB"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            for (Skier skier : skiers) {
                tableModel.addRow(new Object[]{skier.getFirstName(), skier.getLastName(), skier.getCity(), skier.getDob()});
            }

            skierTable.setModel(tableModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading skiers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
