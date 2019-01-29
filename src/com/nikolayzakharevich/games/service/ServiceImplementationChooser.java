package com.nikolayzakharevich.games.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;

public class ServiceImplementationChooser {

    public static GameService getGameService(int chatId) {
        return new BasicGameService(chatId);
    }
}
