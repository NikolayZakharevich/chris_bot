package com.nikolayzakharevich.games.service;

class RockPaperScissorsPlayer extends Player<RockPaperScissors> {

    private int wins;
    private int draws;
    private int losses;

    RockPaperScissorsPlayer(int vkId) {
        super(vkId);
    }

    void addWin() {
        wins++;
    }

    void addDraw() {
        draws++;
    }

    void addLoss() {
        losses++;
    }

    int getWins() {
        return wins;
    }

    int getDraws() {
        return draws;
    }

    int getLosses() {
        return losses;
    }

}
