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
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws IOException, DataAccessException {
        server.dataAccess.clear(); // Assuming `clear()` resets the necessary data
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
            // Register with invalid inputs to trigger IOException
            facade.register("", "password123", "invalidemail");
        }, "Expected java.io.IOException to be thrown, but nothing was thrown.");
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
        assertEquals("{}", response);  // Assuming a successful logout returns an empty JSON
    }

    @Test
    public void testLogoutFailure() {
        assertThrows(IOException.class, () -> {
            facade.logout("invalidToken");
        });
    }

    @Test
    public void testCreateGameSuccess() throws IOException {
        // Register and log in to retrieve a valid authToken
        facade.register("loginUser", "password123", "loginuser@example.com");
        String authToken = facade.login("loginUser", "password123");

        // Log the authToken for debugging
        System.out.println("Obtained authToken for testCreateGameSuccess: " + authToken);

        // Ensure the authToken is valid and attempt to create the game
        assertNotNull(authToken, "Auth token should not be null after login");
        assertTrue(authToken.length() > 0, "Auth token should not be empty");

        // Create game and handle possible IOException
        try {
            String response = facade.createGame(authToken, "Game1");
            assertNotNull(response, "Response from createGame should not be null");
            assertTrue(response.contains("gameId"), "Response should contain 'gameId'");
        } catch (IOException e) {
            fail("Game creation failed with message: " + e.getMessage());
        }
    }


    @Test
    public void testCreateGameFailure() {
        assertThrows(IOException.class, () -> {
            facade.createGame("invalidToken", "");  // Empty game name and invalid token
        });
    }

    @Test
    public void testListGamesSuccess() throws IOException {
        String authToken = facade.login("loginUser", "password123");
        facade.createGame(authToken, "GameListTest");
        String response = facade.listGames(authToken);
        assertNotNull(response);
        assertTrue(response.contains("games"));
    }

    @Test
    public void testListGamesFailure() {
        assertThrows(IOException.class, () -> {
            facade.listGames("invalidToken");  // Edge case with an invalid token
        });
    }

    @Test
    public void testJoinGameSuccess() throws IOException {
        String authToken = facade.login("loginUser", "password123");
        String gameId = facade.createGame(authToken, "JoinableGame");

        // Ensure `joinGame` receives all necessary parameters
        String response = facade.joinGame(authToken, Integer.parseInt(gameId), "white", "exampleUsername");
        assertNotNull(response);
        assertTrue(response.contains("joined"));
    }

    @Test
    public void testJoinGameFailure() {
        assertThrows(IOException.class, () -> {
            // Using an invalid token and missing game ID
            facade.joinGame("invalidToken", 99999, "white", "exampleUsername");
        });
    }
}







