package be.alb.jframes;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import be.alb.models.Skier;


public class CreateSkierPanel extends JPanel {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField cityField;
    private JTextField postalCodeField;
    private JTextField streetNameField;
    private JTextField streetNumberField;
    private JDateChooser dobChooser;
    private JCheckBox insuranceCheckBox;

    public CreateSkierPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Skier", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 2, 10, 10));

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);

        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField();
        formPanel.add(cityLabel);
        formPanel.add(cityField);

        JLabel postalCodeLabel = new JLabel("Postal Code:");
        postalCodeField = new JTextField();
        formPanel.add(postalCodeLabel);
        formPanel.add(postalCodeField);

        JLabel streetNameLabel = new JLabel("Street Name:");
        streetNameField = new JTextField();
        formPanel.add(streetNameLabel);
        formPanel.add(streetNameField);

        JLabel streetNumberLabel = new JLabel("Street Number:");
        streetNumberField = new JTextField();
        formPanel.add(streetNumberLabel);
        formPanel.add(streetNumberField);

        JLabel dobLabel = new JLabel("Date of Birth:");
        dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("yyyy-MM-dd");
        formPanel.add(dobLabel);
        formPanel.add(dobChooser);

        JLabel insuranceLabel = new JLabel("Has Insurance:");
        insuranceCheckBox = new JCheckBox("Yes");
        formPanel.add(insuranceLabel);
        formPanel.add(insuranceCheckBox); 

        add(formPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("Create Skier");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSkier(cardLayout, mainPanel);
            }
        });

        add(submitButton, BorderLayout.SOUTH);

        JButton backButton = new JButton("Back to Manage Skiers");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "manageSkiersPanel");
            }
        });
        add(backButton, BorderLayout.NORTH);
    }

    // method to calculate category based on age
    private String calculateCategory(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age <= 12 ? "Enfant" : "Adulte";
    }

    private void createSkier(CardLayout cardLayout, JPanel mainPanel) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String streetName = streetNameField.getText();
        String streetNumber = streetNumberField.getText();

        java.util.Date dobDate = dobChooser.getDate();
        if (dobDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date of birth.");
            return;
        }
        LocalDate dob = dobDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        String category = calculateCategory(dob);

        Boolean hasInsurance = insuranceCheckBox.isSelected() ? true : false; // 1 for checked, 0 for unchecked

        List<String> result = Skier.createSkier(firstName, lastName, city, postalCode, streetName, streetNumber, dob, hasInsurance);

        if ("1".equals(result.get(0))) {
            JOptionPane.showMessageDialog(this, "Skier created successfully!");

            cardLayout.show(mainPanel, "manageSkiersPanel");
        } else {
            StringBuilder errorMessage = new StringBuilder("Error creating skier:\n");
            for (String error : result) {
                errorMessage.append(error).append("\n");
            }
            JOptionPane.showMessageDialog(this, errorMessage.toString());
        }
    }
}
