package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class ManageSkiersPanel extends JPanel {

    public ManageSkiersPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Skiers", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        add(backButton, BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel();
        contentPanel.add(new JLabel("Liste des skieurs à venir..."));
        add(contentPanel, BorderLayout.CENTER);
    }
}
