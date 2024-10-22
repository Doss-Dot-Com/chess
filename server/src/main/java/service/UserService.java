package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        dataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
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
        dataAccess.deleteAuth(authToken);
    }
}

