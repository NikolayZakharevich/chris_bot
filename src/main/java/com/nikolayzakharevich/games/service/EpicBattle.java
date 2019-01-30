package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

import java.util.List;

class EpicBattle extends Game {

    EpicBattle(int chatId) {
        super(EPIC_BATTLE_NAME, chatId);
    }

    @Override
    void init(int initiatorId, int... playerIds) {
        Player<EpicBattle> player = new EpicBattlePlayer(initiatorId);
        players.add(player);
        currentPlayer = player;

        List<Integer> chatMembers = getChatMembersIds(chatId);
        Keyboard.Builder builder = Keyboard.builder();
        for (int member : chatMembers) {
            if (member != initiatorId) {
                builder.addButton(getFirstName(member) + " " + getLastName(member), Color.WHITE, EPIC_BATTLE_CHALLENGE)
                        .newRow();
            }
        }
        keyboard = builder.setOneTime(true).build();
        message = getFirstName(initiatorId) + ", выберите соперника!";
    }

    @Override
    void processMessage(String text, String payload) {

        switch (payload) {
            case EPIC_BATTLE_CHALLENGE:
                acceptChallenge(text);
                break;
            case EPIC_BATTLE_CONFIRM:
                if (checkConfirmation(text)) {
                    firstAction(text);
                }
                break;
            case EPIC_BATTLE_HERO_PICK:

            default:

        }
    }

    private void acceptChallenge(String text) {
        List<Integer> chatMembers = getChatMembersIds(chatId);
        int secondPlayerId = chatMembers.stream()
                .filter(x -> (getFirstName(x) + " " + getLastName(x)).equalsIgnoreCase(text))
                .findFirst()
                .get();
        Player<EpicBattle> player = new EpicBattlePlayer(secondPlayerId);
        players.add(player);
        currentPlayer = player;
        keyboard = Keyboard.builder().addButton("Го", Color.GREEN, EPIC_BATTLE_CONFIRM)
                .newRow()
                .addButton("Зассал(", Color.RED, EPIC_BATTLE_CONFIRM)
                .setOneTime(true)
                .build();
        message = getFirstName(secondPlayerId) + ", принимаешь вызов?";
    }

    private boolean checkConfirmation(String text) {
        if (text.equalsIgnoreCase("го")) {
            return true;
        } else {
            isClosed = true;
            message = getFirstName(currentPlayer.vkId) + " ссыкло(";
            keyboard = Keyboard.builder().build();
            return false;
        }
    }

    private void firstAction(String text) {
        currentPlayer = players.get(RANDOM.nextInt(2));

        message = "@id" + currentPlayer.vkId + "(" + getFirstName(currentPlayer.vkId) + "), ваш ход!\n" +
                "Выберите героя!\n" +
                "Паладин&#9728;\n" +
                "&#10084;20\n" +
                "&#128167; (4) Удар молотом: 3&#128481;\n" +
                "&#128167; (6) Помощь света: +5&#10084;\n\n" +
                "Некромант&#128128;\n" +
                "&#10084;20\n" +
                "&#128167; (3) Похищение души: 2&#128481;, + 1&#128420;(Нет максимума)\n" +
                "&#128167; (6) Астральный взрыв: (2+&#128420;)&#128481;\n\n";

        keyboard = Keyboard.builder()
                .addButton(EPIC_BATTLE_PALADIN, Color.WHITE, EPIC_BATTLE_HERO_PICK)
                .addButton(EPIC_BATTLE_NECROMANCER, Color.BLUE, EPIC_BATTLE_HERO_PICK)
                .setOneTime(true)
                .build();
    }

    private void switchPlayer() {
        currentPlayer = players.get((currentPlayer == players.get(0) ? 1 : 0));
    }
}
