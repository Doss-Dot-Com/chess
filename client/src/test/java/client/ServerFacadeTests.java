package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws IOException, DataAccessException {
        server.dataAccess.clear();
    }

    @Test
    public void testRegisterSuccess() throws IOException {
        String response = facade.register("newUser", "password123", "newuser@example.com");
        assertNotNull(response);
        assertTrue(response.contains("authToken"));
    }

    @Test
    public void testRegisterFailure() {
        assertThrows(IOException.class, () -> {
            facade.register("", "password123", "invalidemail");
        });
    }

    @Test
    public void testLoginSuccess() throws IOException {
        facade.register("loginUser", "password123", "loginuser@example.com");
        String response = facade.login("loginUser", "password123");
        assertNotNull(response);
        assertTrue(response.contains("authToken"));
    }

    @Test
    public void testLoginFailure() {
        assertThrows(IOException.class, () -> {
            facade.login("invalidUser", "wrongPassword");
        });
    }

    @Test
    public void testLogoutSuccess() throws IOException {
        String authToken = facade.login("loginUser", "password123");
        String response = facade.logout(authToken);
        assertEquals("{}", response);
    }

    @Test
    public void testLogoutFailure() {
        assertThrows(IOException.class, () -> {
            facade.logout("invalidToken");
        });
    }

    @Test
    public void testCreateGameSuccess() throws IOException {
        String response = facade.createGame("Game1");
        assertNotNull(response);
        assertTrue(response.contains("gameId"));
    }

    @Test
    public void testCreateGameFailure() {
        assertThrows(IOException.class, () -> {
            facade.createGame("");  // Empty game name
        });
    }

    @Test
    public void testListGamesSuccess() throws IOException {
        facade.createGame("GameListTest");
        String response = facade.listGames();
        assertNotNull(response);
        assertTrue(response.contains("games"));
    }

    @Test
    public void testListGamesFailure() {
        assertThrows(IOException.class, () -> {
            facade.listGames(); // Edge case: simulate server error if possible
        });
    }

    @Test
    public void testJoinGameSuccess() throws IOException {
        int gameId = Integer.parseInt(facade.createGame("JoinableGame"));  // Ensure gameId is an integer
        String response = facade.joinGame(gameId, "white", "testUser");  // Provide `username` as third argument
        assertNotNull(response);
        assertTrue(response.contains("joined"));
    }

    @Test
    public void testJoinGameFailure() {
        assertThrows(IOException.class, () -> {
            facade.joinGame(-1, "white", "testUser");  // Use invalid game ID
        });
    }
}





