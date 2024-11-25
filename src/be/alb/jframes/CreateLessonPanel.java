package be.alb.jframes;

import java.awt.CardLayout;

import javax.swing.*;

public class CreateLessonPanel extends JPanel {

    public CreateLessonPanel(CardLayout cardLayout, JPanel mainPanel) {
        // Cette page va contenir les champs pour créer une leçon
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Ajoute des champs pour créer une leçon, comme un formulaire
        JLabel lessonLabel = new JLabel("Create a new lesson");
        JButton submitButton = new JButton("Submit");

        add(lessonLabel);
        add(submitButton);
    }
}
