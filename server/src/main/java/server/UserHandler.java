package server;

import com.google.gson.Gson;
import service.UserService;
import model.*;
import dataaccess.DataAccessException;
import service.UserAlreadyExistsException;
import model.ErrorResponse;

import static spark.Spark.*;

public class UserHandler {
    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register() {
        post("/user", (req, res) -> {
            UserData user = gson.fromJson(req.body(), UserData.class);
            try {
                AuthData auth = userService.register(user);
                res.status(200);
                return gson.toJson(auth);
            } catch (UserAlreadyExistsException e) {
                res.status(403);  // User already exists
                return gson.toJson(new ErrorResponse("Error: user already exists"));
            } catch (IllegalArgumentException e) {
                res.status(400);  // Bad request due to missing fields
                return gson.toJson(new ErrorResponse("Error: missing or invalid fields"));
            } catch (DataAccessException e) {
                res.status(500);  // Internal server error
                return gson.toJson(new ErrorResponse("Error: server error"));
            }
        });
    }

    public void login() {
        post("/session", (req, res) -> {
            UserData user = gson.fromJson(req.body(), UserData.class);
            try {
                AuthData auth = userService.login(user);
                res.status(200);
                System.out.println("Login response status: 200 OK"); // Log successful login response
                return gson.toJson(auth);
            } catch (DataAccessException e) {
                res.status(401);
                System.out.println("Login failed: Unauthorized, status 401"); // Log unauthorized response
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });
    }

    public void logout() {
        delete("/session", (req, res) -> {
            String authToken = req.headers("Authorization");

            // Respond with 401 if the authorization token is missing or empty
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            try {
                userService.logout(authToken);  // Call the UserService to handle logout
                res.status(200);  // Set status to 200 OK on success
                return "{}";  // Return an empty JSON object for successful logout
            } catch (DataAccessException e) {
                // Return 401 Unauthorized if the token is invalid
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });
    }
}


