package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();     // Clear all users
        games.clear();     // Clear all games
        authTokens.clear(); // Clear all authentication tokens
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.getGameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);

        return game;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.getGameID(), game);

    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.getAuthToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }
}




