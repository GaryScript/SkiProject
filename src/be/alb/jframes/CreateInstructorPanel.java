package be.alb.jframes;

import be.alb.models.Accreditation;
import be.alb.models.Instructor;
import be.alb.dao.InstructorDAO;



import javax.swing.*;

import com.toedter.calendar.JCalendar;

import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CreateInstructorPanel extends JPanel {

    private JTextField lastNameField, firstNameField, cityField, postalCodeField, streetNameField, streetNumberField;
    private JCalendar dobCalendar;
    private List<JCheckBox> accreditationCheckBoxes;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel errorPanel; // Container for errors

    public CreateInstructorPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout()); // Use BorderLayout for better section management
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10)); // GridLayout for form fields

        // Initialize fields
        lastNameField = new JTextField();
        firstNameField = new JTextField();
        cityField = new JTextField();
        postalCodeField = new JTextField();
        streetNameField = new JTextField();
        streetNumberField = new JTextField();
        dobCalendar = new JCalendar();

        // Create checkboxes for accreditations
        List<Accreditation> accreditations = Accreditation.getAllAccreditations();
        accreditationCheckBoxes = new ArrayList<>();
        JPanel accreditationPanel = new JPanel();
        accreditationPanel.setLayout(new BoxLayout(accreditationPanel, BoxLayout.Y_AXIS));
        for (Accreditation accreditation : accreditations) {
            JCheckBox checkBox = new JCheckBox(accreditation.getName());
            accreditationCheckBoxes.add(checkBox);
            accreditationPanel.add(checkBox);
        }

        // Add components to the form
        formPanel.add(new JLabel("Lastname:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Firstname:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        formPanel.add(new JLabel("Postal code:"));
        formPanel.add(postalCodeField);
        formPanel.add(new JLabel("Street name"));
        formPanel.add(streetNameField);
        formPanel.add(new JLabel("Street number:"));
        formPanel.add(streetNumberField);
        formPanel.add(new JLabel("DOB:"));
        formPanel.add(dobCalendar);
        formPanel.add(new JLabel("Accreditations:"));
        formPanel.add(new JScrollPane(accreditationPanel));

        // Submit button
        JButton submitButton = new JButton("Create instructor");
        submitButton.addActionListener(e -> createInstructor());
        formPanel.add(submitButton);

        // Back button
        JButton backButton = new JButton("Go back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "manageInstructorsPanel"));
        formPanel.add(backButton);

        // Error container
        errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS)); 
        errorPanel.setVisible(false); // Hide errors by default

        // Add form and error panel to the main layout
        add(formPanel, BorderLayout.CENTER);
        add(errorPanel, BorderLayout.NORTH);
    }

    private void createInstructor() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String city = cityField.getText().trim();
        String postalCode = postalCodeField.getText().trim();
        String streetName = streetNameField.getText().trim();
        String streetNumber = streetNumberField.getText().trim();
        LocalDate dob = extractDateOnly(dobCalendar.getDate());  // date extraction

        // get the checked accreditations
        List<Accreditation> selectedAccreditations = new ArrayList<>();
        for (int i = 0; i < accreditationCheckBoxes.size(); i++) {
            JCheckBox checkBox = accreditationCheckBoxes.get(i);
            if (checkBox.isSelected()) {
                Accreditation accreditation = Accreditation.getAllAccreditations().get(i);
                selectedAccreditations.add(accreditation);
            }
        }

        // create instructor with accreditations
        List<String> result = Instructor.createInstructor(firstName, lastName, city, postalCode, streetName, streetNumber, dob, selectedAccreditations);

        if (result.get(0).equals("1")) {
            JOptionPane.showMessageDialog(this, "Instructor created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            goToManageInstructorPanel();
        } else {
            StringBuilder errorMessage = new StringBuilder("An error occurred:\n");
            for (int i = 1; i < result.size(); i++) {
                errorMessage.append(result.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(this, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // method to navigate back to the manage instructors panel
    private void goToManageInstructorPanel() {
        ManageInstructorsPanel manageInstructorsPanel = new ManageInstructorsPanel(cardLayout, mainPanel);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(manageInstructorsPanel);
        frame.revalidate();
    }
    
    private LocalDate extractDateOnly(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Please select a valid date.");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
