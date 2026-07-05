package contacthub;

import java.sql.*;

public class DatabaseConfig 
{
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "contact_manager_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "suruchi0612"; // Change to your MySQL password

    private static final String JDBC_URL =
    "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
    "?useSSL=false&allowPublicKeyRetrieval=true&connectionTimeZone=LOCAL";
    
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) 
        {
            try 
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            } 
            catch (ClassNotFoundException e) 
            {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java.jar to classpath.", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        // First connect without DB to create it if needed
        String baseUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) 
        {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        try (Connection tempConn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
             Statement stmt = tempConn.createStatement()) 
        {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) 
        {
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS contacts (
                        id          INT AUTO_INCREMENT PRIMARY KEY,
                        name        VARCHAR(120) NOT NULL,
                        phone       VARCHAR(20)  NOT NULL,
                        email       VARCHAR(120),
                        category    VARCHAR(50)  DEFAULT 'General',
                        notes       TEXT,
                        created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
                        updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        UNIQUE KEY uq_phone (phone)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);
        }
    }

    public static void closeConnection() 
    {
        if (connection != null) 
        {
            try 
            {
                connection.close();
            } 
            catch (SQLException ignored) {}
        }
    }
}
