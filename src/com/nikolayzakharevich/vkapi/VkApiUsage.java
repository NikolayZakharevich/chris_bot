package com.nikolayzakharevich.vkapi;

import static com.nikolayzakharevich.BotRequestHandler.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UsersNameCase;

import java.util.ArrayList;
import java.util.List;

public class VkApiUsage {

    public static String getFirstName(int vkId, UsersNameCase nameCase) {
        return getVkUser(vkId, nameCase).getFirstName();
    }

    public static String getLastName(int vkId, UsersNameCase nameCase) {
        return getVkUser(vkId, nameCase).getLastName();
    }

    public static String getFirstName(int vkId) {
        return getVkUser(vkId).getFirstName();
    }

    public static String getLastName(int vkId) {
        return getVkUser(vkId).getLastName();
    }

    private static UserXtrCounters getVkUser(int vkId) {
        UserXtrCounters user = null;
        try {
            user = apiClient.users().get(actor).userIds(String.valueOf(vkId)).execute().get(0);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return user;
    }

    private static UserXtrCounters getVkUser(int vkId, UsersNameCase nameCase) {
        UserXtrCounters user = null;
        try {
            user = apiClient.users().get(actor).userIds(String.valueOf(vkId)).nameCase(nameCase).execute().get(0);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void sendKeyboardMessage(int chatId, String message, String keyboard) {
        try {
            apiClient.messages().send(actor)
                    .chatId(chatId)
                    .message(message)
                    .unsafeParam("keyboard", keyboard)
                    .execute();
        } catch (ApiException | ClientException e) {
            // ignore
        }
    }

    public static void sendMessage(int chatId, String message) {
        try {
            apiClient.messages().send(actor)
                    .chatId(chatId)
                    .message(message)
                    .execute();
        } catch (ApiException | ClientException e) {
            // ignore
        }
    }

    public static List<Integer> getChatMembersIds(int chatId) {
        String code = "return API.messages.getConversationMembers(" +
                "{\"peer_id\":" + (chatId + CHAT_ID_SHIFT) + ",\"v\":5.92});";
        JsonElement element;
        try {
            element = apiClient.execute()
                    .code(actor, code)
                    .execute();
        } catch (ApiException | ClientException e) {
            return null;
        }
        JsonArray members = element
                .getAsJsonObject()
                .get("profiles")
                .getAsJsonArray();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            result.add(members.getAsJsonObject()
                    .get("id")
                    .getAsInt());
        }
        return result;
    }
}
