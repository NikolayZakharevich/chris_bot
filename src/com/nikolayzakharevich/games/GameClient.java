package com.nikolayzakharevich.games;

public interface GameClient {

    void init(String gameName, int userId);

    void process(String text, int userId);

    String getKeyboard();

    String getMessage();

}
