package com.nikolayzakharevich.games.service;

abstract class Player<T extends Game> {

    protected int vkId;

    Player(int vkId) {
        this.vkId = vkId;
    }

    public int getVkId() {
        return vkId;
    }

}
