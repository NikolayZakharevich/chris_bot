package com.nikolayzakharevich.games.service;

abstract class Player {

    int vkId;

    Player(int vkId) {
        this.vkId = vkId;
    }

    Player(Player currentPlayer) {
        int temp = vkId;
        vkId = currentPlayer.vkId;
        currentPlayer.vkId = temp;
    }


}
