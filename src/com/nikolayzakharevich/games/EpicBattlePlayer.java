package com.nikolayzakharevich.games;

class EpicBattlePlayer extends Player<EpicBattle> {
    private int hp;

    EpicBattlePlayer(String vkId) {
        super(vkId);
    }


    int getHp() {
        return hp;
    }

    void setHp(int hp) {
        this.hp = hp;
    }
}
