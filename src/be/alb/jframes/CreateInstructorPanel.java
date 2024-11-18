package be.alb.jframes;

import be.alb.models.Accreditation;
import be.alb.models.Instructor;
import be.alb.dao.InstructorDAO;

import com.toedter.calendar.JCalendar;

import javax.swing.*;
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
        formPanel.add(new JLabel("Nom:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Prénom:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Ville:"));
        formPanel.add(cityField);
        formPanel.add(new JLabel("Code Postal:"));
        formPanel.add(postalCodeField);
        formPanel.add(new JLabel("Nom de rue:"));
        formPanel.add(streetNameField);
        formPanel.add(new JLabel("Numéro de rue:"));
        formPanel.add(streetNumberField);
        formPanel.add(new JLabel("Date de naissance:"));
        formPanel.add(dobCalendar);
        formPanel.add(new JLabel("Accréditations:"));
        formPanel.add(new JScrollPane(accreditationPanel));

        // Submit button
        JButton submitButton = new JButton("Créer l'instructeur");
        submitButton.addActionListener(e -> createInstructor());
        formPanel.add(submitButton);

        // Back button
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "manageInstructorsPanel"));
        formPanel.add(backButton);

        // Error container
        errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS)); // Stack errors vertically
        errorPanel.setVisible(false); // Hide errors by default

        // Add form and error panel to the main layout
        add(formPanel, BorderLayout.CENTER);
        add(errorPanel, BorderLayout.NORTH);
    }

    private void createInstructor() {
        try {
            // Collect data from the form
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String city = cityField.getText().trim();
            String postalCode = postalCodeField.getText().trim();
            String streetName = streetNameField.getText().trim();
            String streetNumber = streetNumberField.getText().trim();
            LocalDate dob = extractDateOnly(dobCalendar.getDate()); // Extract the date cleanly

            // Business logic
            List<String> result = Instructor.createInstructor(firstName, lastName, city, postalCode, streetName, streetNumber, dob);

            if (result.get(0).equals("1")) {
                JOptionPane.showMessageDialog(this, "Instructor created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                goToManageInstructorPanel();
            } else {
                result.remove(0);
                displayFieldErrors(result);
            }
        } catch (IllegalArgumentException e) {
            displayFieldErrors(List.of(e.getMessage()));
        }
    }

    // Method to navigate back to the manage instructors panel
    private void goToManageInstructorPanel() {
        ManageInstructorsPanel manageInstructorsPanel = new ManageInstructorsPanel(cardLayout, mainPanel);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(manageInstructorsPanel);
        frame.revalidate();
    }

    // Display errors above the relevant form fields
    private void displayFieldErrors(List<String> errors) {
        // Hide error panel initially
        errorPanel.removeAll();
        errorPanel.setVisible(true);

        // Display errors for each field
        for (String error : errors) {
            JLabel errorLabel = new JLabel(error);
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Increased font size and bold for better visibility
            errorLabel.setOpaque(true); // Make background visible
            errorLabel.setBackground(new Color(255, 240, 240)); // Soft red background
            errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding around error text
            errorPanel.add(errorLabel);
        }

        // Revalidate and repaint to ensure errors are displayed correctly
        errorPanel.revalidate();
        errorPanel.repaint();
    }
    
    private LocalDate extractDateOnly(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Please select a valid date.");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
