package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String CONNECTION_URL;
    private static final String USER;
    private static final String PASSWORD;

    // Static block to load properties from db.properties
    static {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new RuntimeException("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);

            var host = props.getProperty("db.host");
            var port = props.getProperty("db.port");
            var dbName = props.getProperty("db.name");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            // Build the connection URL
            CONNECTION_URL = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", host, port, dbName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    // Method to create tables if they don't exist
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(100) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS games (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "state JSON NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS moves (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "game_id INT, " +
                    "move VARCHAR(10) NOT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE)");

            System.out.println("Database and tables created or confirmed to exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


