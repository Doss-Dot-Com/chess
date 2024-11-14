package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                gameName VARCHAR(100),
                whiteUsername VARCHAR(50),
                blackUsername VARCHAR(50),
                FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE SET NULL,
                FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE SET NULL
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(100) PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            );
            """
        };

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Database tables created or confirmed to exist.");
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String[] clearStatements = {
                "DELETE FROM auth",   // Clear auth table
                "DELETE FROM games",  // Clear games table
                "DELETE FROM users"   // Clear users table
        };

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : clearStatements) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
            System.out.println("Tables auth, games, and users cleared.");  // Log successful clearance
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing tables: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt()); // Hash the password
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);  // Store hashed password
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        // Implement the method with no return as required by the DataAccess interface
        createGameWithID(game);
    }

    // New MySQL-specific method to create a game and return its generated ID
    public int createGameWithID(GameData game) throws DataAccessException {
        String statement = "INSERT INTO games (gameID, gameName, whiteUsername, blackUsername) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, game.getGameID()); // Use the specified gameID
            ps.setString(2, game.getGameName());
            ps.setString(3, game.getWhiteUsername());
            ps.setString(4, game.getBlackUsername());
            ps.executeUpdate();
            System.out.println("Game created with specified ID: " + game.getGameID());
            return game.getGameID();  // Return the specified gameID
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game with specified ID: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GameData game = new GameData(rs.getInt("gameID"), rs.getString("gameName"));
                    game.setWhiteUsername(rs.getString("whiteUsername"));
                    game.setBlackUsername(rs.getString("blackUsername"));
                    return game;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String checkStatement = "SELECT COUNT(*) FROM games WHERE gameID = ?";
        String updateStatement = "UPDATE games SET gameName = ?, whiteUsername = ?, blackUsername = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if the game exists
            try (PreparedStatement checkPs = conn.prepareStatement(checkStatement)) {
                checkPs.setInt(1, game.getGameID());
                try (ResultSet rs = checkPs.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        throw new DataAccessException("Game with ID " + game.getGameID() + " does not exist.");
                    }
                }
            }

            // If game exists, proceed with update
            try (PreparedStatement ps = conn.prepareStatement(updateStatement)) {
                ps.setString(1, game.getGameName());
                ps.setString(2, game.getWhiteUsername());
                ps.setString(3, game.getBlackUsername());
                ps.setInt(4, game.getGameID());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }


    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GameData game = new GameData(rs.getInt("gameID"), rs.getString("gameName"));
                game.setWhiteUsername(rs.getString("whiteUsername"));
                game.setBlackUsername(rs.getString("blackUsername"));
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, auth.getAuthToken());
            ps.setString(2, auth.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT * FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }
}




