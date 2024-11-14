package client;

import org.junit.jupiter.api.*;
import server.Server;
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
    public void clearDatabase() throws Exception {
        facade.logout();
        // Assuming there is a clear endpoint or database reset functionality for testing
    }

    @Test
    public void testRegisterSuccess() {
        try {
            String response = facade.register("testUser", "password123", "test@example.com");
            assertNotNull(response);
            assertTrue(response.contains("authToken"));
        } catch (Exception e) {
            fail("Exception during successful registration: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterDuplicateUsername() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            Exception exception = assertThrows(Exception.class, () -> {
                facade.register("testUser", "password123", "test2@example.com");
            });
            assertTrue(exception.getMessage().contains("response code"));
        } catch (Exception e) {
            fail("Unexpected exception during duplicate username test: " + e.getMessage());
        }
    }

    @Test
    public void testLoginSuccess() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            String response = facade.login("testUser", "password123");
            assertNotNull(response);
            assertTrue(response.contains("authToken"));
        } catch (Exception e) {
            fail("Exception during successful login: " + e.getMessage());
        }
    }

    @Test
    public void testLoginInvalidCredentials() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("nonexistentUser", "wrongPassword");
        });
        assertTrue(exception.getMessage().contains("response code"));
    }

    @Test
    public void testLogoutSuccess() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            facade.login("testUser", "password123");
            String response = facade.logout();
            assertEquals("Logged out successfully", response);
        } catch (Exception e) {
            fail("Exception during successful logout: " + e.getMessage());
        }
    }

    @Test
    public void testCreateGameSuccess() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            facade.login("testUser", "password123");
            String response = facade.createGame("MyGame");
            assertNotNull(response);
            assertTrue(response.contains("gameId"));
        } catch (Exception e) {
            fail("Exception during game creation: " + e.getMessage());
        }
    }

    @Test
    public void testListGames() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            facade.login("testUser", "password123");
            facade.createGame("MyGame");
            String response = facade.listGames();
            assertNotNull(response);
            assertTrue(response.contains("MyGame"));
        } catch (Exception e) {
            fail("Exception during listing games: " + e.getMessage());
        }
    }

    @Test
    public void testJoinGameSuccess() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            facade.login("testUser", "password123");
            String gameResponse = facade.createGame("MyGame");
            String gameId = parseGameId(gameResponse); // Helper method to parse gameId from response
            String joinResponse = facade.joinGame(gameId, "white");
            assertNotNull(joinResponse);
            assertTrue(joinResponse.contains("joined"));
        } catch (Exception e) {
            fail("Exception during successful game join: " + e.getMessage());
        }
    }

    @Test
    public void testJoinNonexistentGame() {
        try {
            facade.register("testUser", "password123", "test@example.com");
            facade.login("testUser", "password123");
            Exception exception = assertThrows(Exception.class, () -> {
                facade.joinGame("nonexistentGameId", "white");
            });
            assertTrue(exception.getMessage().contains("response code"));
        } catch (Exception e) {
            fail("Unexpected exception during join nonexistent game test: " + e.getMessage());
        }
    }

    // Helper method to parse gameId from the createGame response (stub)
    private String parseGameId(String gameResponse) {
        int startIndex = gameResponse.indexOf("gameId") + 9;
        int endIndex = gameResponse.indexOf("\"", startIndex);
        return gameResponse.substring(startIndex, endIndex);
    }
}


