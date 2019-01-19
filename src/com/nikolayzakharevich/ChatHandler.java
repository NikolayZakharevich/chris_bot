package com.nikolayzakharevich;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.nikolayzakharevich.games.Game;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class ChatHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);

    ChatHandler(VkApiClient apiClient, UserActor actor) {
        super(apiClient, actor);
    }

    @Override
    void sayHi(int chatId) {
        try {
            if (RANDOM.nextInt(2) == 0) {
                tryKeyboard(chatId);
                return;
            }
            
            String code = "return API.messages.getConversationMembers(" +
                    "{\"peer_id\":" + (chatId + CHAT_ID_SHIFT) + ",\"v\":5.92});";
            JsonElement element = apiClient.execute()
                    .code(actor, code)
                    .execute();
            JsonArray members = element
                    .getAsJsonObject()
                    .get("profiles")
                    .getAsJsonArray();
            if (members.size() == 0) {
                LOG.error("Unable to say hi in chat " + chatId);
                return;
            }

            String userId = members
                    .get(RANDOM.nextInt(members.size()))
                    .getAsJsonObject()
                    .get("id")
                    .getAsString();
            UserXtrCounters user = apiClient.users()
                    .get(actor)
                    .userIds(String.valueOf(userId))
                    .execute()
                    .get(0);
            String message = "Бросаем кубик... Результат: " + user.getFirstName() + " - любит члены";
            apiClient.messages().send(actor)
                    .chatId(chatId)
                    .message(message)
                    .execute();
            
            LOG.info("Said hi to user " + userId + " in chat " + chatId);
        } catch (JsonSyntaxException | ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    void tryKeyboard(int chatId) {
        try {
            String keyboard = Keyboard.builder()
                    .addLabel(new Pair<>("ПУ", Color.WHITE))
                    .newRow()
                    .addLabel(new Pair<>("ТИ", Color.BLUE))
                    .newRow()
                    .addLabel(new Pair<>("Н", Color.RED))
                    .setOneTime(true)
                    .build();

            String message = "...";
            apiClient.messages().send(actor)
                    .chatId(chatId)
                    .message(message)
                    .unsafeParam("keyboard", keyboard)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    List<Game> getGameList() {
        return new ArrayList<>();
    }
}
