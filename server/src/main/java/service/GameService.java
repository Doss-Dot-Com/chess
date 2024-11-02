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

    public void clearData() throws DataAccessException {
        dataAccess.clear();  // Clear the data in the database or in-memory storage
    }

    // Method to create a new game
    public int createGame(GameRequest gameRequest) throws DataAccessException {
        if (gameRequest.getGameName() == null || gameRequest.getGameName().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty");
        }
        int gameID = Math.abs(UUID.randomUUID().hashCode());
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

        // Handle the case where the game does not exist
        if (game == null) {
            throw new IllegalArgumentException("Game not found with ID: " + joinRequest.getGameID());
        }

        // Set the player color and validate input
        if ("WHITE".equalsIgnoreCase(joinRequest.getPlayerColor())) {
            game.setWhiteUsername(joinRequest.getUsername());
        } else if ("BLACK".equalsIgnoreCase(joinRequest.getPlayerColor())) {
            game.setBlackUsername(joinRequest.getUsername());
        } else {
            throw new IllegalArgumentException("Invalid player color: " + joinRequest.getPlayerColor());
        }

        // Update the game in the database
        dataAccess.updateGame(game);


        // After game updated
        System.out.println("Game updated! White: " + game.getWhiteUsername() + ", Black: " + game.getBlackUsername());
    }
}








