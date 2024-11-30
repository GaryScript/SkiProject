package be.alb.jframes;

import be.alb.models.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageBookingsPanel extends JPanel {

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
        bookings.forEach(booking -> {
            tableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getLesson().getLessonType().getName(),
                    booking.getInstructor().getFirstName(),
                    booking.getSkier().getFirstName(),
                    booking.getLesson().getStartDate(),
                    booking.getLesson().getEndDate()
            });
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un panneau contenant un tableau pour afficher les bookings privés regroupés.
     */
    private JPanel createGroupedBookingsTablePanel(Map<Integer, List<Booking>> groupedBookings) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Lesson Group ID", "Lesson Type", "Instructor", "Start Date", "End Date"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        groupedBookings.forEach((groupId, bookings) -> {
            String lessonType = bookings.get(0).getLesson().getLessonType().getName();
            String instructorName = bookings.get(0).getInstructor().getFirstName();
            String startDate = bookings.stream()
                    .map(booking -> booking.getLesson().getStartDate())
                    .min(java.util.Date::compareTo)
                    .orElse(null)
                    .toString();
            String endDate = bookings.stream()
                    .map(booking -> booking.getLesson().getEndDate())
                    .max(java.util.Date::compareTo)
                    .orElse(null)
                    .toString();

            tableModel.addRow(new Object[]{groupId, lessonType, instructorName, startDate, endDate});
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
