package be.alb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class OracleDBConnection {

    private static Connection connection = null;

    private OracleDBConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            String url = "jdbc:oracle:thin:@//193.190.64.10:1522/xepdb1"; 
            String user = "STUDENT03_22";  
            String password = "changeme";    
            System.setProperty("oracle.jdbc.Trace", "true"); 
            
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connetion went successfully !");
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connetion error to the database:" + e.getMessage());
        }
    }

  
    public static Connection getInstance() {
        if (connection == null) {
            new OracleDBConnection();
        }
        return connection;
    }

   
    public static Object[][] getData(String query) {
        Connection conn = getInstance();
        Object[][] data = null;
        
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
            
            int columnCount = rs.getMetaData().getColumnCount();
            
            java.util.List<Object[]> tempData = new java.util.ArrayList<>();
            
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int col = 0; col < columnCount; col++) {
                    rowData[col] = rs.getObject(col + 1);
                }
                tempData.add(rowData);
            }
            
            data = tempData.toArray(new Object[tempData.size()][]);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération des données : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return data;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;  
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
