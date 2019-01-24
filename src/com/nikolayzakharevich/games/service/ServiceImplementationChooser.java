package com.nikolayzakharevich.games.service;

public class ServiceImplementationChooser {

    public static GameService getGameService(int chatId) {
        return new BasicGameService(chatId);
    }
}
