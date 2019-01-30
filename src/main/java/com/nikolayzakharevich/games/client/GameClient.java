package com.nikolayzakharevich.games.client;

import com.nikolayzakharevich.games.service.GameService;
import com.nikolayzakharevich.games.service.ServiceImplementationChooser;

import java.util.HashMap;
import java.util.Map;

public abstract class GameClient {

    private Map<Integer, GameService> services = new HashMap<>();

    public abstract void init(int chatId, String gameName, int userId);

    public abstract void process(int chatId, String text, int userId, String payload);

    public abstract String getKeyboard(int chatId);

    public abstract String getMessage(int chatId);

    GameService getChatService(int chatId) {
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
