package com.nikolayzakharevich.games.service;

class EpicBattlePlayer extends Player<EpicBattle> {

    private int hp;

    EpicBattlePlayer(int vkId) {
        super(vkId);
    }

    int getHp() {
        return hp;
    }

    void setHp(int hp) {
        this.hp = hp;
    }
}
