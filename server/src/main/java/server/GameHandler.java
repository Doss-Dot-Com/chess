package server;

import com.google.gson.Gson;
import model.GameData;
import model.GameRequest;
import model.JoinGameRequest;
import model.ListGameResponse;
import model.GameResponse;
import model.ErrorResponse;
import service.GameService;
import service.UserService;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.List;
import java.util.Objects;

import static spark.Spark.*;

public class GameHandler {
    private final Gson gson = new Gson();
    private final GameService gameService;
    private final DataAccess dataAccess;
    private final UserService userService;

    public GameHandler(GameService gameService, DataAccess dataAccess, UserService userService) {
        this.gameService = gameService;
        this.dataAccess = dataAccess;
        this.userService = userService;
    }

    public void createGame() {
        post("/game", (req, res) -> {
            String authToken = req.headers("Authorization");
            if (authToken == null || !userService.isValidToken(authToken)) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            GameRequest gameRequest = gson.fromJson(req.body(), GameRequest.class);
            try {
                int gameID = gameService.createGame(gameRequest);
                res.status(200);
                return gson.toJson(new GameResponse(gameID));
            } catch (IllegalArgumentException e) {
                res.status(400);  // Bad request due to missing or invalid fields
                return gson.toJson(new ErrorResponse("Error: bad request"));
            } catch (DataAccessException e) {
                res.status(500);  // Internal server error
                return gson.toJson(new ErrorResponse("Error: server error"));
            }
        });
    }

    public void joinGame() {
        put("/game", (req, res) -> {
            String authToken = req.headers("Authorization");

            if (authToken == null || !userService.isValidToken(authToken)) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: Unauthorized"));
            }

            // Log the raw request body
            System.out.println("Raw request body: " + req.body());

            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);

            if (joinRequest.getGameID() == 0) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: gameID is missing or invalid"));
            }

            // Extract username from the authorization token
            String username = userService.getUsernameFromToken(authToken);
            joinRequest.setUsername(username);

            // Check for missing fields and handle errors gracefully
            if (joinRequest.getPlayerColor() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: Player color is missing"));
            }

            if (Objects.equals(joinRequest.getPlayerColor(), "WHITE")) {
                GameData game = dataAccess.getGame(joinRequest.getGameID());
                if (game.getWhiteUsername() != null) {
                    res.status(403);
                    return gson.toJson(new ErrorResponse("Error: user already exists"));
                }
            } else if (Objects.equals(joinRequest.getPlayerColor(), "BLACK")) {
                GameData game = dataAccess.getGame(joinRequest.getGameID());
                if (game.getBlackUsername() != null) {
                    res.status(403);
                    return gson.toJson(new ErrorResponse("Error: user already exists"));
                }
            }

            try {
                // Try to join the game
                gameService.joinGame(joinRequest);
                res.status(200);
                return "{}";  // Empty JSON for success
            } catch (IllegalArgumentException e) {
                System.out.println("Caught exception: " + e.getMessage());
                res.status(403);  // Forbidden when a player slot is already taken
                return gson.toJson(new ErrorResponse(e.getMessage()));  // Include the error message in the response
            } catch (DataAccessException e) {
                res.status(400);  // Bad request for other errors
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });
    }



    public void listGames() {
        get("/game", (req, res) -> {
            String authToken = req.headers("Authorization");

            // Validate the auth token
            if (authToken == null || !userService.isValidToken(authToken)) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            try {
                List<GameData> games = gameService.listGames();
                res.status(200);
                return gson.toJson(new ListGameResponse(games));
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error listing games"));
            }
        });
    }

    public void clearData() {
        delete("/db", (req, res) -> {
            String authToken = req.headers("Authorization");
            if (authToken == null || !userService.isValidToken(authToken)) {
                res.status(401);  // Unauthorized response
                return gson.toJson(new ErrorResponse("Error: Unauthorized"));
            }

            // Clear operation
            try {
                gameService.clearData();
                res.status(200);
                return "{}";  // Success response
            } catch (DataAccessException e) {
                res.status(500);  // Internal server error
                return gson.toJson(new ErrorResponse("Error: Server error"));
            }
        });
    }

    public void handleGameEndpoints() {
        createGame();
        joinGame();
        listGames();
    }
}








