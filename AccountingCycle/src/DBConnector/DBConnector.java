package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {
    // Update these details for your own MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/accounting_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "anitsaj@2006"; // replace with your actual MySQL password

    public static Connection getConnection() {
        Connection connect = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Attempt connection
            connect = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL successfully!");

            // 🔍 Check which database is connected
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATABASE();");
            if (rs.next()) {
                System.out.println("📂 Connected Database: " + rs.getString(1));
            }

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }

        return connect;
    }

    // A simple test main method
    public static void main(String[] args) {
        Connection conn = getConnection();

        if (conn != null) {
            System.out.println("✅ Database connection test successful!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

   
}
