package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new InMemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    public void testRegisterSuccess() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("newUser", "password", "email@test.com");
        AuthData authData = userService.register(user);
        assertNotNull(authData);
        assertEquals("newUser", authData.getUsername());
    }

    @Test
    public void testRegisterFailureUserAlreadyExists() throws DataAccessException, UserAlreadyExistsException {
        UserData user = new UserData("existingUser", "password", "email@test.com");
        dataAccess.createUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(user));
    }

    @Test
    public void testRegisterFailureMissingFields() {
        UserData user = new UserData(null, "password", null);
        assertThrows(IllegalArgumentException.class, () -> userService.register(user));
    }

    @Test
    public void testLoginSuccess() throws DataAccessException {
        // Create user with hashed password
        UserData user = new UserData("existingUser", BCrypt.hashpw("password", BCrypt.gensalt()), "email@test.com");
        dataAccess.createUser(user);

        // Attempt login with correct password
        AuthData authData = userService.login(new UserData("existingUser", "password", "email@test.com"));
        assertNotNull(authData);  // Ensure login succeeds and returns auth data
    }

    @Test
    public void testLoginFailureWrongPassword() throws DataAccessException {
        // Create user with correct hashed password
        UserData user = new UserData("existingUser", BCrypt.hashpw("password", BCrypt.gensalt()), "email@test.com");
        dataAccess.createUser(user);

        // Attempt login with wrong password
        assertThrows(DataAccessException.class, () -> userService.login(new UserData("existingUser", "wrongPassword", "email@test.com")));
    }

    @Test
    public void testIsValidTokenSuccess() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "existingUser");
        dataAccess.createAuth(authData);
        assertTrue(userService.isValidToken("authToken"));
    }

    @Test
    public void testIsValidTokenFailureInvalidToken() {
        assertFalse(userService.isValidToken("invalidToken"));
    }

    @Test
    public void testGetUsernameFromTokenSuccess() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "existingUser");
        dataAccess.createAuth(authData);
        assertEquals("existingUser", userService.getUsernameFromToken("authToken"));
    }

    @Test
    public void testGetUsernameFromTokenFailureInvalidToken() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUsernameFromToken("invalidToken"));
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "existingUser");
        dataAccess.createAuth(authData);
        userService.logout("authToken");
        assertFalse(userService.isValidToken("authToken"));
    }

    @Test
    public void testLogoutFailureInvalidToken() {
        assertThrows(DataAccessException.class, () -> userService.logout("invalidToken"));
    }
}



