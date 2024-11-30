package be.alb.dao;

import be.alb.database.OracleDBConnection;
import be.alb.models.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonDAO {

    // Create a single lesson
    public static boolean createLesson(Lesson lesson) {
        String query = "INSERT INTO LESSONS (LESSONID, MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, STARTDATE, ENDDATE, ISPRIVATE) "
                     + "VALUES (Lessons_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set parameters for the prepared statement
            stmt.setInt(1, lesson.getMinBookings());
            stmt.setInt(2, lesson.getMaxBookings());
            stmt.setInt(3, lesson.getLessonType().getLessonTypeId());
            
            // Handle nullable instructor ID
            if (lesson.getInstructor() != null) {
                stmt.setInt(4, lesson.getInstructor().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setDate(5, lesson.getStartDate());
            stmt.setDate(6, lesson.getEndDate());
            stmt.setInt(7, lesson.isPrivate() ? 1 : 0);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a group of lessons
    public static boolean createGroupLessons(List<Lesson> lessons) {
        // Requête avec les nouvelles colonnes
        String query = "INSERT INTO LESSONS (LESSONID, MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, "
                     + "STARTDATE, ENDDATE, ISPRIVATE, LESSONGROUPID, ISFIRSTDAY, ISLASTDAY) "
                     + "VALUES (Lessons_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Variable pour générer le LESSONGROUPID
            int lessonGroupId = 0;

            for (int i = 0; i < lessons.size(); i++) {
                Lesson lesson = lessons.get(i);

                // Si c'est la première leçon du groupe, générer un ID de groupe
                if (i == 0) {
                    lessonGroupId = getNextGroupId(); // Méthode pour obtenir un nouvel ID de groupe
                }

                // Remplir les paramètres de la requête SQL
                stmt.setInt(1, lesson.getMinBookings()); // MINBOOKINGS
                stmt.setInt(2, lesson.getMaxBookings()); // MAXBOOKINGS
                stmt.setInt(3, lesson.getLessonType().getLessonTypeId()); // LESSONTYPEID

                // Gérer l'ID de l'instructeur qui peut être nul
                if (lesson.getInstructor() != null) {
                    stmt.setInt(4, lesson.getInstructor().getId()); // INSTRUCTORID
                } else {
                    stmt.setNull(4, Types.INTEGER); // Si instructeur nul
                }

                stmt.setDate(5, lesson.getStartDate()); // STARTDATE
                stmt.setDate(6, lesson.getEndDate()); // ENDDATE
                stmt.setInt(7, lesson.isPrivate() ? 1 : 0); // ISPRIVATE

                stmt.setInt(8, lessonGroupId); // LESSONGROUPID

                // Définir ISFIRSTDAY et ISLASTDAY pour la première et la dernière leçon
                stmt.setInt(9, (i == 0) ? 1 : 0); // ISFIRSTDAY : 1 pour la première leçon
                stmt.setInt(10, (i == lessons.size() - 1) ? 1 : 0); // ISLASTDAY : 1 pour la dernière leçon

                // Ajouter à la batch
                stmt.addBatch();
                stmt.clearParameters();  // Nettoyer les paramètres pour la prochaine leçon
            }

            // Exécuter la batch
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static int getNextGroupId() throws SQLException {
        Connection connection = OracleDBConnection.getInstance();
    	String query = "SELECT LESSONGROUPID_SEQ.NEXTVAL FROM DUAL";  // Supposons que vous avez une séquence LESSONGROUPID_SEQ
        try (PreparedStatement stmt = OracleDBConnection.getInstance().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Retourne le prochain ID de groupe
            } else {
                throw new SQLException("Failed to get next group ID");
            }
        }
    }

    
    public List<Lesson> getAllLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = OracleDBConnection.getInstance(); 

            // Étape 1 : Requête principale pour récupérer les leçons
            String query = """
                    SELECT l.LESSONID, l.STARTDATE, l.ENDDATE, l.ISPRIVATE, l.LESSONTYPEID, l.INSTRUCTORID,
                           lt.NAME AS LESSONTYPE_NAME, lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, lt.ACCREDITATIONID AS LESSONTYPE_ACC_ID,
                           i.LASTNAME, i.FIRSTNAME, i.CITY, i.POSTALCODE, i.STREETNAME, i.STREETNUMBER, i.DOB,
                           a.ACCREDITATIONID AS INST_ACC_ID, a.NAME AS ACCREDITATION_NAME
                    FROM LESSONS l
                    JOIN LESSONTYPE lt ON l.LESSONTYPEID = lt.LESSONTYPEID
                    LEFT JOIN INSTRUCTORS i ON l.INSTRUCTORID = i.INSTRUCTORID
                    LEFT JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID
                    LEFT JOIN ACCREDITATIONS a ON lt.ACCREDITATIONID = a.ACCREDITATIONID
                    """;
            
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            // Structures temporaires pour gérer les relations
            Map<Integer, Accreditation> accreditationMap = new HashMap<>();
            Map<Integer, LessonType> lessonTypeMap = new HashMap<>();
            Map<Integer, Instructor> instructorMap = new HashMap<>();
            Map<Integer, List<Accreditation>> instructorAccreditationsMap = new HashMap<>();

            while (rs.next()) {
                int lessonId = rs.getInt("LESSONID");

                Date startDate = rs.getDate("STARTDATE"); 
                Date endDate = rs.getDate("ENDDATE");

                boolean isPrivate = rs.getInt("ISPRIVATE") == 1;

                int lessonTypeId = rs.getInt("LESSONTYPEID");
                String lessonTypeName = rs.getString("LESSONTYPE_NAME");
                String ageGroup = rs.getString("AGEGROUP");
                String sportType = rs.getString("SPORTTYPE");
                double price = rs.getDouble("PRICE");
                int lessonTypeAccreditationId = rs.getInt("LESSONTYPE_ACC_ID");

                int instructorId = rs.getInt("INSTRUCTORID");
                String firstName = rs.getString("FIRSTNAME");
                String lastName = rs.getString("LASTNAME");
                String city = rs.getString("CITY");
                String postalCode = rs.getString("POSTALCODE");
                String streetName = rs.getString("STREETNAME");
                String streetNumber = rs.getString("STREETNUMBER");
                Date dob = rs.getDate("DOB");

                // Récupérer les informations des accréditations
                int accreditationId = rs.getInt("INST_ACC_ID");
                String accreditationName = rs.getString("ACCREDITATION_NAME");

                // Gestion des accréditations
                Accreditation accreditation = accreditationMap.computeIfAbsent(accreditationId, 
                    id -> new Accreditation(id, accreditationName));

                // Gestion des LessonTypes
                LessonType lessonType = lessonTypeMap.computeIfAbsent(lessonTypeId, 
                    id -> new LessonType(id, lessonTypeName, ageGroup, sportType, price, accreditation));

                // Gestion des Instructors
                Instructor instructor = instructorMap.computeIfAbsent(instructorId, id -> {
                    List<Accreditation> accreditations = new ArrayList<>();
                    accreditations.add(accreditation); 
                    LocalDate dobLocalDate = (dob != null ? dob.toLocalDate() : null);  // Correction ici
                    return new Instructor(id, firstName, lastName, city, postalCode, streetName, streetNumber, 
                                          dobLocalDate, accreditations);
                });

 
                // Création de l'objet Lesson
                Lesson lesson = new Lesson(lessonId, startDate, endDate, instructor, lessonType, isPrivate);
                lessons.add(lesson);
            }



        } finally {
//            if (rs != null) rs.close();
//            if (stmt != null) stmt.close();
            //if (connection != null) connection.close();
        }

        return lessons;
    }
    
    public List<Lesson> getAllPrivateLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        Connection connection = OracleDBConnection.getInstance();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Requête pour récupérer uniquement les leçons privées
            String query = """
                    SELECT l.LESSONID, l.STARTDATE, l.ENDDATE, l.ISPRIVATE, l.LESSONTYPEID, l.INSTRUCTORID,
                           lt.NAME AS LESSONTYPE_NAME, lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, lt.ACCREDITATIONID AS LESSONTYPE_ACC_ID,
                           i.LASTNAME, i.FIRSTNAME, i.CITY, i.POSTALCODE, i.STREETNAME, i.STREETNUMBER, i.DOB,
                           a.ACCREDITATIONID AS INST_ACC_ID, a.NAME AS ACCREDITATION_NAME
                    FROM LESSONS l
                    JOIN LESSONTYPE lt ON l.LESSONTYPEID = lt.LESSONTYPEID
                    LEFT JOIN INSTRUCTORS i ON l.INSTRUCTORID = i.INSTRUCTORID
                    LEFT JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID
                    LEFT JOIN ACCREDITATIONS a ON lt.ACCREDITATIONID = a.ACCREDITATIONID
                    WHERE l.ISPRIVATE = 1
                    """;

            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            // Structures temporaires pour gérer les relations
            Map<Integer, Accreditation> accreditationMap = new HashMap<>();
            Map<Integer, LessonType> lessonTypeMap = new HashMap<>();
            Map<Integer, Instructor> instructorMap = new HashMap<>();
            Map<Integer, List<Accreditation>> instructorAccreditationsMap = new HashMap<>();

            while (rs.next()) {
                int lessonId = rs.getInt("LESSONID");
                Date startDate = rs.getDate("STARTDATE"); 
                Date endDate = rs.getDate("ENDDATE");

                boolean isPrivate = rs.getInt("ISPRIVATE") == 1;

                int lessonTypeId = rs.getInt("LESSONTYPEID");
                String lessonTypeName = rs.getString("LESSONTYPE_NAME");
                String ageGroup = rs.getString("AGEGROUP");
                String sportType = rs.getString("SPORTTYPE");
                double price = rs.getDouble("PRICE");
                int lessonTypeAccreditationId = rs.getInt("LESSONTYPE_ACC_ID");

                int instructorId = rs.getInt("INSTRUCTORID");
                String firstName = rs.getString("FIRSTNAME");
                String lastName = rs.getString("LASTNAME");
                String city = rs.getString("CITY");
                String postalCode = rs.getString("POSTALCODE");
                String streetName = rs.getString("STREETNAME");
                String streetNumber = rs.getString("STREETNUMBER");
                Date dob = rs.getDate("DOB");

                // Récupérer les informations des accréditations
                int accreditationId = rs.getInt("INST_ACC_ID");
                String accreditationName = rs.getString("ACCREDITATION_NAME");

                // Gestion des accréditations
                Accreditation accreditation = accreditationMap.computeIfAbsent(accreditationId, 
                    id -> new Accreditation(id, accreditationName));

                // Gestion des LessonTypes
                LessonType lessonType = lessonTypeMap.computeIfAbsent(lessonTypeId, 
                    id -> new LessonType(id, lessonTypeName, ageGroup, sportType, price, accreditation));

                // Gestion des Instructors
                Instructor instructor = instructorMap.computeIfAbsent(instructorId, id -> {
                    List<Accreditation> accreditations = new ArrayList<>();
                    accreditations.add(accreditation); 
                    LocalDate dobLocalDate = (dob != null ? dob.toLocalDate() : null);
                    return new Instructor(id, firstName, lastName, city, postalCode, streetName, streetNumber, 
                                          dobLocalDate, accreditations);
                });

                // Création de l'objet Lesson
                Lesson lesson = new Lesson(lessonId, startDate, endDate, instructor, lessonType, isPrivate);
                lessons.add(lesson);
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            //if (connection != null) connection.close();
        }

        return lessons;
    }
    
    public List<Lesson> getAllPublicLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        Connection conn = OracleDBConnection.getInstance(); 
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Requête pour récupérer uniquement les leçons publiques, incluant le groupId et les flags isFirstDay et isLastDay
            String query = """
                    SELECT l.LESSONID, l.STARTDATE, l.ENDDATE, l.ISPRIVATE, l.LESSONTYPEID, l.INSTRUCTORID,
                           lt.NAME AS LESSONTYPE_NAME, lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, lt.ACCREDITATIONID AS LESSONTYPE_ACC_ID,
                           i.LASTNAME, i.FIRSTNAME, i.CITY, i.POSTALCODE, i.STREETNAME, i.STREETNUMBER, i.DOB,
                           a.ACCREDITATIONID AS INST_ACC_ID, a.NAME AS ACCREDITATION_NAME,
                           l.LESSONGROUPID, l.ISFIRSTDAY, l.ISLASTDAY
                    FROM LESSONS l
                    JOIN LESSONTYPE lt ON l.LESSONTYPEID = lt.LESSONTYPEID
                    LEFT JOIN INSTRUCTORS i ON l.INSTRUCTORID = i.INSTRUCTORID
                    LEFT JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID
                    LEFT JOIN ACCREDITATIONS a ON lt.ACCREDITATIONID = a.ACCREDITATIONID
                    WHERE l.ISPRIVATE = 0
                    """;

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            // Structures temporaires pour gérer les relations
            Map<Integer, Accreditation> accreditationMap = new HashMap<>();
            Map<Integer, LessonType> lessonTypeMap = new HashMap<>();
            Map<Integer, Instructor> instructorMap = new HashMap<>();

            while (rs.next()) {
                int lessonId = rs.getInt("LESSONID");
                Date startDate = rs.getDate("STARTDATE");
                Date endDate = rs.getDate("ENDDATE");

                boolean isPrivate = rs.getInt("ISPRIVATE") == 1;

                int lessonTypeId = rs.getInt("LESSONTYPEID");
                String lessonTypeName = rs.getString("LESSONTYPE_NAME");
                String ageGroup = rs.getString("AGEGROUP");
                String sportType = rs.getString("SPORTTYPE");
                double price = rs.getDouble("PRICE");
                int lessonTypeAccreditationId = rs.getInt("LESSONTYPE_ACC_ID");

                int instructorId = rs.getInt("INSTRUCTORID");
                String firstName = rs.getString("FIRSTNAME");
                String lastName = rs.getString("LASTNAME");
                String city = rs.getString("CITY");
                String postalCode = rs.getString("POSTALCODE");
                String streetName = rs.getString("STREETNAME");
                String streetNumber = rs.getString("STREETNUMBER");
                Date dob = rs.getDate("DOB");

                // Récupérer les informations des accréditations
                int accreditationId = rs.getInt("INST_ACC_ID");
                String accreditationName = rs.getString("ACCREDITATION_NAME");

                // Gestion des accréditations
                Accreditation accreditation = accreditationMap.computeIfAbsent(accreditationId, 
                    id -> new Accreditation(id, accreditationName));

                // Gestion des LessonTypes
                LessonType lessonType = lessonTypeMap.computeIfAbsent(lessonTypeId, 
                    id -> new LessonType(id, lessonTypeName, ageGroup, sportType, price, accreditation));

                // Gestion des Instructors
                Instructor instructor = instructorMap.computeIfAbsent(instructorId, id -> {
                    List<Accreditation> accreditations = new ArrayList<>();
                    accreditations.add(accreditation);
                    LocalDate dobLocalDate = (dob != null ? dob.toLocalDate() : null);
                    return new Instructor(id, firstName, lastName, city, postalCode, streetName, streetNumber, 
                                          dobLocalDate, accreditations);
                });

                // Récupérer les nouvelles informations
                int lessonGroupId = rs.getInt("LESSONGROUPID");
                boolean isFirstDay = rs.getInt("ISFIRSTDAY") == 1;
                boolean isLastDay = rs.getInt("ISLASTDAY") == 1;

                // Création de l'objet Lesson avec les nouveaux attributs
                Lesson lesson = new Lesson(lessonId, startDate, endDate, instructor, lessonType, 
                                           isPrivate, isFirstDay, isLastDay, lessonGroupId);
                lessons.add(lesson);
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            // Connexion déjà gérée par OracleDBConnection.getInstance() 
        }

        return lessons;
    }

    
    public boolean isLessonFull(Lesson lesson) throws SQLException {
        String query = "SELECT COUNT(*) AS bookingCount " +
                       "FROM Bookings b " +
                       "WHERE b.lessonId = ?"; 

        try (PreparedStatement stmt = OracleDBConnection.getInstance().prepareStatement(query)) {
            stmt.setInt(1, lesson.getLessonId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int bookingCount = rs.getInt("bookingCount");

                    return bookingCount >= lesson.getMaxBookings();
                }
            }
        }
        return false; 
    }
    
    public boolean deleteLesson(Lesson lesson) {
        String sql = "DELETE FROM LESSONS WHERE LESSONID = ?";
        try (Connection conn = OracleDBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lesson.getLessonId());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





}
