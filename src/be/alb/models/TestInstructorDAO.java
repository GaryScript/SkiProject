package be.alb.models;

import java.sql.*;
import java.util.*;

import be.alb.dao.InstructorDAO;
import be.alb.database.OracleDBConnection;

public class TestInstructorDAO {
    public static void main(String[] args) {
        // Utilisation de la connexion gérée par OracleDBConnection
        try (Connection conn = OracleDBConnection.getInstance()) {
            InstructorDAO instructorDAO = new InstructorDAO();

            // Paramètres de test : plage de dates et type de leçon
            java.sql.Date startDate = java.sql.Date.valueOf("2024-02-01");
            java.sql.Date endDate = java.sql.Date.valueOf("2024-02-01");
            int lessonTypeId = 1;  // Utilise un ID existant dans ta table LESSONTYPE

            // Appeler la méthode pour récupérer les instructeurs disponibles
            List<Instructor> instructors = instructorDAO.getAvailableInstructors(startDate, endDate, lessonTypeId);

            // Afficher les instructeurs récupérés
            for (Instructor instructor : instructors) {
                System.out.println("Instructor ID: " + instructor.getId());
                System.out.println("Name: " + instructor.getFirstName() + " " + instructor.getLastName());
                System.out.println("Accreditations: " + instructor.getAccreditations());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
