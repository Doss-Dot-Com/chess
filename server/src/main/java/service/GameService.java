package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameRequest;
import model.JoinGameRequest;

import java.util.List;
import java.util.UUID;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Method to create a new game
    public int createGame(GameRequest gameRequest) throws DataAccessException {
        int gameID = Math.abs(UUID.randomUUID().hashCode());  // Generate a positive game ID
        GameData newGame = new GameData(gameID, gameRequest.getGameName());
        dataAccess.createGame(newGame);
        return gameID;
    }

    // Method to list all games
    public List<GameData> listGames() throws DataAccessException {
        return dataAccess.getAllGames();
    }

    public void joinGame(JoinGameRequest joinRequest) throws DataAccessException {
        // Get the game from the data access layer
        GameData game = dataAccess.getGame(joinRequest.getGameID());

        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        // Check if playerColor is provided, if it's null, we need to throw an error
        String playerColor = joinRequest.getPlayerColor();
        if (playerColor == null) {
            throw new DataAccessException("Player color is missing");
        }

        String username = joinRequest.getUsername();
        if (username == null) {
            throw new DataAccessException("Username is missing");
        }

        // Check which color the player is joining as
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("White player slot already taken");
            }
            game.setWhiteUsername(username);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Black player slot already taken");
            }
            game.setBlackUsername(username);
        } else {
            throw new DataAccessException("Invalid player color");
        }

        // Update the game in memory
        dataAccess.updateGame(game);
    }
}







