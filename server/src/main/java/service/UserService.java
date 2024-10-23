package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public boolean isValidToken(String authToken) {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            return authData != null; // Token is valid if authData is found
        } catch (DataAccessException e) {
            return false; // Invalid token if there's an exception
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
        UserData existingUser = dataAccess.getUser(user.username());
        if (existingUser != null && existingUser.password().equals(user.password())) {
            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(authToken, user.username());
            dataAccess.createAuth(auth);
            return auth;
        } else {
            throw new DataAccessException("Unauthorized");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Invalid auth token");
        }

        dataAccess.deleteAuth(authToken);
    }
}


