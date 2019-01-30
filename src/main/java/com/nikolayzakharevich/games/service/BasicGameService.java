package com.nikolayzakharevich.games.service;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.getFirstName;

import com.nikolayzakharevich.exeptions.InvalidPayloadException;
import com.nikolayzakharevich.games.dao.Dao;
import com.nikolayzakharevich.games.dao.DaoImplementationChooser;

import java.util.List;

public class BasicGameService implements GameService {

    private int chatId;
    private Dao dao;

    BasicGameService(int chatId) {
        this.chatId = chatId;
        dao = DaoImplementationChooser.getDao(chatId);
    }

    @Override
    public void init(String gameName, int userId) {
        Game currentGame = getCurrentGame();
        if (currentGame != null) {
            dao.saveMessage("Нельзя начать более одной игры!");
            return;
        }

        switch (gameName) {
            case EPIC_BATTLE_NAME:
                currentGame = new EpicBattle(chatId);
                currentGame.init(userId);
                break;
            case ROCK_PAPER_SCISSORS_NAME:
                currentGame = new RockPaperScissors(chatId);
                currentGame.init(userId);
                break;
            default:
                throw new InvalidPayloadException();
        }
        dao.saveKeyboard(currentGame.getKeyboard());
        dao.saveMessage(currentGame.getMessage());
        dao.saveGame(currentGame);
    }

    @Override
    public void process(String text, int userId, String payload) {
        Game game = getCurrentGame();
        if (!isValidPlayer(game, userId)) {
            dao.saveMessage("@id" + userId + "(" + getFirstName(userId) + "), не лезь, блин\n");
        } else {
            game.processMessage(text, payload);
            if (game.isClosed()) {
                dao.closeGame(game);
            } else {
                dao.saveGame(game);
            }
            dao.saveMessage(game.getMessage());
            dao.saveKeyboard(game.getKeyboard());
        }
    }

    @Override
    public String getKeyboard() {
        return dao.getKeyboard();
    }

    @Override
    public String getMessage() {
        return dao.getMessage();
    }

    private boolean isValidPlayer(Game game, int userId) {
        return game.getCurrentPlayer().vkId == userId;
    }

    private Game getCurrentGame() {
        String gameName = dao.getCurrentGameType();
        Game result = null;
        if (gameName != null) {
            switch (gameName) {
                case EPIC_BATTLE_NAME:
                    result = dao.getCurrentGame(EpicBattle.class);
                    break;
                case ROCK_PAPER_SCISSORS_NAME:
                    result = dao.getCurrentGame(RockPaperScissors.class);
                    break;
            }
        }
        return result;
    }
}
