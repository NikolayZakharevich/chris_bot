package com.nikolayzakharevich.games;

import java.util.List;

public abstract class Game {

    final String name;
    protected List<Player> players;

    public Game(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    List<Player> getPlayers() {
        return players;
    }

    public String getPlayerList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Player player : players) {
            stringBuilder.append(player.firstName)
                    .append(" ").append(player.lastName).append("\n");
        }
        return stringBuilder.toString();
    }

}
