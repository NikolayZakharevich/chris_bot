package com.nikolayzakharevich.games;

abstract class Player<T extends Game> {
    protected String vkId;
    protected String firstName;
    protected String lastName;

    Player(String vkId) {
        this.vkId = vkId;

    }

}
