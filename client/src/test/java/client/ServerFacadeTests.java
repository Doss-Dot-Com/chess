package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws InterruptedException {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);

        // Wait for a moment to ensure server is fully initialized
        Thread.sleep(1000);  // Wait for 1 second
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

        // Log the response for inspection
        System.out.println("Login response: " + response);

        assertNotNull(response, "Response should not be null");
        assertTrue(response.contains("authToken"), "Response should contain 'authToken'");
    }

    @Test
    public void testLoginFailure() {
        assertThrows(IOException.class, () -> {
            facade.login("invalidUser", "wrongPassword");
        });
    }

    @Test
    public void testLogoutSuccess() throws IOException {
        facade.register("loginUser", "password123", "loginuser@example.com");
        String loginResponse = facade.login("loginUser", "password123");

        // Parse the authToken from the login response
        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        System.out.println("Auth token for logout: " + authToken);

        // Ensure authToken is valid before proceeding
        assertNotNull(authToken, "Auth token should not be null after login");
        assertFalse(authToken.isEmpty(), "Auth token should not be empty");

        // Perform logout with the parsed authToken
        String response = facade.logout(authToken);
        assertEquals("{}", response, "Expected successful logout to return an empty JSON object");
    }


    @Test
    public void testLogoutFailure() {
        assertThrows(IOException.class, () -> {
            facade.logout("invalidToken");
        });
    }

    @Test
    public void testCreateGameSuccess() throws IOException {
        facade.register("userListGames", "password123", "userlistgames@example.com");
        String loginResponse = facade.login("userListGames", "password123");

        // Parse the authToken from the login response
        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        System.out.println("Auth token for create game: " + authToken);

        // Ensure authToken is valid before proceeding
        assertNotNull(authToken, "Auth token should not be null after login");
        assertFalse(authToken.isEmpty(), "Auth token should not be empty");

        // Perform create game request with the parsed authToken
        String response = facade.createGame(authToken, "GameListTest");
        assertNotNull(response, "Response should not be null for a successful game creation");
    }


    @Test
    public void testCreateGameFailure() {
        assertThrows(IOException.class, () -> {
            facade.createGame("invalidToken", "");  // Empty game name and invalid token
        });
    }

    @Test
    public void testListGamesSuccess() throws IOException {
        facade.register("userListGames", "password123", "listgames@example.com");
        String authToken = facade.login("userListGames", "password123");

        // Confirm authToken is valid before listing games
        assertNotNull(authToken, "Auth token should not be null after login");
        assertFalse(authToken.isEmpty(), "Auth token should not be empty");

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
        facade.register("loginUser", "password123", "loginuser@example.com");
        String loginResponse = facade.login("loginUser", "password123");

        // Parse the authToken from the login response
        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        System.out.println("Auth token for join game: " + authToken);

        // Ensure authToken is valid before proceeding
        assertNotNull(authToken, "Auth token should not be null after login");
        assertFalse(authToken.isEmpty(), "Auth token should not be empty");

        // Create a game and retrieve the game ID (assuming createGame returns the game ID in its response)
        String gameCreateResponse = facade.createGame(authToken, "TestGame");
        JsonObject gameResponseObject = JsonParser.parseString(gameCreateResponse).getAsJsonObject();
        int gameId = gameResponseObject.get("gameID").getAsInt();

        // Perform join game request with the parsed authToken and game ID
        String joinResponse = facade.joinGame(authToken, gameId, "WHITE");
        assertNotNull(joinResponse, "Response should not be null for a successful join game");
    }

    @Test
    public void testJoinGameFailure() {
        assertThrows(IOException.class, () -> {
            // Using an invalid token and missing game ID
            facade.joinGame("invalidToken", 99999, "white");
        });
    }
}







