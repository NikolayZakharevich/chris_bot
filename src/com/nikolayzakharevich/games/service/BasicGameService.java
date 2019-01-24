package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.games.dao.Dao;
import com.nikolayzakharevich.games.dao.DaoImplementationChooser;

import java.util.List;

import static com.nikolayzakharevich.games.GameConstants.EPIC_BATTLE_NAME;
import static com.nikolayzakharevich.games.GameConstants.ROCK_PAPER_SCISSORS_NAME;

public class BasicGameService implements GameService {

    private int chatId;
    private Game currentGame;
    private String message;
    private Dao dao;

    public BasicGameService(int chatId) {
        this.chatId = chatId;
        dao = DaoImplementationChooser.getDao();
    }

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

    @Override
    public String getKeyboard() {
        return currentGame.getKeyboard();
    }

    @Override
    public String getMessage() {
        return message;
    }

    private boolean isValidPlayer(int userId) {
        List<Player<?>> currentPlayers = currentGame.getCurrentPlayers();
        return currentPlayers.stream().anyMatch(player -> player.getVkId() == userId);
    }
}
