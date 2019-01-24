package com.nikolayzakharevich.games.dao;

import java.util.List;

public interface Dao {

    void addChat(int chatId);

    void addGame(String name, List<Integer> playerIds);

    void addPlayer(int vkId);

}
