package be.alb.jframes;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import be.alb.models.Skier;

public class ManageSkiersPanel extends JPanel {

    public ManageSkiersPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Manage Skiers", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Back button to return to the main menu
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        add(backButton, BorderLayout.SOUTH);

        // Create a table to display the skiers
        String[] columns = {"ID", "Last Name", "First Name", "City"};
        List<Skier> skiers = Skier.getAllSkiers();
        
        Object[][] data = new Object[skiers.size()][4];
        
        // Fill the table with skier data
        for (int i = 0; i < skiers.size(); i++) {
            Skier skier = skiers.get(i);
            data[i][0] = skier.getId();
            data[i][1] = skier.getLastName();
            data[i][2] = skier.getFirstName();
            data[i][3] = skier.getCity();
        }
        
        // Create the table with skiers data
        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
