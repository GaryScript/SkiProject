package be.alb.jframes;

import be.alb.models.Instructor;
import be.alb.models.Accreditation;
import be.alb.dao.AccreditationDAO;
import be.alb.dao.InstructorDAO;
import javax.swing.*;

import com.toedter.calendar.JCalendar;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateInstructorPanel extends JPanel {

    private JTextField lastNameField, firstNameField, cityField, postalCodeField, streetNameField, streetNumberField;
    private JCalendar dobCalendar;
    private List<JCheckBox> accreditationCheckBoxes;
    
    public CreateInstructorPanel() {
        setLayout(new GridLayout(9, 2, 10, 10)); // GridLayout for the form

        // Add fields for instructor details
        lastNameField = new JTextField();
        firstNameField = new JTextField();
        cityField = new JTextField();
        postalCodeField = new JTextField();
        streetNameField = new JTextField();
        streetNumberField = new JTextField();
        
        dobCalendar = new JCalendar(); // Using JCalendar for date of birth
        
        // Load all accreditations from the database
        List<Accreditation> accreditations = Accreditation.getAllAccreditations();
        accreditationCheckBoxes = new ArrayList<>();
        
        // Create checkboxes for each accreditation
        JPanel accreditationPanel = new JPanel();
        accreditationPanel.setLayout(new BoxLayout(accreditationPanel, BoxLayout.Y_AXIS));
        for (Accreditation accreditation : accreditations) {
            JCheckBox checkBox = new JCheckBox(accreditation.getName());
            accreditationCheckBoxes.add(checkBox);
            accreditationPanel.add(checkBox);
        }

        // Add all components to the form
        add(new JLabel("Nom:"));
        add(lastNameField);

        add(new JLabel("Prénom:"));
        add(firstNameField);

        add(new JLabel("Ville:"));
        add(cityField);

        add(new JLabel("Code Postal:"));
        add(postalCodeField);

        add(new JLabel("Nom de rue:"));
        add(streetNameField);

        add(new JLabel("Numéro de rue:"));
        add(streetNumberField);

        add(new JLabel("Date de naissance:"));
        add(dobCalendar);

        add(new JLabel("Accréditations:"));
        add(accreditationPanel);

        // Submit button
        JButton submitButton = new JButton("Créer l'instructeur");
        submitButton.addActionListener(e -> createInstructor());
        add(submitButton);
    }

    private void createInstructor() {
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String streetName = streetNameField.getText();
        String streetNumber = streetNumberField.getText();
        java.util.Date dobDate = dobCalendar.getDate();
        LocalDate dob = new java.sql.Date(dobDate.getTime()).toLocalDate(); // Convertir en LocalDate

 
        List<String> errors = Instructor.createInstructor(0, firstName, lastName, city, postalCode, streetName, streetNumber, dob);

        if (!errors.isEmpty()) {
 
            String errorMessage = String.join("\n", errors);
            JOptionPane.showMessageDialog(this, errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Instructor createdInstructor = InstructorDAO.getInstructorByName(firstName, lastName);

        List<Accreditation> selectedAccreditations = new ArrayList<>();
        for (int i = 0; i < accreditationCheckBoxes.size(); i++) {
            if (accreditationCheckBoxes.get(i).isSelected()) {
                selectedAccreditations.add(AccreditationDAO.getAccreditationById(i + 1)); 
            }
        }

        // Ajouter les accréditations à l'instructeur dans la base de données
        for (Accreditation accreditation : selectedAccreditations) {
            InstructorDAO.addAccreditationToInstructor(createdInstructor.getId(), accreditation.getId());
        }

        JOptionPane.showMessageDialog(this, "Instructeur créé avec succès !");
    }

}
