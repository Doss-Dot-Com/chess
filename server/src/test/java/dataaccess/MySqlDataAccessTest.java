package dataaccess;

import dataaccess.MySqlDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTest {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();  // Ensure a clean slate before each test
    }

    // 1. Tests for User operations
    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("user1", "password123", "user1@example.com");
        dataAccess.createUser(user);
        UserData retrievedUser = dataAccess.getUser("user1");
        assertNotNull(retrievedUser);
        assertEquals("user1@example.com", retrievedUser.email());
    }

    // 2. Adjusting Game Creation Tests
    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        // First, create the necessary user to satisfy foreign key constraint
        UserData user = new UserData("whitePlayer", "password123", "white@example.com");
        dataAccess.createUser(user);

        GameData game = new GameData(1, "Test Game");
        game.setWhiteUsername("whitePlayer"); // Associate white player
        dataAccess.createGame(game);

        GameData retrievedGame = dataAccess.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("Test Game", retrievedGame.getGameName());
    }

    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        // Ensure users are present for foreign key constraints
        UserData whiteUser = new UserData("whitePlayer", "password123", "white@example.com");
        UserData blackUser = new UserData("blackPlayer", "password123", "black@example.com");
        dataAccess.createUser(whiteUser);
        dataAccess.createUser(blackUser);

        GameData game = new GameData(2, "Game Update Test");
        game.setWhiteUsername("whitePlayer");
        dataAccess.createGame(game);

        // Now update the game to include a black player
        game.setBlackUsername("blackPlayer");
        dataAccess.updateGame(game);

        GameData updatedGame = dataAccess.getGame(2);
        assertNotNull(updatedGame);
        assertEquals("blackPlayer", updatedGame.getBlackUsername());
    }

    // 3. Adjusting Auth Creation Tests
    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        // Create user to satisfy foreign key constraint in auth table
        UserData user = new UserData("user1", "password123", "user1@example.com");
        dataAccess.createUser(user);

        AuthData auth = new AuthData("authToken123", "user1");
        dataAccess.createAuth(auth);

        AuthData retrievedAuth = dataAccess.getAuth("authToken123");
        assertNotNull(retrievedAuth);
        assertEquals("user1", retrievedAuth.getUsername());
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        // Create user to satisfy foreign key constraint in auth table
        UserData user = new UserData("user2", "password123", "user2@example.com");
        dataAccess.createUser(user);

        AuthData auth = new AuthData("authToken456", "user2");
        dataAccess.createAuth(auth);

        dataAccess.deleteAuth("authToken456");
        assertNull(dataAccess.getAuth("authToken456"));  // Auth should be deleted
    }
}


