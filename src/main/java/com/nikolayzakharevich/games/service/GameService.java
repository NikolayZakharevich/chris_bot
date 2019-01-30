package com.nikolayzakharevich.games.service;

public interface GameService {

    void init(String gameName, int userId);

    void process(String text, int userId, String payload);

    String getKeyboard();

    String getMessage();

}
