package com.nikolayzakharevich.games;

import java.util.List;

import static com.nikolayzakharevich.games.GameConstants.*;

public class BasicGameClient implements GameClient {
    private Game currentGame;
    private String message;

    @Override
    public void init(String gameName, int userId) {
        switch (gameName) {
            case EPIC_BATTLE_NAME:
                // TODO: 20.01.19
                break;
            case ROCK_PAPER_SCISSORS_NAME:
                currentGame = new RockPaperScissors();
                currentGame.init(userId);
                break;

            default:
                // TODO: 20.01.19
        }
        message = currentGame.getMessage();
    }

    @Override
    public void process(String text, int userId) {
        if (!isValidPlayer(userId)) {
            message = "@id" + userId + ", не лезь, блин\n" + message;
        } else {
            currentGame.processMessage(text);
            message = currentGame.getMessage();
        }
    }

    private boolean isValidPlayer(int userId) {
        List<Player<?>> currentPlayers = currentGame.getCurrentPlayers();
        return currentPlayers.stream().anyMatch(player -> player.getVkId() == userId);
    }

    @Override
    public String getKeyboard() {
        return currentGame.getKeyboard();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
