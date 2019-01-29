package com.nikolayzakharevich.games.dao;

import com.nikolayzakharevich.games.service.Game;

public interface Dao {

    String getCurrentGameType();

    String getMessage();

    String getKeyboard();

    <T extends Game> T getCurrentGame(Class<T> clazz);

    void saveGame(Game game);

    void saveMessage(String message);

    void saveKeyboard(String keyboard);

    void closeGame(Game game);

}
