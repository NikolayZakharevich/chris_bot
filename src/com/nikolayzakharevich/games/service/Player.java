package com.nikolayzakharevich.games.service;

abstract class Player<T extends Game> {

    protected int vkId;
    protected String firstName;
    protected String lastName;

    Player(int vkId) {
        this.vkId = vkId;
// TODO: 20.01.19
    }

    public int getVkId() {
        return vkId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
