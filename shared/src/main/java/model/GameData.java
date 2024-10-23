package model;

public class GameData {
    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;

    public GameData(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = null;
        this.blackUsername = null;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

}



