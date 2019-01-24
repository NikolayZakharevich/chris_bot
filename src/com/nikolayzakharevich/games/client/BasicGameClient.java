package com.nikolayzakharevich.games.client;

import com.nikolayzakharevich.games.service.GameService;
import com.nikolayzakharevich.games.service.ServiceImplementationChooser;

import java.util.HashMap;
import java.util.Map;

public class BasicGameClient implements GameClient {

    private Map<Integer, GameService> services = new HashMap<>();

    @Override
    public void init(int chatId, String gameName, int userId) {
        GameService service = getChatService(chatId);
        service.init(gameName, userId);
    }

    @Override
    public void process(int chatId, String text, int userId) {
        GameService service = getChatService(chatId);
        service.process(text, userId);
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

    private GameService getChatService(int chatId) {
        GameService service;
        if (!services.containsKey(chatId)) {
            service = ServiceImplementationChooser.getGameService(chatId);
            services.put(chatId, service);
        } else {
            service = services.get(chatId);
        }
        return service;
    }
}
