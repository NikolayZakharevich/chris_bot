package com.nikolayzakharevich;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

import com.nikolayzakharevich.games.client.GameClient;
import com.nikolayzakharevich.games.client.BasicGameClient;

import com.nikolayzakharevich.games.client.GameStoppingClient;
import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class ChatHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);
    private final static long VOTE_TIME = 60000;

    private GameClient gameClient = new BasicGameClient();
    private GameClient voteClient = new GameStoppingClient();
    private Timer timer;

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
        boolean type1 = text.contains("[club156440140|@randmagic] ");
        boolean type2 = text.contains("[club156440140|Крис Бот] ");
        if (type1) {
            text = text.substring("[club156440140|@randmagic] ".length());
        } else if (type2) {
            text = text.substring("[club156440140|Крис Бот] ".length());
        }

        LOG.info("message = " + text);

        if (payload.equalsIgnoreCase(VOTE_STOP_GAME)) {
            LOG.info("A vote for stopping current game in chat " + chatId);
            voteClient.process(chatId, text, userId, payload);
            sendKeyboardMessage(chatId, voteClient.getMessage(chatId), voteClient.getKeyboard(chatId));
            return;
        }

        switch (payload) {
            case EPIC_BATTLE_INIT:
                LOG.info("Initialization of EpicBattle in chat " + chatId);
                gameClient.init(chatId, text, userId);
                break;
            case EPIC_BATTLE_ACT1_CHALLENGE:
            case EPIC_BATTLE_ACT2_CONFIRM:
            case EPIC_BATTLE_ACT3_FIRST_HERO_PICK:
            case EPIC_BATTLE_ACT4_SECOND_HERO_PICK:
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
        }
        sendKeyboardMessage(chatId, gameClient.getMessage(chatId), gameClient.getKeyboard(chatId));
    }

    @Override
    void processMessage(int userId, int chatId, String text) {
        if (text.equalsIgnoreCase("список игр")) {
            getGameList(chatId);
        } else if (text.equalsIgnoreCase("повтори")) {
            sendKeyboardMessage(chatId, gameClient.getMessage(chatId), gameClient.getKeyboard(chatId));
        } else if (text.equalsIgnoreCase("повтори голосование")) {
            sendKeyboardMessage(chatId, voteClient.getMessage(chatId), voteClient.getKeyboard(chatId));
        } else if (text.equalsIgnoreCase("стоп игра")) {
            voteClient.init(chatId, text, userId);
            sendKeyboardMessage(chatId, voteClient.getMessage(chatId), voteClient.getKeyboard(chatId));
            if (!voteClient.getMessage(chatId).equals(VOTE_ALREADY_RUNNING)) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        voteClient.process(chatId, text, userId, VOTE_END_STOP_GAME);
                        sendKeyboardMessage(chatId, voteClient.getMessage(chatId),
                                voteClient.getKeyboard(chatId));
                    }
                }, VOTE_TIME);
            }
        } else if (text.equalsIgnoreCase("команды")) {
            String message = "\"список игр\" чтобы посмотреть игры\n" +
                    "\"повтори\" если потерялось сообщение или клавиатура\n" +
                    "\"повтори голосование\" если потерялось сообщение или клавиатура голосования\n" +
                    "\"стоп игра\" чтобы остановить текущую игру" +
                    "\"команды\" чтобы посмотреть команды";
            sendMessage(chatId, message);
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
