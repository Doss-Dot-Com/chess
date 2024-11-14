package client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ServerFacade {
    private final String serverUrl;

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
    public String createGame(String authToken, String gameName) throws IOException {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"gameName\":\"%s\"}", gameName);
        conn.getOutputStream().write(requestBody.getBytes());

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Game creation failed with response code: " + conn.getResponseCode());
        }

        return new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
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






