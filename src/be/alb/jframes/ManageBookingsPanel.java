package be.alb.jframes;

import javax.swing.*;
import java.awt.*;

public class ManageBookingsPanel extends JPanel {

    public ManageBookingsPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel("Manage Bookings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Contenu principal
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBookingButton = new JButton("Create Booking");

        // Action du bouton pour ouvrir CreateBookingPanel
        createBookingButton.addActionListener(e -> {
            CreateBookingPanel createBookingPanel = new CreateBookingPanel(cardLayout, mainPanel);
            mainPanel.add(createBookingPanel, "createBookingPanel");
            cardLayout.show(mainPanel, "createBookingPanel");
        });

        buttonPanel.add(createBookingButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Bouton retour
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        add(backButton, BorderLayout.SOUTH);
    }
}
