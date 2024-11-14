package client;

import model.GameRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Register user and get an auth token
    public String register(String username, String password, String email) throws IOException {
        URL url = new URL(serverUrl + "/user");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}", username, password, email);
        conn.getOutputStream().write(requestBody.getBytes());

        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            throw new IOException("Invalid input: username, password, and extraField are required.");
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Registration failed with response code: " + conn.getResponseCode());
        }

        return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
    }

    // Login and return auth token
    public String login(String username, String password) throws IOException {
        URL url = new URL(serverUrl + "/session");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        conn.getOutputStream().write(requestBody.getBytes());

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Login failed with response code: " + conn.getResponseCode());
        }

        // Read the response as a plain string
        return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
    }

        // Logout
        public String logout(String authToken) throws IOException {
            URL url = new URL(serverUrl + "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", authToken);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Logout failed with response code: " + conn.getResponseCode());
            }

            return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
        }

    // Create a new game
    public String createGame(String authToken, String gameName, String username, String password) throws IOException {
        try {
            return createGameRequest(authToken, gameName);  // Attempt the request
        } catch (IOException e) {
            if (e.getMessage().contains("401")) {  // Check for Unauthorized response
                System.out.println("Retrying createGame due to 401 Unauthorized...");
                authToken = login(username, password);  // Re-login to get a new token
                return createGameRequest(authToken, gameName);  // Retry the request
            } else {
                throw e;  // Propagate other errors
            }
        }
    }

    private String createGameRequest(String authToken, String gameName) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(serverUrl + "/game").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", authToken);  // Pass only the token, not JSON
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        GameRequest gameRequest = new GameRequest(gameName);
        String jsonInputString = gson.toJson(gameRequest);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("Game creation failed with response code: " + responseCode);
        }
    }

    // List games
    public String listGames(String authToken) throws IOException {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authToken);

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Listing games failed with response code: " + conn.getResponseCode());
        }

        return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
    }

    // Join game
    public String joinGame(String authToken, int gameId, String playerColor, String username) throws IOException {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"gameID\":%d,\"playerColor\":\"%s\",\"username\":\"%s\"}", gameId, playerColor, username);
        conn.getOutputStream().write(requestBody.getBytes());

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Join game failed with response code: " + conn.getResponseCode());
        }

        return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
    }
}






