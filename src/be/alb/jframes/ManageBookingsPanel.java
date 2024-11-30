package be.alb.jframes;

import be.alb.models.Booking;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageBookingsPanel extends JPanel {

    private Booking selectedBooking; // Stocke le booking sélectionné
    private JButton deleteBookingButton; // Bouton pour supprimer un booking

    public ManageBookingsPanel(CardLayout cardLayout, JPanel mainPanel) throws SQLException {
        setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel("Manage Bookings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Récupération des données directement dans le constructeur
        List<Booking> allBookings = Booking.getAllBookings(); // Appel direct au modèle
        List<Booking> publicBookings = getPublicBookings(allBookings);
        Map<Integer, List<Booking>> privateBookings = getPrivateBookings(allBookings);

        // Contenu principal : les onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet pour les bookings publics
        JPanel publicBookingsPanel = createBookingsTablePanel(publicBookings);
        tabbedPane.addTab("Public Bookings", publicBookingsPanel);

        // Onglet pour les bookings privés
        JPanel privateBookingsPanel = createGroupedBookingsTablePanel(privateBookings);
        tabbedPane.addTab("Private Bookings", privateBookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Section des boutons en bas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton "Create Booking"
        JButton createBookingButton = new JButton("Create Booking");
        createBookingButton.addActionListener(e -> {
            CreateBookingPanel createBookingPanel = new CreateBookingPanel(cardLayout, mainPanel);
            mainPanel.add(createBookingPanel, "createBookingPanel");
            cardLayout.show(mainPanel, "createBookingPanel");
        });
        buttonPanel.add(createBookingButton);

        // Bouton "Delete Booking"
        deleteBookingButton = new JButton("Delete Booking");
        deleteBookingButton.setEnabled(false); // Désactivé par défaut
        deleteBookingButton.addActionListener(e -> {
            if (selectedBooking != null) {
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    // Suppression du booking
                    selectedBooking.deleteBooking(); // Méthode à implémenter dans la classe Booking
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully.");
                    // Recharger la liste des bookings après suppression
                    refreshBookings();
                }
            }
        });
        buttonPanel.add(deleteBookingButton);

        // Bouton "Back to Menu"
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Filtre les bookings publics.
     */
    private List<Booking> getPublicBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> !booking.getLesson().isPrivate())
                .collect(Collectors.toList());
    }

    /**
     * Filtre et groupe les bookings privés par lessonGroupId.
     */
    private Map<Integer, List<Booking>> getPrivateBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getLesson().isPrivate())
                .collect(Collectors.groupingBy(booking -> booking.getLesson().getLessonGroupId()));
    }

    /**
     * Crée un panneau contenant un tableau pour afficher les bookings publics.
     */
    private JPanel createBookingsTablePanel(List<Booking> bookings) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Booking ID", "Lesson Type", "Instructor", "Skier", "Start Date", "End Date"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format avec heure

        bookings.forEach(booking -> {
            tableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getLesson().getLessonType().getName(),
                    booking.getInstructor().getFirstName(),
                    booking.getSkier().getFirstName(),
                    dateFormat.format(booking.getLesson().getStartDate()), // Formaté avec heure
                    dateFormat.format(booking.getLesson().getEndDate())   // Formaté avec heure
            });
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Écouteur pour la sélection d'une ligne dans la table
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Récupère le booking sélectionné à partir de la ligne
                    selectedBooking = bookings.get(selectedRow);
                    deleteBookingButton.setEnabled(true); // Active le bouton de suppression
                }
            }
        });

        return panel;
    }

    /**
     * Crée un panneau contenant un tableau pour afficher les bookings privés regroupés.
     */
    private JPanel createGroupedBookingsTablePanel(Map<Integer, List<Booking>> groupedBookings) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format avec heure

        groupedBookings.forEach((groupId, bookings) -> {
            String lessonType = bookings.get(0).getLesson().getLessonType().getName();
            String instructorName = bookings.get(0).getInstructor().getFirstName();
            String startDate = dateFormat.format(bookings.stream()
                    .map(booking -> booking.getLesson().getStartDate())
                    .min(java.util.Date::compareTo)
                    .orElse(null));
            String endDate = dateFormat.format(bookings.stream()
                    .map(booking -> booking.getLesson().getEndDate())
                    .max(java.util.Date::compareTo)
                    .orElse(null));

            tableModel.addRow(new Object[]{lessonType, instructorName, startDate, endDate});
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Recharge la liste des bookings et met à jour les tableaux après suppression.
     */
    private void refreshBookings() {
        try {
            // Recharger les bookings depuis la base de données
            List<Booking> allBookings = Booking.getAllBookings();
            List<Booking> publicBookings = getPublicBookings(allBookings);
            Map<Integer, List<Booking>> privateBookings = getPrivateBookings(allBookings);

            // Mettre à jour les tables
            // (Vous pouvez mettre à jour les panels ici selon vos besoins)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
