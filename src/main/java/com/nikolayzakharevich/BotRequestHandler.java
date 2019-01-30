package com.nikolayzakharevich;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;

import java.util.Random;

public abstract class BotRequestHandler {

    protected final static Random RANDOM = new Random();
    protected static final int GROUP_ID = 156440140;

    public final static int CHAT_ID_SHIFT = 2000000000;
    public static VkApiClient apiClient;
    public static GroupActor actor;

    BotRequestHandler(VkApiClient client, GroupActor groupActor) {
        apiClient = client;
        actor = groupActor;
    }

    abstract void processMessage(int userId, int chatId, String text);

    abstract void processMessage(int userId, int chatId, String text, String payload);

    abstract void sayHi(int chatId);

    abstract void getGameList(int chatId);
}

