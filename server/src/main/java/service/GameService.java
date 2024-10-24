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

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        GameData game = dataAccess.getGame(request.getGameID());

        System.out.println("Joining game ID: " + request.getGameID() + " as " + request.getPlayerColor() + " with username: " + request.getUsername());

        if (request.getPlayerColor().equalsIgnoreCase("WHITE")) {
            System.out.println("Current whiteUsername: " + game.getWhiteUsername());
            if (game.getWhiteUsername() != null) {
                System.out.println("White player slot already taken");
                throw new IllegalArgumentException("Error: unauthorized");
            }
            game.setWhiteUsername(request.getUsername());
        } else if (request.getPlayerColor().equalsIgnoreCase("BLACK")) {
            System.out.println("Current blackUsername: " + game.getBlackUsername());
            if (game.getBlackUsername() != null) {
                System.out.println("Black player slot already taken");
                throw new IllegalArgumentException("Error: unauthorized");
            }
            game.setBlackUsername(request.getUsername());
        } else {
            throw new IllegalArgumentException("Invalid player color");
        }

        // After game updated
        System.out.println("Game updated! White: " + game.getWhiteUsername() + ", Black: " + game.getBlackUsername());
    }
}








