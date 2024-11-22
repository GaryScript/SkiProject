package be.alb.jframes;

import be.alb.models.Skier;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ManageSkiersPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ManageSkiersPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        List<Skier> skiers = Skier.getAllSkiers();

        if (skiers == null || skiers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No skiers found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"ID", "Nom", "Prénom", "Ville", "Catégorie"};
        Object[][] data = new Object[skiers.size()][columnNames.length];

        for (int i = 0; i < skiers.size(); i++) {
            Skier skier = skiers.get(i);
            data[i][0] = skier.getId();
            data[i][1] = skier.getLastName();
            data[i][2] = skier.getFirstName();
            data[i][3] = skier.getCity();
            data[i][4] = calculateCategory(skiers.get(i).getDob()); // Calculer la catégorie ici
        }

        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Button to create a new skier
        JButton createButton = new JButton("Créer un nouveau skieur");
        createButton.addActionListener(e -> {
            CreateSkierPanel createSkierPanel = new CreateSkierPanel(cardLayout, mainPanel);
            mainPanel.add(createSkierPanel, "CreateSkierPanel");
            cardLayout.show(mainPanel, "CreateSkierPanel");
        });
        buttonPanel.add(createButton);

        // Button to go back to the main menu
        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    // Méthode pour calculer la catégorie en fonction de l'âge
    private String calculateCategory(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age <= 12 ? "Enfant" : "Adulte";
    }

}

