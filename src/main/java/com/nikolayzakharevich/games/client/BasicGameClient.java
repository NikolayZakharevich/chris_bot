package com.nikolayzakharevich.games.client;

import com.nikolayzakharevich.games.service.GameService;

public class BasicGameClient extends GameClient {

    @Override
    public void init(int chatId, String gameName, int userId) {
        GameService service = getChatService(chatId);
        service.init(gameName, userId);
    }

    @Override
    public void process(int chatId, String text, int userId, String payload) {
        GameService service = getChatService(chatId);
        service.process(text, userId, payload);
    }

    @Override
    public String getKeyboard(int chatId) {
        GameService service = getChatService(chatId);
        return service.getKeyboard();
    }

    @Override
    public String getMessage(int chatId) {
        GameService service = getChatService(chatId);
        return service.getMessage();
    }

}
