package be.alb.dao;

import be.alb.models.Skier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkierDAO implements SkierDAOInterface {

    private Connection connection;

    public SkierDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Skier> getAllSkiers() throws SQLException {
        List<Skier> skiers = new ArrayList<>();
        String query = "SELECT * FROM Skiers";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Skier skier = new Skier(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("city"),
                    rs.getString("postal_code"),
                    rs.getString("street_name"),
                    rs.getString("street_number"),
                    rs.getDate("dob").toLocalDate()
                );
                skiers.add(skier);
            }
        }

        return skiers;
    }

    public Skier getSkierById(int skierId) throws SQLException {
        String query = "SELECT * FROM Skiers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, skierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Skier(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("city"),
                        rs.getString("postal_code"),
                        rs.getString("street_name"),
                        rs.getString("street_number"),
                        rs.getDate("dob").toLocalDate()
                    );
                }
            }
        }

        return null; 
    }

    public int createSkier(Skier skier) throws SQLException {
        String query = "INSERT INTO Skiers (first_name, last_name, city, postal_code, street_name, street_number, dob) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, skier.getFirstName());
            pstmt.setString(2, skier.getLastName());
            pstmt.setString(3, skier.getCity());
            pstmt.setString(4, skier.getPostalCode());
            pstmt.setString(5, skier.getStreetName());
            pstmt.setString(6, skier.getStreetNumber());
            pstmt.setDate(7, Date.valueOf(skier.getDob()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating skiers failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retourne l'ID généré
                }
                else {
                    throw new SQLException("Creating skier failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateSkier(Skier skier) throws SQLException {
        String query = "UPDATE Skiers SET first_name = ?, last_name = ?, city = ?, postal_code = ?, street_name = ?, street_number = ?, dob = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, skier.getFirstName());
            pstmt.setString(2, skier.getLastName());
            pstmt.setString(3, skier.getCity());
            pstmt.setString(4, skier.getPostalCode());
            pstmt.setString(5, skier.getStreetName());
            pstmt.setString(6, skier.getStreetNumber());
            pstmt.setDate(7, Date.valueOf(skier.getDob()));
            pstmt.setInt(8, skier.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; 
        }
    }

    public boolean deleteSkier(int skierId) throws SQLException {
        String query = "DELETE FROM Skiers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, skierId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; 
        }
    }
}
