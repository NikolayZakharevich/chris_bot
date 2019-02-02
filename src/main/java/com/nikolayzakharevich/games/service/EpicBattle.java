package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.stuff.Color;
import com.nikolayzakharevich.vkapi.Keyboard;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.games.service.EpicBattlePlayer.*;
import static com.nikolayzakharevich.vkapi.VkApiUsage.*;

import java.util.List;
import java.util.Map;

class EpicBattle extends Game<EpicBattlePlayer> {

    private Hero[] heroes = {new Paladin(), new Necromancer(), new Huntress(), new Tiger(), new Mage(), new Demon()};
    private EpicBattlePlayer otherPlayer = new EpicBattlePlayer(0);
    private int step = 0;

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
                if (secondPlayerAccepted(text)) {
                    setRandomCurrentPlayer();
                    heroPick(EPIC_BATTLE_ACT3_FIRST_HERO_PICK);
                }
                break;
            case EPIC_BATTLE_ACT3_FIRST_HERO_PICK:
                setHero(currentPlayer, text);
                swapPlayers();
                heroPick(EPIC_BATTLE_ACT4_SECOND_HERO_PICK);
                break;
            case EPIC_BATTLE_ACT4_SECOND_HERO_PICK:
                setHero(currentPlayer, text);
                nextAct();
                break;
            case EPIC_BATTLE_ACT_NEXT:
                if (text.startsWith(EPIC_BATTLE_TAP_SKILL)) {
                    currentPlayer.hero.useTap(otherPlayer.hero);
                }
                currentPlayer.hero.endOfTurnEffect(otherPlayer.hero);
                if (checkEnd()) {
                    return;
                }
                nextAct();
                break;
            case EPIC_BATTLE_ACT_CONTINUE:
                currentPlayer.hero.useSkill(text.substring(0, text.indexOf(" (")), otherPlayer.hero);
                if (checkEnd()) {
                    return;
                }
                message = EpicBattleMessages.scoreMessage(currentPlayer, otherPlayer) + "\n" +
                        EpicBattleMessages.manaInfo(currentPlayer);
                checkSkills(EPIC_BATTLE_ACT_CONTINUE);
        }


    }

    private void nextAct() {
        message = "";
        if (!currentPlayer.hero.repeatTurn) {
            swapPlayers();
            step++;
            if (step % 2 == 1) {
                int roll = RANDOM.nextInt(6) + 1;
                currentPlayer.hero.startOfTurnEffect(otherPlayer.hero);
                otherPlayer.hero.startOfTurnEffect(currentPlayer.hero);
                currentPlayer.hero.addMana(roll);
                otherPlayer.hero.addMana(roll);
                message = EpicBattleMessages.rollInfo(roll);
            }
        } else {
            int roll = RANDOM.nextInt(6) + 1;
            currentPlayer.hero.startOfTurnEffect(otherPlayer.hero);
            currentPlayer.hero.addMana(roll);
            message = EpicBattleMessages.rollInfo(roll);
        }

        message += EpicBattleMessages.scoreMessage(currentPlayer, otherPlayer) + "\n" +
                EPIC_BATTLE_NECT_ACT_ICON + EpicBattleMessages.manaInfo(currentPlayer);
        checkSkills(EPIC_BATTLE_ACT_NEXT);
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
        keyboard = builder.setOneTime(true).build();
    }

    private void setRandomCurrentPlayer() {
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
        message = EpicBattleMessages.challenge(secondPlayerId);
    }

    private boolean secondPlayerAccepted(String text) {
        if (text.equalsIgnoreCase("го")) {
            return true;
        } else {
            isEnded = true;
            message = EpicBattleMessages.challengeReject(currentPlayer.vkId);
            keyboard = Keyboard.builder().build();
            return false;
        }
    }

    private void heroPick(String newPayload) {

        message = EpicBattleMessages.heroList(currentPlayer);
        Keyboard.Builder builder = Keyboard.builder();
        for (Hero hero : heroes) {
            builder.addButton(hero.name, Color.BLUE, newPayload)
                    .newRow();
        }
        keyboard = builder.setOneTime(true).build();
    }

    private void swapPlayers() {
        EpicBattlePlayer temp = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = temp;
    }

    private boolean checkEnd() {
        if (!currentPlayer.hero.isDead() && !otherPlayer.hero.isDead()) {
            return false;
        }
        keyboard = Keyboard.builder().build();
        message = EpicBattleMessages.scoreMessage(currentPlayer, otherPlayer) + "\n" +
                EpicBattleMessages.gameResults(currentPlayer, otherPlayer);
        isEnded = true;
        return true;
    }

    private void setHero(EpicBattlePlayer player, String heroName) {
        for (Hero hero : heroes) {
            if (heroName.equals(hero.name)) {
                player.hero = hero;
            }
        }
    }

}
