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
    private List<Skier> skiers; 
    private JTable table; 

    public ManageSkiersPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        loadSkiers();

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
            data[i][4] = calculateCategory(skier.getDob());
        }

        table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("Créer un nouveau skieur");
        createButton.addActionListener(e -> {
            CreateSkierPanel createSkierPanel = new CreateSkierPanel(cardLayout, mainPanel);
            mainPanel.add(createSkierPanel, "CreateSkierPanel");
            cardLayout.show(mainPanel, "CreateSkierPanel");
        });
        buttonPanel.add(createButton);
        
        JButton deleteButton = new JButton("Supprimer un skieur");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int skierId = (int) table.getValueAt(selectedRow, 0); 
                Skier skier = getSkierById(skierId); 
                if (skier != null) {
                    deleteSkier(skier); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un skieur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
    }
    private void loadSkiers() {
        this.skiers = Skier.getAllSkiers();
    }

    private Skier getSkierById(int id) {
        for (Skier skier : skiers) {
            if (skier.getId() == id) {
                return skier;
            }
        }
        return null;
    }

    public void deleteSkier(Skier skier) {
        if (skier.deleteSkier()) {
            skiers.remove(skier);
            JOptionPane.showMessageDialog(this, "Skieur supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(); 
        } else {
            JOptionPane.showMessageDialog(this, "Échec de la suppression du skieur.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        String[] columnNames = {"ID", "Nom", "Prénom", "Ville", "Catégorie"};
        Object[][] data = new Object[skiers.size()][columnNames.length];

        for (int i = 0; i < skiers.size(); i++) {
            Skier skier = skiers.get(i);
            data[i][0] = skier.getId();
            data[i][1] = skier.getLastName();
            data[i][2] = skier.getFirstName();
            data[i][3] = skier.getCity();
            data[i][4] = calculateCategory(skier.getDob());
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames)); 
        revalidate(); 
        repaint(); 
    }

    private String calculateCategory(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age <= 12 ? "Enfant" : "Adulte";
    }
}
