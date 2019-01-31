package com.nikolayzakharevich.games.service;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.getFirstName;

import com.nikolayzakharevich.exeptions.InvalidPayloadException;
import com.nikolayzakharevich.games.dao.Dao;
import com.nikolayzakharevich.games.dao.DaoImplementationChooser;
import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import java.util.List;
import java.util.ArrayList;

public class BasicGameService implements GameService {

    private int chatId;
    private Dao dao;
    private Voting voting = new Voting();

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
            if (game.isEnded()) {
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

    @Override
    public String getVoteMessage() {
        return voting.message;
    }

    @Override
    public String getVoteKeyboard() {
        return voting.keyboard;
    }

    private class Voting {
        int voteYes;
        int voteNo;
        String message = NO_VOTE_RUNNING;
        String keyboard = Keyboard.builder().build();
        boolean isRunning;
        boolean successful;

        List<Integer> voted = new ArrayList<>();

        void init(int userId) {

            if (isRunning) {
                message = VOTE_ALREADY_RUNNING;
                return;
            }

            Game<Player> game = getCurrentGame();
            if (game == null) {
                message = "Нет текущих игр";
            } else if (game.players.stream()
                    .anyMatch(player -> player.vkId == userId)) {
                voteYes = 1;
                voteNo = 0;
                isRunning = true;
                voted.add(userId);
                keyboard = Keyboard.builder()
                        .addButton(VOTE_YES, Color.GREEN, VOTE_STOP_GAME)
                        .addButton(VOTE_NO, Color.RED, VOTE_STOP_GAME)
                        .build();
                message = "Голосование за прекращение игры: за - " + voteYes + ", против - " + voteNo;
            } else {
                message = getFirstName(userId) + ", вы не играете сейчас";
            }
        }

        void addVote(String text, int userId) {
            if (getCurrentGame().players.stream()
                    .anyMatch(player -> player.vkId == userId) && !voted.contains(userId)) {
                if (text.equalsIgnoreCase(VOTE_YES)) {
                    voteYes++;
                } else if (text.equalsIgnoreCase(VOTE_NO)) {
                    voteNo++;
                }
                message = "Голосование за прекращение игры: за - " + voteYes + ", против - " + voteNo;
                voted.add(userId);
            }

            keyboard = Keyboard.builder()
                    .addButton(VOTE_YES, Color.GREEN, VOTE_STOP_GAME)
                    .addButton(VOTE_NO, Color.RED, VOTE_STOP_GAME)
                    .build();
        }

        void check() {
            successful = voteYes > voteNo;
            if (successful) {
                message = "Игра остановлена!";
            } else {
                message = "Недостаточно голосов для отмены";
            }
            keyboard = Keyboard.builder().build();
            voted.clear();
            isRunning = false;
        }
    }

    @Override
    public void startVoting(int userId) {
        voting.init(userId);
    }

    @Override
    public void processVote(String text, int userId) {
       voting.addVote(text, userId);
    }

    @Override
    public void checkVotingResults() {
        voting.check();
        if (voting.successful) {
            dao.closeGame(getCurrentGame());
        }
    }

    private boolean isValidPlayer(Game game, int userId) {
        return game.currentPlayer.vkId == userId;
    }

    private Game<Player> getCurrentGame() {
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
