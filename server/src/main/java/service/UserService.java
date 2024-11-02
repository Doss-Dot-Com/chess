package service;

import dataaccess.*;
import model.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // This method extracts the username based on the provided auth token
    public String getUsernameFromToken(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new IllegalArgumentException("Invalid auth token");
        }
        return authData.getUsername();  // Assuming AuthData has a method getUsername()
    }

    public boolean isValidToken(String authToken) {
        try {
            return dataAccess.getAuth(authToken) != null;
        } catch (DataAccessException e) {
            return false;
        }
    }


    public AuthData register(UserData user) throws DataAccessException, UserAlreadyExistsException {
        // Check if any required field is missing
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new IllegalArgumentException("Username, password, and email are required");
        }

        // Check if the user already exists
        if (dataAccess.getUser(user.username()) != null) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        // Proceed with registration
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        return auth;
    }


    public AuthData login(UserData user) throws DataAccessException {
        UserData storedUser = dataAccess.getUser(user.username());

        if (storedUser == null || !BCrypt.checkpw(user.password(), storedUser.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        // Generate and store the token if login is successful
        AuthData authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        System.out.println("Token created: " + authData.getAuthToken() + " for user: " + user.username());  // Log token creation
        return authData;
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            System.out.println("Logout failed: Invalid token " + authToken);  // Log failed validation
            throw new DataAccessException("Invalid token");
        }

        // If valid, delete the token
        dataAccess.deleteAuth(authToken);
        System.out.println("Token deleted successfully: " + authToken);  // Log successful deletion
    }
}


