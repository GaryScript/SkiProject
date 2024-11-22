package be.alb.dao;

import be.alb.database.OracleDBConnection;
import be.alb.models.Skier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkierDAO implements SkierDAOInterface {

    private Connection connection;

    public SkierDAO() {
        this.connection = OracleDBConnection.getInstance();
    }

    public List<Skier> getAllSkiers() throws SQLException {
        List<Skier> skiers = new ArrayList<>();
        String query = "SELECT * FROM skiers";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Skier skier = new Skier(
                    rs.getInt("skierid"),
                    rs.getString("lastname"),
                    rs.getString("firstname"),
                    rs.getString("city"),
                    rs.getString("postalcode"),
                    rs.getString("streetname"),
                    rs.getString("streetnumber"),
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
        String insertQuery = "INSERT INTO Skiers (skierid, firstname, lastname, city, postalcode, streetname, streetnumber, dob) " +
                             "VALUES (skier_id_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT skier_id_seq.CURRVAL FROM DUAL";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, skier.getFirstName());
            pstmt.setString(2, skier.getLastName());
            pstmt.setString(3, skier.getCity());
            pstmt.setString(4, skier.getPostalCode());
            pstmt.setString(5, skier.getStreetName());
            pstmt.setString(6, skier.getStreetNumber());
            pstmt.setDate(7, Date.valueOf(skier.getDob()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating skier failed, no rows affected.");
            }
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve skier ID after insertion.");
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
