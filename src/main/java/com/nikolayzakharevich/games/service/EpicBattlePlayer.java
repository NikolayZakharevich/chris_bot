package com.nikolayzakharevich.games.service;

class EpicBattlePlayer extends Player<EpicBattle> {

    int hp;
    int mana;
    int souls;
    int arrows;
    int fireOrbs;
    int iceOrbs;

    String hero;

    EpicBattlePlayer(int vkId) {
        super(vkId);
    }
}
