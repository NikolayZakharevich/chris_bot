package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.games.service.EpicBattlePlayer.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

import java.util.List;
import java.util.Map;

class EpicBattle extends Game<EpicBattlePlayer> {

    private EpicBattlePlayer otherPlayer = new EpicBattlePlayer(0);

    EpicBattle(int chatId) {
        super(EPIC_BATTLE_NAME, chatId);
    }

    @Override
    void init(int initiatorId, int... playerIds) {
        EpicBattlePlayer player = new EpicBattlePlayer(initiatorId);
        players.add(player);
        currentPlayer = player;

        List<Integer> chatMembers = getChatMembersIds(chatId);
        Keyboard.Builder builder = Keyboard.builder();
        for (int member : chatMembers) {
            if (member != initiatorId) {
                builder.addButton(getFirstName(member) + " " +
                        getLastName(member), Color.WHITE, EPIC_BATTLE_ACT1_CHALLENGE)
                        .newRow();
            }
        }
        keyboard = builder.setOneTime(true).build();
        message = getFirstName(initiatorId) + ", выберите соперника!";
    }

    @Override
    void processMessage(String text, String payload) {

        switch (payload) {
            case EPIC_BATTLE_ACT1_CHALLENGE:
                acceptChallenge(text);
                break;
            case EPIC_BATTLE_ACT2_CONFIRM:
                if (checkConfirmation(text)) {
                    getRandomCurrentPlayer();
                    heroPick(EPIC_BATTLE_ACT3_FIRST_HERO_PICK);
                }
                break;
            case EPIC_BATTLE_ACT3_FIRST_HERO_PICK:
                setHero(currentPlayer, text);

                EpicBattlePlayer temp = currentPlayer;
                currentPlayer = otherPlayer;
                otherPlayer = temp;
                heroPick(EPIC_BATTLE_ACT4_SECOND_HERO_PICK);
                break;
            case EPIC_BATTLE_ACT4_SECOND_HERO_PICK:
                setHero(currentPlayer, text);

                temp = currentPlayer;
                currentPlayer = otherPlayer;
                otherPlayer = temp;

                int manaPoints = RANDOM.nextInt(6) + 1;
                currentPlayer.hero.addMana(manaPoints);

                message = getScoreMessage();
                message += getFirstName(currentPlayer.vkId) + " бросает кубик. Выпало " + manaPoints + "\n" +
                        "У вас " + currentPlayer.hero.mana + ",&#128167; выберите заклинание";

                checkSkills(EPIC_BATTLE_ACT4_SECOND_HERO_PICK);
                break;
            case EPIC_BATTLE_ACT_NEXT:
                if (text.startsWith(EPIC_BATTLE_TAP_SKILL)) {
                    currentPlayer.hero.useTap(otherPlayer.hero);
                }


                if (checkEnd()) {
                    return;
                }

                temp = currentPlayer;
                currentPlayer = otherPlayer;
                otherPlayer = temp;

                manaPoints = RANDOM.nextInt(6) + 1;
                currentPlayer.hero.addMana(manaPoints);

                message = getScoreMessage();
                message += getFirstName(currentPlayer.vkId) + " бросает кубик. Выпало " + manaPoints + "\n" +
                        "У вас " + currentPlayer.hero.mana + " " + EPIC_BATTLE_MANA_ICON + ", выберите заклинание";
                checkSkills(EPIC_BATTLE_ACT_NEXT);
                break;
            case EPIC_BATTLE_ACT_CONTINUE:
                currentPlayer.hero.useSkill(text.substring(0, text.indexOf(" (")), otherPlayer.hero);
                if (checkEnd()) {
                    return;
                }
                message = getScoreMessage();
                message += getFirstName(currentPlayer.vkId) + ", у вас осталось " + currentPlayer.hero.mana + " маны\n";
                checkSkills(EPIC_BATTLE_ACT_CONTINUE);
        }


    }

    private void checkSkills(String payload) {

        Keyboard.Builder builder = Keyboard.builder();
        boolean noAvailable = true;
        for (Map.Entry<String, ? extends Skill<? extends Hero>> entry : currentPlayer.hero.skills.entrySet()) {
            Skill<? extends Hero> skill = entry.getValue();
            if (skill.manaCost <= currentPlayer.hero.mana) {
                noAvailable = false;
                builder.addButton(skill.name + " (" + skill.manaCost + EPIC_BATTLE_MANA_ICON + ")",
                        Color.BLUE, EPIC_BATTLE_ACT_CONTINUE);
                builder.newRow();
            }
        }
        if (noAvailable && !payload.equals(EPIC_BATTLE_ACT_CONTINUE)) {
            builder.addButton(EPIC_BATTLE_TAP_SKILL + " (0" + EPIC_BATTLE_MANA_ICON + ")",
                    Color.BLUE, EPIC_BATTLE_ACT_NEXT);
        } else {
            builder.addButton(EPIC_BATTLE_SKIP, Color.WHITE, EPIC_BATTLE_ACT_NEXT);
        }
        keyboard = builder.build();
    }

    private String getScoreMessage() {
        if (currentPlayer.vkId < otherPlayer.vkId) {
            return getFirstName(currentPlayer.vkId) + " (" + currentPlayer.hero.icon + ") " + EPIC_BATTLE_HP_ICON +
                    currentPlayer.hero.hp + " - " + EPIC_BATTLE_HP_ICON + otherPlayer.hero.hp + " " +
                    getFirstName(otherPlayer.vkId) + " (" + otherPlayer.hero.icon + ") " + "\n";
        }
        return getFirstName(otherPlayer.vkId) + " (" + otherPlayer.hero.icon + ") " + EPIC_BATTLE_HP_ICON +
                otherPlayer.hero.hp + " - " + EPIC_BATTLE_HP_ICON + currentPlayer.hero.hp + " " +
                getFirstName(currentPlayer.vkId) + " (" + currentPlayer.hero.icon + ") " + "\n";
    }

    private void getRandomCurrentPlayer() {
        int seed = RANDOM.nextInt(2);
        if (seed == 0) {
            currentPlayer = players.get(0);
            otherPlayer = players.get(1);
        } else {
            currentPlayer = players.get(1);
            otherPlayer = players.get(0);
        }
    }

    private void acceptChallenge(String text) {
        List<Integer> chatMembers = getChatMembersIds(chatId);
        int secondPlayerId = chatMembers.stream()
                .filter(x -> (getFirstName(x) + " " + getLastName(x)).equalsIgnoreCase(text))
                .findFirst()
                .get();
        players.add(new EpicBattlePlayer(secondPlayerId));
        currentPlayer = players.get(1);
        keyboard = Keyboard.builder().addButton(EPIC_BATTLE_ACCEPT, Color.GREEN, EPIC_BATTLE_ACT2_CONFIRM)
                .newRow()
                .addButton(EPIC_BATTLE_REJECT, Color.RED, EPIC_BATTLE_ACT2_CONFIRM)
                .setOneTime(true)
                .build();
        message = getFirstName(secondPlayerId) + ", принимаешь вызов?";
    }

    private boolean checkConfirmation(String text) {
        if (text.equalsIgnoreCase("го")) {
            return true;
        } else {
            isEnded = true;
            message = getFirstName(currentPlayer.vkId) + " ссыкло(";
            keyboard = Keyboard.builder().build();
            return false;
        }
    }

    private void heroPick(String newPayload) {

        message = "@id" + currentPlayer.vkId + "(" + getFirstName(currentPlayer.vkId) + "), ваш ход!\n" +
                "Выберите героя!\n" +
                EPIC_BATTLE_PALADIN + EPIC_BATTLE_PALADIN_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (4) " + EPIC_BATTLE_HUMMER_PUNCH_SKILL + ": 3&#128481;\n" +
                "&#128167; (6) " + EPIC_BATTLE_HOLY_LIGHT_SKILL + ": +5&#10084;\n\n" +
                EPIC_BATTLE_NECROMANCER + EPIC_BATTLE_NECROMANCER_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_SOUL_STEALING_SKILL + ": 2&#128481;, + 1&#128420;(Нет максимума)\n" +
                "&#128167; (6) " + EPIC_BATTLE_ASTRAL_EXPLOSION_SKILL + ": (2+&#128420;)&#128481;\n\n" +
                EPIC_BATTLE_HUNTRESS + EPIC_BATTLE_HUNTRESS_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_MAGIC_ARROW_SKILL + ":+1&#128481; к атаке на ход\n" +
                "&#128167; (6) " + EPIC_BATTLE_SALVO_SKILL + ": атака 1-2-3 раза\n\n" +
                EPIC_BATTLE_TIGER + EPIC_BATTLE_TIGER_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_FURIOUS_STRIKE_SKILL + ": 2&#128481; &#10084;-2 если &#10084;>6, &#10084;+2, " +
                "если &#10084;<=6\n" +
                "&#128167; (6) " + EPIC_BATTLE_BEAST_SWIPE_SKILL + ": 50%&#128481; от &#10084; каждому герою\n\n";

        keyboard = Keyboard.builder()
                .addButton(EPIC_BATTLE_PALADIN, Color.WHITE, newPayload)
                .newRow()
                .addButton(EPIC_BATTLE_NECROMANCER, Color.BLUE, newPayload)
                .newRow()
                .addButton(EPIC_BATTLE_HUNTRESS, Color.GREEN, newPayload)
                .newRow()
                .addButton(EPIC_BATTLE_TIGER, Color.RED, newPayload)
                .setOneTime(true)
                .build();
    }

    private boolean checkEnd() {
        if (!currentPlayer.hero.isDead() && !otherPlayer.hero.isDead()) {
            return false;
        }
        keyboard = Keyboard.builder().build();
        message = getScoreMessage();
        if (currentPlayer.hero.isDead()) {
            message += getFirstName(otherPlayer.vkId) + " " + getLastName(otherPlayer.vkId) + " - победитель";
        } else if (otherPlayer.hero.isDead()) {
            message += getFirstName(currentPlayer.vkId) + " " + getLastName(currentPlayer.vkId) + " - победитель";
        } else {
            message += "Победителей нет!";
        }
        isEnded = true;
        return true;
    }

    private void setHero(EpicBattlePlayer player, String heroName) {
        switch (heroName) {
            case EPIC_BATTLE_PALADIN:
                player.hero = new Paladin();
                break;
            case EPIC_BATTLE_NECROMANCER:
                player.hero = new Necromancer();
                break;
            case EPIC_BATTLE_HUNTRESS:
                player.hero = new Huntress();
                break;
            case EPIC_BATTLE_TIGER:
                player.hero = new Tiger();
                break;
        }
    }

}
