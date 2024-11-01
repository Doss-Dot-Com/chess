package server;

import dataaccess.DatabaseManager;
import dataaccess.InMemoryDataAccess;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import static spark.Spark.*;

public class Server {

    private InMemoryDataAccess dataAccess;
    private UserService userService;
    private GameService gameService;

    // Method to start the server on a given port
    public int run(int port) {
        // Initialize the database and tables if they don’t exist
        DatabaseManager.initializeDatabase();

        // Set the port for the server
        port(port);

        // Serve static files
        staticFiles.location("/web");

        // Initialize data access and services
        dataAccess = new InMemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        // Handlers for users and games
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService, dataAccess, userService);

        // Register the user-related routes
        userHandler.register();
        userHandler.login();
        userHandler.logout();

        // Register the game-related routes
        gameHandler.createGame();
        gameHandler.joinGame();
        gameHandler.listGames();
        //gameHandler.clearData();

        // Root route
        get("/", (req, res) -> "Welcome to the User Service!");

        // Handle favicon.ico requests
        get("/favicon.ico", (req, res) -> {
            res.status(404);
            return "";
        });

        // Route to clear the database (DELETE /db)
        delete("/db", (req, res) -> {
            try {
                dataAccess.clear();
                res.status(200);
                return "{}";  // Return an empty JSON object
            } catch (Exception e) {
                res.status(500);
                return "{\"message\": \"Error: " + e.getMessage() + "\"}";
            }
        });

        // Wait for Spark to be fully initialized
        awaitInitialization();

        // Output to indicate the server is running
        System.out.println("Server started on port " + port);

        // Return the port
        return port();
    }

    // Method to stop the server
    public void stop() {
        Spark.stop();
    }

    // Main method to start the server
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);  // Start the server on port 8080
    }
}



