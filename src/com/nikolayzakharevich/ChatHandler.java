package com.nikolayzakharevich;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.nikolayzakharevich.games.client.GameClient;
import com.nikolayzakharevich.games.client.BasicGameClient;

import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import com.nikolayzakharevich.vkapi.VkApiUsage;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ChatHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);

    private GameClient gameClient = new BasicGameClient();

    ChatHandler(VkApiClient apiClient, GroupActor actor) {
        super(apiClient, actor);
    }

    @Override
    void sayHi(int chatId) {
        List<Integer> chatMembers = getChatMembersIds(chatId);
        int target = chatMembers.get(RANDOM.nextInt(chatMembers.size()));
        String message = "Бросаем кубик... Результат: " + getFirstName(target) + " " +
                getLastName(target) + " - любит члены";
        sendMessage(chatId, message);
        LOG.info("Said hi to user " + target + " in chat " + chatId);
    }

    @Override
    void processMessage(int userId, int chatId, String text, String payload) {

        text = text.substring(text.indexOf(" ") + 1);
        switch (payload) {
            case EPIC_BATTLE_INIT:
                LOG.info("Initialization of EpicBattle in chat " + chatId);
                gameClient.init(chatId, text, userId);
                break;
            case EPIC_BATTLE_ACTION:
                LOG.info("Action of EpicBattle in chat " + chatId);
                gameClient.process(chatId, text, userId, payload);
                break;
            case ROCK_PAPER_SCISSORS_INIT:
                LOG.info("Initialization of RockPaperScissors in chat " + chatId);
                gameClient.init(chatId, text, userId);
                break;
            case ROCK_PAPER_SCISSORS_ACTION:
                LOG.info("Action of RockPaperScissors in chat " + chatId);
                gameClient.process(chatId, text, userId, payload);
                break;
            default:
                // TODO: 20.01.19
        }
        sendKeyboardMessage(chatId, gameClient.getMessage(chatId), gameClient.getKeyboard(chatId));
    }

    @Override
    void processMessage(int userId, int chatId, String text) {
        if (text.matches("([0-9]+\\s?)+")) {
            tryKeyboard(chatId, text);
        } else {
            if (text.equalsIgnoreCase("список игр")) {
                getGameList(chatId);
            }
            if (text.equalsIgnoreCase("повтори")) {
                sendKeyboardMessage(chatId, gameClient.getMessage(chatId), gameClient.getKeyboard(chatId));
            }
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
