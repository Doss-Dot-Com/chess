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
                return gson.toJson(auth);
            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });
    }

    public void logout() {
        delete("/session", (req, res) -> {
            String authToken = req.headers("authorization");
            try {
                userService.logout(authToken);
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        });
    }
}

