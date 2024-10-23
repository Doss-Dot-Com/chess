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
        // Game ID is properly generated and unique
        int gameID = UUID.randomUUID().hashCode();

        // Create a new GameData object using the game ID and the game name from the request
        GameData newGame = new GameData(gameID, gameRequest.getGameName());

        // Persist the new game to the data store
        dataAccess.createGame(newGame);

        // Return the generated game ID
        return gameID;
    }

    // Method to list all games
    public List<GameData> listGames() throws DataAccessException {
        return dataAccess.getAllGames();  // getAllGames is implemented in DataAccess
    }

    // Method to join a game
    public void joinGame(JoinGameRequest joinRequest) throws DataAccessException {
        GameData game = dataAccess.getGame(joinRequest.getGameID());

        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        String playerColor = joinRequest.getPlayerColor();
        if (playerColor == null) {
            throw new DataAccessException("Player color not specified");
        }

        if (playerColor.equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("White player slot already taken");
            }
            game.setWhiteUsername(joinRequest.getUsername());
        } else if (playerColor.equals("BLACK")) {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Black player slot already taken");
            }
            game.setBlackUsername(joinRequest.getUsername());
        } else {
            throw new DataAccessException("Invalid player color");
        }

        dataAccess.updateGame(game);  // Update the game in the data store
    }
}







