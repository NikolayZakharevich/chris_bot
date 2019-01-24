package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.tools.Color;
import com.nikolayzakharevich.tools.Keyboard;

import java.util.Random;

import static com.nikolayzakharevich.games.GameConstants.*;

class RockPaperScissors extends Game {

    private Random RANDOM = new Random();
    private RockPaperScissorsPlayer player;
    private int limit;

    RockPaperScissors() {
        super(ROCK_PAPER_SCISSORS_NAME);
    }

    @Override
    void init(int initiatorId, int... playerIds) {
        player = new RockPaperScissorsPlayer(initiatorId);
        players.add(player);
        currentPlayers.add(player);
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
    void processMessage(String text) {
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
                .addButton(ROCK_PAPER_SCISSORS_PAPER, Color.RED, ROCK_PAPER_SCISSORS_ACTION)
                .addButton(ROCK_PAPER_SCISSORS_ROCK, Color.BLUE, ROCK_PAPER_SCISSORS_ACTION)
                .addButton(ROCK_PAPER_SCISSORS_SCISSORS, Color.GREEN, ROCK_PAPER_SCISSORS_ACTION)
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

        message = "Крис использовал " + botAnswer + ", счет - " + player.getWins() + " - " + player.getLosses();
    }

    private void finalAction() {
        message += "\nИгра окончена";
        message += "\nПобедитель - " + (player.getWins() == limit ? "Человек" : "Робот");
        keyboard = Keyboard.builder().build();
    }

    private boolean isEndOfGame() {
        return player.getWins() == limit || player.getLosses() == limit;
    }
}
