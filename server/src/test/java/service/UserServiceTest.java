package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.InMemoryDataAccess;
import service.UserAlreadyExistsException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        // Initialize a simple in-memory data access implementation
        dataAccess = new InMemoryDataAccess();  // Replace this with your implementation
        userService = new UserService(dataAccess);
    }

    @Test
    public void testRegister_Success() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("testUser", "password123", "testUser@example.com");
        AuthData auth = userService.register(user);

        assertNotNull(auth, "AuthData should not be null after successful registration");
        assertEquals("testUser", auth.getUsername(), "Username should match the registered username");
        assertNotNull(auth.getAuthToken(), "Auth token should not be null after successful registration");
    }

    @Test
    public void testRegister_Failure_UserAlreadyExists() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("existingUser", "password123", "existingUser@example.com");
        userService.register(user);  // Register the user first

        // Try to register the same user again, should throw UserAlreadyExistsException
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(user);
        }, "Should throw UserAlreadyExistsException when registering an existing user");
    }

    @Test
    public void testRegister_Failure_MissingFields() {
        // Try registering with missing fields
        UserData user = new UserData(null, "password123", "testUser@example.com");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        }, "Should throw IllegalArgumentException for missing username");
    }

    @Test
    public void testLogin_Success() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("loginUser", "password123", "loginUser@example.com");
        userService.register(user);  // Register the user first

        AuthData auth = userService.login(user);
        assertNotNull(auth, "AuthData should not be null after successful login");
        assertEquals("loginUser", auth.getUsername(), "Username should match the logged-in username");
        assertNotNull(auth.getAuthToken(), "Auth token should not be null after successful login");
    }

    @Test
    public void testLogin_Failure_WrongPassword() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("wrongPasswordUser", "password123", "wrongPasswordUser@example.com");
        userService.register(user);  // Register the user first

        // Try to log in with the wrong password
        UserData invalidUser = new UserData("wrongPasswordUser", "wrongPassword", "wrongPasswordUser@example.com");
        assertThrows(DataAccessException.class, () -> {
            userService.login(invalidUser);
        }, "Should throw DataAccessException for invalid password");
    }

    @Test
    public void testIsValidToken_Success() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("validTokenUser", "password123", "validTokenUser@example.com");
        AuthData auth = userService.register(user);

        assertTrue(userService.isValidToken(auth.getAuthToken()), "Token should be valid for an existing user");
    }

    @Test
    public void testIsValidToken_Failure_InvalidToken() {
        assertFalse(userService.isValidToken("invalidToken"), "Invalid token should return false");
    }

    @Test
    public void testGetUsernameFromToken_Success() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("tokenUser", "password123", "tokenUser@example.com");
        AuthData auth = userService.register(user);

        String username = userService.getUsernameFromToken(auth.getAuthToken());
        assertEquals("tokenUser", username, "Username should match the one associated with the token");
    }

    @Test
    public void testGetUsernameFromToken_Failure_InvalidToken() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsernameFromToken("invalidToken");
        }, "Should throw IllegalArgumentException for an invalid token");
    }

    @Test
    public void testLogout_Success() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("logoutUser", "password123", "logoutUser@example.com");
        AuthData auth = userService.register(user);

        // Logout the user
        userService.logout(auth.getAuthToken());

        // Check if the token is now invalid
        assertFalse(userService.isValidToken(auth.getAuthToken()), "Token should be invalid after logout");
    }

    @Test
    public void testLogout_Failure_InvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("invalidToken");
        }, "Should throw DataAccessException for an invalid token");
    }
}


