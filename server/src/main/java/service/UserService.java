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

        // Check if user exists and validate the hashed password
        if (storedUser == null || !BCrypt.checkpw(user.password(), storedUser.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        // Generate an auth token if login is successful
        AuthData authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken); // Check if the token exists in the database
        if (auth == null) {
            throw new DataAccessException("Invalid token"); // Unauthorized if token is not found
        }
        dataAccess.deleteAuth(authToken);  // Remove the token from the database if valid
        System.out.println("Token deleted successfully for token: " + authToken); // Log successful deletion
    }
}


