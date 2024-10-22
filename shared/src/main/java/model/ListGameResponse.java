package model;

import java.util.List;

public class ListGameResponse {
    private List<GameData> games;

    public ListGameResponse(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() {
        return games;
    }

    public void setGames(List<GameData> games) {
        this.games = games;
    }
}

