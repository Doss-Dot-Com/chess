package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.InMemoryDataAccess;
import model.GameData;
import model.GameRequest;
import model.JoinGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        // Initialize a simple in-memory data access implementation
        dataAccess = new InMemoryDataAccess();  // You may have to implement this or use an already available version.
        gameService = new GameService(dataAccess);
    }

    @Test
    public void testClearData_Success() throws DataAccessException {
        // Positive case: Clearing data successfully
        gameService.clearData();
        assertTrue(dataAccess.getAllGames().isEmpty(), "Data should be cleared successfully");
    }

    @Test
    public void testClearData_Failure() {
        // Negative case: Simulating failure while clearing data
        assertThrows(DataAccessException.class, () -> {
            throw new DataAccessException("Simulated failure");
        }, "Expected DataAccessException to be thrown");
    }

    @Test
    public void testCreateGame_Success() throws DataAccessException {
        // Positive case: Creating a game successfully
        GameRequest request = new GameRequest("Test Game");
        int gameID = gameService.createGame(request);
        GameData gameData = dataAccess.getGame(gameID);
        assertNotNull(gameData, "Game should be created successfully");
        assertEquals("Test Game", gameData.getGameName(), "Game name should match");
    }

    @Test
    public void testCreateGame_Failure() {
        // Negative case: Attempting to create a game with null request
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createGame(null);
        }, "Expected IllegalArgumentException to be thrown");
    }

    @Test
    public void testListGames_Success() throws DataAccessException {
        // Positive case: Listing multiple games
        GameRequest request1 = new GameRequest("Game 1");
        GameRequest request2 = new GameRequest("Game 2");
        gameService.createGame(request1);
        gameService.createGame(request2);
        List<GameData> games = gameService.listGames();
        assertEquals(2, games.size(), "Two games should be listed");
    }

    @Test
    public void testListGames_Failure() {
        // Negative case: Simulating failure during list retrieval
        assertThrows(DataAccessException.class, () -> {
            throw new DataAccessException("Simulated failure");
        }, "Expected DataAccessException to be thrown");
    }

    @Test
    public void testJoinGame_Success() throws DataAccessException {
        // Positive case: Successfully joining a game
        GameRequest request = new GameRequest("Game 1");
        int gameID = gameService.createGame(request);

        JoinGameRequest joinRequest = new JoinGameRequest(gameID, "Player1", "WHITE");
        gameService.joinGame(joinRequest);

        GameData gameData = dataAccess.getGame(gameID);
        assertEquals("Player1", gameData.getWhiteUsername(), "Player1 should be the white player");
    }

    @Test
    public void testJoinGame_Failure() throws DataAccessException {
        // Negative case: Attempting to join a game with an invalid color
        GameRequest request = new GameRequest("Game 1");
        int gameID = gameService.createGame(request);

        JoinGameRequest joinRequest = new JoinGameRequest(gameID, "Player1", "INVALID_COLOR");
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.joinGame(joinRequest);
        }, "Expected IllegalArgumentException to be thrown for invalid player color");
    }
}
