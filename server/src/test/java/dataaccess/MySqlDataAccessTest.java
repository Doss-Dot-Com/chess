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
        dataAccess.clear();  // Clear tables before each test
    }

    // 1. Tests for createUser
    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("user1", "password123", "user1@example.com");
        dataAccess.createUser(user);
        UserData retrievedUser = dataAccess.getUser("user1");
        assertNotNull(retrievedUser);
        assertEquals("user1@example.com", retrievedUser.email());
    }

    @Test
    public void testCreateUserDuplicateUsername() {
        assertThrows(DataAccessException.class, () -> {
            UserData user1 = new UserData("duplicateUser", "password1", "user1@example.com");
            UserData user2 = new UserData("duplicateUser", "password2", "user2@example.com");
            dataAccess.createUser(user1);
            dataAccess.createUser(user2);  // Should throw an exception
        });
    }

    // 2. Tests for getUser
    @Test
    public void testGetUserSuccess() throws DataAccessException {
        UserData user = new UserData("existingUser", "password123", "existingUser@example.com");
        dataAccess.createUser(user);
        UserData retrievedUser = dataAccess.getUser("existingUser");
        assertNotNull(retrievedUser);
        assertEquals("existingUser", retrievedUser.username());
    }

    @Test
    public void testGetUserNotFound() throws DataAccessException {
        assertNull(dataAccess.getUser("nonExistentUser"));
    }

    // 3. Tests for createGame
    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        UserData user = new UserData("whitePlayer", "password123", "white@example.com");
        dataAccess.createUser(user);

        GameData game = new GameData(1, "Chess Game");
        game.setWhiteUsername("whitePlayer");
        dataAccess.createGame(game);

        GameData retrievedGame = dataAccess.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("Chess Game", retrievedGame.getGameName());
    }

    @Test
    public void testCreateGameNonExistentUser() {
        assertThrows(DataAccessException.class, () -> {
            GameData game = new GameData(2, "Invalid Game");
            game.setWhiteUsername("nonExistentUser");
            dataAccess.createGame(game);  // Should fail due to missing user
        });
    }

    // 4. Tests for getGame
    @Test
    public void testGetGameSuccess() throws DataAccessException {
        UserData user = new UserData("player", "password123", "player@example.com");
        dataAccess.createUser(user);

        GameData game = new GameData(3, "Retrieve Game");
        game.setWhiteUsername("player");
        dataAccess.createGame(game);

        GameData retrievedGame = dataAccess.getGame(3);
        assertNotNull(retrievedGame);
        assertEquals("Retrieve Game", retrievedGame.getGameName());
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException {
        assertNull(dataAccess.getGame(999));  // Non-existent game ID
    }

    // 5. Tests for updateGame
    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        UserData whiteUser = new UserData("whitePlayer", "password123", "white@example.com");
        UserData blackUser = new UserData("blackPlayer", "password123", "black@example.com");
        dataAccess.createUser(whiteUser);
        dataAccess.createUser(blackUser);

        GameData game = new GameData(4, "Original Game");
        game.setWhiteUsername("whitePlayer");
        dataAccess.createGame(game);

        game.setBlackUsername("blackPlayer");
        dataAccess.updateGame(game);

        GameData updatedGame = dataAccess.getGame(4);
        assertEquals("blackPlayer", updatedGame.getBlackUsername());
    }

    @Test
    public void testUpdateGameNotFound() {
        GameData nonExistentGame = new GameData(999, "Nonexistent Game");
        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(nonExistentGame));
    }

    // 6. Tests for createAuth
    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        UserData user = new UserData("authUser", "password123", "authUser@example.com");
        dataAccess.createUser(user);

        AuthData auth = new AuthData("authToken123", "authUser");
        dataAccess.createAuth(auth);

        AuthData retrievedAuth = dataAccess.getAuth("authToken123");
        assertNotNull(retrievedAuth);
        assertEquals("authUser", retrievedAuth.getUsername());
    }

    @Test
    public void testCreateAuthNonExistentUser() {
        assertThrows(DataAccessException.class, () -> {
            AuthData auth = new AuthData("invalidAuthToken", "nonExistentUser");
            dataAccess.createAuth(auth);  // Should fail due to missing user
        });
    }

    // 7. Tests for getAuth
    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        UserData user = new UserData("authTestUser", "password123", "authTestUser@example.com");
        dataAccess.createUser(user);

        AuthData auth = new AuthData("authToken456", "authTestUser");
        dataAccess.createAuth(auth);

        AuthData retrievedAuth = dataAccess.getAuth("authToken456");
        assertNotNull(retrievedAuth);
        assertEquals("authTestUser", retrievedAuth.getUsername());
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException {
        assertNull(dataAccess.getAuth("nonExistentToken"));
    }

    // 8. Tests for deleteAuth
    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        UserData user = new UserData("deleteAuthUser", "password123", "deleteAuthUser@example.com");
        dataAccess.createUser(user);

        AuthData auth = new AuthData("authTokenToDelete", "deleteAuthUser");
        dataAccess.createAuth(auth);

        dataAccess.deleteAuth("authTokenToDelete");
        assertNull(dataAccess.getAuth("authTokenToDelete"));
    }

    @Test
    public void testDeleteAuthNotFound() throws DataAccessException {
        dataAccess.deleteAuth("nonExistentAuthToken");  // Ensure no exception thrown
        assertNull(dataAccess.getAuth("nonExistentAuthToken"));
    }
}



