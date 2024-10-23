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
        GameData game = dataAccess.getGame(joinRequest.getGameID());

        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        String playerColor = joinRequest.getPlayerColor();
        if (playerColor == null) {
            throw new DataAccessException("Player color not specified");
        }

        // Ensure the user is assigned to the correct color, and the game is updated
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new IllegalArgumentException("White player slot already taken");
            }
            game.setWhiteUsername(joinRequest.getUsername());
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.getBlackUsername() != null) {
                throw new IllegalArgumentException("Black player slot already taken");
            }
            game.setBlackUsername(joinRequest.getUsername());
        } else {
            throw new DataAccessException("Invalid player color");
        }

        // Persist the updated game with the new player assigned
        dataAccess.updateGame(game);
    }
}








