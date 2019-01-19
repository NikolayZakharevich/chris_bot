package com.nikolayzakharevich;

import com.nikolayzakharevich.games.Game;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import java.util.List;
import java.util.Random;

abstract class BotRequestHandler {

    protected final static Random RANDOM = new Random();
    protected final static int CHAT_ID_SHIFT = 2000000000;
    protected static final int GROUP_ID = 156440140;
    protected final VkApiClient apiClient;
    protected final UserActor actor;

    BotRequestHandler(VkApiClient apiClient, UserActor actor) {
        this.apiClient = apiClient;
        this.actor = actor;
    }

    abstract void sayHi(int id);

    abstract List<Game> getGameList();
}

