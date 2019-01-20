package com.nikolayzakharevich;

import static com.nikolayzakharevich.games.GameConstants.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.nikolayzakharevich.games.BasicGameClient;
import com.nikolayzakharevich.games.GameClient;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class ChatHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);

    private final static String EPIC_BATTLE_INIT = "{\"init_game\":\"epic_battle\"}";
    private final static String ROCK_PAPER_SCISSORS_INIT = "{\"init_game\":\"rock_paper_scissors\"}";
    private final static String ROCK_PAPER_SCISSORS_ACTION = "{\"game_action\":\"rock_paper_scissors\"}";

    private Map<Integer, GameClient> database = new HashMap<>();

    ChatHandler(VkApiClient apiClient, GroupActor actor) {
        super(apiClient, actor);
    }

    @Override
    void sayHi(int chatId) {
        try {

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
            // ignore
        }
    }

    @Override
    void processMessage(int userId, int chatId, String text, String payload) {
        GameClient gameClient;
        if (database.containsKey(chatId)) {
            gameClient = database.get(chatId);
        } else {
            gameClient = new BasicGameClient();
            database.put(chatId, gameClient);
        }

        text = text.substring(text.indexOf(" ") + 1);
        switch (payload) {
            case EPIC_BATTLE_INIT:
                // TODO: 20.01.19
                break;
            case ROCK_PAPER_SCISSORS_INIT:
                LOG.info("Initialization of RockPaperScissors in chat " + chatId);
                gameClient.init(text, userId);
                sendKeyboardMessage(chatId, gameClient.getMessage(), gameClient.getKeyboard());
                break;
            case ROCK_PAPER_SCISSORS_ACTION:
                LOG.info("Action of RockPaperScissors in chat " + chatId);
                gameClient.process(text, userId);
                sendKeyboardMessage(chatId, gameClient.getMessage(), gameClient.getKeyboard());
                break;
            default:
                LOG.error("WTF");
                // TODO: 20.01.19
        }

    }

    @Override
    void processMessage(int userId, int chatId, String text) {
        if (text.matches("([0-9]+\\s?)+")) {
            tryKeyboard(chatId, text);
        } else {
            if (text.equalsIgnoreCase("список игр")) {
                getGameList(chatId);
            }
        }
    }

    private void sendKeyboardMessage(int chatId, String message, String keyboard) {
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

    private void tryKeyboard(int chatId, String text) {
        String[] parameters = text.split("\\s");
        Keyboard.Builder builder = Keyboard.builder();
        Color[] color = {Color.BLUE, Color.RED, Color.WHITE, Color.GREEN};
        int curr = 1;

        for (String parameter : parameters) {
            for (int j = 0; j < Integer.parseInt(parameter); j++) {
                builder.addButton(String.valueOf(curr++), color[RANDOM.nextInt(color.length)]);
            }
            builder.newRow();
        }
        String keyboard = builder.build();

        String message = "...";

        sendKeyboardMessage(chatId, message, keyboard);

    }

    @Override
    void getGameList(int chatId) {
        String keyboard = Keyboard.builder()
                .addButton(EPIC_BATTLE_NAME, Color.RED, EPIC_BATTLE_INIT)
                .newRow()
                .addButton(ROCK_PAPER_SCISSORS_NAME, Color.BLUE, ROCK_PAPER_SCISSORS_INIT)
                .build();
        String message = "Выберите игру";
        sendKeyboardMessage(chatId, message, keyboard);
    }

}
