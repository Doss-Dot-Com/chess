package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new InMemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    public void testClearDataSuccess() throws DataAccessException {
        gameService.clearData();
        assertTrue(dataAccess.getAllGames().isEmpty());
    }

    @Test
    public void testClearDataFailure() throws DataAccessException {
        assertDoesNotThrow(() -> gameService.clearData());
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        GameRequest gameRequest = new GameRequest("TestGame");
        int gameId = gameService.createGame(gameRequest);
        assertNotNull(dataAccess.getGame(gameId));
    }

    @Test
    public void testCreateGameFailure() {
        GameRequest gameRequest = new GameRequest(null);
        assertThrows(IllegalArgumentException.class, () -> gameService.createGame(gameRequest));
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        GameRequest gameRequest1 = new GameRequest("Game1");
        GameRequest gameRequest2 = new GameRequest("Game2");
        gameService.createGame(gameRequest1);
        gameService.createGame(gameRequest2);
        List<GameData> games = gameService.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesFailure() throws DataAccessException {
        assertDoesNotThrow(() -> gameService.listGames());
    }

    @Test
    public void testJoinGameSuccess() throws DataAccessException {
        GameRequest gameRequest = new GameRequest("TestGame");
        int gameId = gameService.createGame(gameRequest);
        JoinGameRequest joinRequest = new JoinGameRequest(gameId, "existingUser", "WHITE");
        gameService.joinGame(joinRequest);
        GameData gameData = dataAccess.getGame(gameId);
        assertEquals("existingUser", gameData.getWhiteUsername());
    }

    @Test
    public void testJoinGameFailure() throws DataAccessException {
        GameRequest gameRequest = new GameRequest("TestGame");
        int gameId = gameService.createGame(gameRequest);
        JoinGameRequest joinRequest = new JoinGameRequest(gameId, "existingUser", "RED");
        assertThrows(IllegalArgumentException.class, () -> gameService.joinGame(joinRequest));
    }
}
