package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class ManageLessonsPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ManageLessonsPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        // Bouton pour aller à CreateLessonPanel
        JButton createLessonButton = new JButton("Create Lesson");
        createLessonButton.addActionListener(e -> openCreateLessonPage());

        add(createLessonButton, BorderLayout.CENTER);
    }

    private void openCreateLessonPage() {
        CreateLessonPanel createLessonPanel = new CreateLessonPanel(cardLayout, mainPanel);
        mainPanel.add(createLessonPanel, "createLessonPanel");
        cardLayout.show(mainPanel, "createLessonPanel");
    }
}
