package com.nikolayzakharevich;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;

import java.util.List;
import java.util.Random;

abstract class BotRequestHandler {

    protected final static Random RANDOM = new Random();
    protected final static int CHAT_ID_SHIFT = 2000000000;
    protected static final int GROUP_ID = 156440140;
    protected final VkApiClient apiClient;
    protected final GroupActor actor;

    BotRequestHandler(VkApiClient apiClient, GroupActor actor) {
        this.apiClient = apiClient;
        this.actor = actor;
    }

    abstract void processMessage(int userId, int chatId, String text);

    abstract void processMessage(int userId, int chatId, String text, String payload);

    abstract void sayHi(int chatId);

    abstract void getGameList(int chatId);
}

