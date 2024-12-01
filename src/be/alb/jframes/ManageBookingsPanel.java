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

    private Booking selectedBooking; 
    private JButton deleteBookingButton; 

    public ManageBookingsPanel(CardLayout cardLayout, JPanel mainPanel) throws SQLException {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Bookings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);


        List<Booking> allBookings = Booking.getAllBookings(); 
        List<Booking> publicBookings = getPublicBookings(allBookings);
        Map<Integer, List<Booking>> privateBookings = getPrivateBookings(allBookings);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel publicBookingsPanel = createBookingsTablePanel(publicBookings);
        tabbedPane.addTab("Public Bookings", publicBookingsPanel);

        JPanel privateBookingsPanel = createGroupedBookingsTablePanel(privateBookings);
        tabbedPane.addTab("Private Bookings", privateBookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton createBookingButton = new JButton("Create Booking");
        createBookingButton.addActionListener(e -> {
            CreateBookingPanel createBookingPanel = new CreateBookingPanel(cardLayout, mainPanel);
            mainPanel.add(createBookingPanel, "createBookingPanel");
            cardLayout.show(mainPanel, "createBookingPanel");
        });
        buttonPanel.add(createBookingButton);

        deleteBookingButton = new JButton("Delete Booking");
        deleteBookingButton.setEnabled(false); 
        deleteBookingButton.addActionListener(e -> {
            if (selectedBooking != null) {
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    selectedBooking.deleteBooking();
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully.");
                    //refreshBookings();
                }
            }
        });
        buttonPanel.add(deleteBookingButton);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menuPanel"));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private List<Booking> getPublicBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> !booking.getLesson().isPrivate())
                .collect(Collectors.toList());
    }

    private Map<Integer, List<Booking>> getPrivateBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getLesson().isPrivate())
                .collect(Collectors.groupingBy(booking -> booking.getLesson().getLessonGroupId()));
    }

    private JPanel createBookingsTablePanel(List<Booking> bookings) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Booking ID", "Lesson Type", "Instructor", "Skier", "Start Date", "End Date", "Price"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bookings.forEach(booking -> {
            tableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getLesson().getLessonType().getName(),
                    booking.getInstructor().getFirstName(),
                    booking.getSkier().getFirstName(),
                    dateFormat.format(booking.getLesson().getStartDate()),
                    dateFormat.format(booking.getLesson().getEndDate()),
                    booking.calculatePrice()
            });
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedBooking = bookings.get(selectedRow);
                    deleteBookingButton.setEnabled(true);
                }
            }
        });

        return panel;
    }

    private JPanel createGroupedBookingsTablePanel(Map<Integer, List<Booking>> groupedBookings) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Lesson Type", "Instructor", "Start Date", "End Date", "Price"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            double totalPrice = bookings.stream().mapToDouble(Booking::calculatePrice).sum();

            tableModel.addRow(new Object[]{lessonType, instructorName, startDate, endDate, totalPrice});
        });

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

}
