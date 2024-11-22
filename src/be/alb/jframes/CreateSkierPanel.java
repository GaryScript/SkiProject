package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import be.alb.models.Skier;
import com.toedter.calendar.JDateChooser;

public class CreateSkierPanel extends JPanel {

    // Declare all fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField cityField;
    private JTextField postalCodeField;
    private JTextField streetNameField;
    private JTextField streetNumberField;
    private JDateChooser dobChooser;
    private JCheckBox insuranceCheckBox; // Add the checkbox for insurance

    public CreateSkierPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Add New Skier", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Form panel to input skier data
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 2, 10, 10)); // Update grid to 10 rows

        // First Name
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);

        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);

        // City
        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField();
        formPanel.add(cityLabel);
        formPanel.add(cityField);

        // Postal Code
        JLabel postalCodeLabel = new JLabel("Postal Code:");
        postalCodeField = new JTextField();
        formPanel.add(postalCodeLabel);
        formPanel.add(postalCodeField);

        // Street Name
        JLabel streetNameLabel = new JLabel("Street Name:");
        streetNameField = new JTextField();
        formPanel.add(streetNameLabel);
        formPanel.add(streetNameField);

        // Street Number
        JLabel streetNumberLabel = new JLabel("Street Number:");
        streetNumberField = new JTextField();
        formPanel.add(streetNumberLabel);
        formPanel.add(streetNumberField);

        // Date of Birth
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("yyyy-MM-dd");
        formPanel.add(dobLabel);
        formPanel.add(dobChooser);

        // Insurance Checkbox
        JLabel insuranceLabel = new JLabel("Has Insurance:");
        insuranceCheckBox = new JCheckBox("Yes");
        formPanel.add(insuranceLabel);
        formPanel.add(insuranceCheckBox); // Add the checkbox for insurance

        // Add form panel to main layout
        add(formPanel, BorderLayout.CENTER);

        // Button to submit the form
        JButton submitButton = new JButton("Create Skier");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSkier(cardLayout, mainPanel);
            }
        });

        add(submitButton, BorderLayout.SOUTH);

        // Add a "Back to Manage Skiers" button
        JButton backButton = new JButton("Back to Manage Skiers");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Return to ManageSkiersPanel
                cardLayout.show(mainPanel, "manageSkiersPanel");
            }
        });
        add(backButton, BorderLayout.NORTH);
    }

    // Method to calculate category based on age
    private String calculateCategory(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age <= 12 ? "Enfant" : "Adulte";
    }

    // Method to create the skier
    private void createSkier(CardLayout cardLayout, JPanel mainPanel) {
        // Retrieve form data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String streetName = streetNameField.getText();
        String streetNumber = streetNumberField.getText();

        // Get the date of birth from JCalendar
        java.util.Date dobDate = dobChooser.getDate();
        if (dobDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date of birth.");
            return;
        }
        LocalDate dob = dobDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Calculate category based on age
        String category = calculateCategory(dob);

        // Get the insurance status from the checkbox
        Boolean hasInsurance = insuranceCheckBox.isSelected() ? true : false; // 1 for checked, 0 for unchecked

        // Call Skier model to create the skier
        List<String> result = Skier.createSkier(firstName, lastName, city, postalCode, streetName, streetNumber, dob, hasInsurance);

        if ("1".equals(result.get(0))) {
            JOptionPane.showMessageDialog(this, "Skier created successfully!");

            // Redirect to ManageSkiersPanel
            cardLayout.show(mainPanel, "manageSkiersPanel");
        } else {
            // Display errors
            StringBuilder errorMessage = new StringBuilder("Error creating skier:\n");
            for (String error : result) {
                errorMessage.append(error).append("\n");
            }
            JOptionPane.showMessageDialog(this, errorMessage.toString());
        }
    }
}
