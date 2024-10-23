package server;

import com.google.gson.Gson;
import model.AuthData;
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

            // Check if token is valid before proceeding
            if (authToken == null || !userService.isValidToken(authToken)) {
                res.status(401); // Unauthorized
                return gson.toJson(new ErrorResponse("Unauthorized: Invalid auth token"));
            }

            GameRequest gameRequest = gson.fromJson(req.body(), GameRequest.class);
            try {
                int gameID = gameService.createGame(gameRequest);
                res.status(200);
                return gson.toJson(new GameResponse(gameID));
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error creating game"));
            }
        });
    }

    public void joinGame() {
        put("/game", (req, res) -> {
            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            try {
                gameService.joinGame(joinRequest);
                res.status(200);
                return "{}";  // Empty JSON for success
            } catch (DataAccessException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            } catch (IllegalArgumentException e) {
                res.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken"));
            }
        });
    }

    public void listGames() {
        get("/game", (req, res) -> {
            String authToken = req.headers("Authorization");  // Use consistent case for header name
            try {
                // Validate the auth token
                if (authToken == null || !userService.isValidToken(authToken)) {
                    res.status(401);  // Unauthorized
                    return gson.toJson(new ErrorResponse("Unauthorized: Invalid auth token"));
                }

                List<GameData> games = gameService.listGames();
                res.status(200);
                return gson.toJson(new ListGameResponse(games));
            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error listing games"));
            }
        });
    }

    public void handleGameEndpoints() {
        createGame();
        joinGame();
        listGames();
    }
}







