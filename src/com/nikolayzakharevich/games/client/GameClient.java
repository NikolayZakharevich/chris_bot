package com.nikolayzakharevich.games.client;

public interface GameClient {

    void init(int chatId, String gameName, int userId);

    void process(int chatId, String text, int userId);

    String getKeyboard(int chatId);

    String getMessage(int chatId);

}
