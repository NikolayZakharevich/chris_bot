package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

class RockPaperScissors extends Game<RockPaperScissorsPlayer> {

    private RockPaperScissorsPlayer player;
    private int limit;

    RockPaperScissors(int chatId) {
        super(ROCK_PAPER_SCISSORS_NAME, chatId);
    }

    @Override
    void init(int initiatorId, int... playerIds) {
        player = new RockPaperScissorsPlayer(initiatorId);
        players.add(player);
        currentPlayer = player;
        message = "Выберите лимит побед";

        Keyboard.Builder builder = Keyboard.builder();
        for (int i = 1; i < 10; i++) {
            builder.addButton(String.valueOf(i), Color.BLUE, ROCK_PAPER_SCISSORS_ACTION);
            if (i % 3 == 0) {
                builder.newRow();
            }
        }
        keyboard = builder.setOneTime(true).build();
    }

    @Override
    void processMessage(String text, String payload) {
        if (text.matches("[0-9]+")) {
            firstAction(text);
        } else {
            nextAction(text);
        }
        if (isEndOfGame()) {
            finalAction();
        }
    }

    private void firstAction(String text) {
        limit = Integer.parseInt(text);
        message = "Игра начинается. Выберите действие:";

        keyboard = Keyboard.builder()
                .addButton(ROCK_PAPER_SCISSORS_ROCK, Color.BLUE, ROCK_PAPER_SCISSORS_ACTION)
                .addButton(ROCK_PAPER_SCISSORS_SCISSORS, Color.GREEN, ROCK_PAPER_SCISSORS_ACTION)
                .addButton(ROCK_PAPER_SCISSORS_PAPER, Color.RED, ROCK_PAPER_SCISSORS_ACTION)
                .setOneTime(true)
                .build();
    }

    private void nextAction(String text) {

        String[] values = {ROCK_PAPER_SCISSORS_ROCK, ROCK_PAPER_SCISSORS_SCISSORS, ROCK_PAPER_SCISSORS_PAPER};
        String botAnswer = values[RANDOM.nextInt(values.length)];

        if (botAnswer.equals(text)) {
            player.addDraw();
        } else if (botAnswer.equals(ROCK_PAPER_SCISSORS_ROCK) && text.equals(ROCK_PAPER_SCISSORS_PAPER)
                || botAnswer.equals(ROCK_PAPER_SCISSORS_PAPER) && text.equals(ROCK_PAPER_SCISSORS_SCISSORS)
                || botAnswer.equals(ROCK_PAPER_SCISSORS_SCISSORS) && text.equals(ROCK_PAPER_SCISSORS_ROCK)) {
            player.addWin();
        } else {
            player.addLoss();
        }

        message = getFirstName(player.vkId) + " " + text + " vs. " + botAnswer + " Крис\n" +
                "счет: " + player.getWins() + " - " + player.getLosses();
    }

    private void finalAction() {
        isEnded = true;
        message += "\nИгра окончена";
        message += "\nПобедитель - " + (player.getWins() == limit ? getFirstName(player.vkId) : "Крис") + "!";
        keyboard = Keyboard.builder().build();
    }

    private boolean isEndOfGame() {
        return player.getWins() == limit || player.getLosses() == limit;
    }
}
