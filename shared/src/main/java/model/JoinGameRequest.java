package model;

public class JoinGameRequest {
    private int gameID;
    private String username;
    private String playerColor;

    public JoinGameRequest(int gameID, String username, String playerColor) {
        this.gameID = gameID;
        this.username = username;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    // Add this setter method to update the username
    public void setUsername(String username) {
        this.username = username;
    }
}




