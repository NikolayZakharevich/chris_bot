package com.nikolayzakharevich.games;

import java.util.ArrayList;
import java.util.List;

abstract class Game {

    protected final String name;
    protected List<Player<?>> players = new ArrayList<>();
    protected List<Player<?>> currentPlayers = new ArrayList<>();
    protected String keyboard;
    protected String message;

    Game(String name) {
        this.name = name;
    }

    abstract void init(int initiatorId, int... playerIds);

    abstract void processMessage(String text);

    String getKeyboard() {
        return keyboard;
    }

    String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    List<Player<?>> getPlayers() {
        return players;
    }

    List<Player<?>> getCurrentPlayers() {
        return currentPlayers;
    }

    String getPlayerList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Player player : players) {
            stringBuilder.append(player.firstName)
                    .append(" ").append(player.lastName).append("\n");
        }
        return stringBuilder.toString();
    }

}
