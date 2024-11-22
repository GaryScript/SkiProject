package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import be.alb.models.Skier;
import com.toedter.calendar.JDateChooser;
import java.util.List;

public class CreateSkierPanel extends JPanel {

    // Declare all fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField cityField;
    private JTextField postalCodeField;
    private JTextField streetNameField;
    private JTextField streetNumberField;
    private JDateChooser dobChooser;

    public CreateSkierPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Add New Skier", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Back button to return to the main menu
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        add(backButton, BorderLayout.SOUTH);

        // Form panel to input skier data
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2, 10, 10));  // 8 rows, 2 columns, 10px gap

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

        add(formPanel, BorderLayout.CENTER);

        // Button to submit the form
        JButton submitButton = new JButton("Create Skier");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSkier();
            }
        });

        add(submitButton, BorderLayout.SOUTH);
    }

    // Method to create the skier
    private void createSkier() {
        // Retrieve form data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String streetName = streetNameField.getText();
        String streetNumber = streetNumberField.getText();

        // Get the date of birth from JCalendar
        java.util.Date dobDate = dobChooser.getDate();
        java.time.LocalDate dob = dobDate != null ? java.time.LocalDate.ofInstant(dobDate.toInstant(), java.time.ZoneId.systemDefault()) : null;

        // Call Skier model to create the skier
        List<String> result = Skier.createSkier(firstName, lastName, city, postalCode, streetName, streetNumber, dob);

        // Display result
        if ("1".equals(result.get(0))) {
            JOptionPane.showMessageDialog(this, "Skier created successfully!");
        } else {
            StringBuilder errorMessage = new StringBuilder("Error creating skier:\n");
            for (String error : result) {
                errorMessage.append(error).append("\n");
            }
            JOptionPane.showMessageDialog(this, errorMessage.toString());
        }
    }
}
