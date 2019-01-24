package com.nikolayzakharevich.games.service;

public interface GameService {

    void init(String gameName, int userId);

    void process(String text, int userId);

    String getKeyboard();

    String getMessage();

}
