package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String CONNECTION_URL;
    private static final String USER;
    private static final String PASSWORD;

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

            CONNECTION_URL = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", host, port, dbName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    public static void createDatabase() {
        String baseUrl = CONNECTION_URL.substring(0, CONNECTION_URL.lastIndexOf("/") + 1) + "?serverTimezone=UTC";
        try (var conn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + getDatabaseName());
            System.out.println("Database created or confirmed to exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getDatabaseName() {
        return CONNECTION_URL.substring(CONNECTION_URL.lastIndexOf('/') + 1, CONNECTION_URL.indexOf('?'));
    }
}




