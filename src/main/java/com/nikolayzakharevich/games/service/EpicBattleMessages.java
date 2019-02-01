package com.nikolayzakharevich.games.service;

import static com.nikolayzakharevich.games.GameConstants.*;
import static com.nikolayzakharevich.games.GameConstants.EPIC_BATTLE_HP_ICON;
import static com.nikolayzakharevich.vkapi.VkApiUsage.getFirstName;
import static com.nikolayzakharevich.vkapi.VkApiUsage.getLastName;

public class EpicBattleMessages {
    static String manaInfo(EpicBattlePlayer player) {
        return getFirstName(player.vkId) + ", у вас " + player.hero.attributesInfo();
    }

    static String rollInfo(int roll) {
        return  "Бросаем кубик.. Выпало " + roll + "\n";
    }

    static String challenge(int id) {
        return "@id" + id + "(" + getFirstName(id) + "), принимаешь вызов?";
    }

    static String challengeReject(int id) {
        return getFirstName(id) + " ссыкло(";
    }

    static String heroList(EpicBattlePlayer player) {
        return  EPIC_BATTLE_NECT_ACT_ICON + getFirstName(player.vkId) + ", ваш ход!\n" +
                "Выберите героя!\n" +
                EPIC_BATTLE_PALADIN + EPIC_BATTLE_PALADIN_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (4) " + EPIC_BATTLE_HUMMER_PUNCH_SKILL + ": 3&#128481;\n" +
                "&#128167; (6) " + EPIC_BATTLE_HOLY_LIGHT_SKILL + ": +5&#10084;\n\n" +
                EPIC_BATTLE_NECROMANCER + EPIC_BATTLE_NECROMANCER_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_SOUL_STEALING_SKILL +
                ": 2&#128481;, + 1 " + EPIC_BATTLE_SOUL_ICON + "(Нет максимума)\n" + "&#128167; (6) " +
                EPIC_BATTLE_ASTRAL_EXPLOSION_SKILL + ": (2+" + EPIC_BATTLE_SOUL_ICON + ")&#128481;\n\n" +
                EPIC_BATTLE_HUNTRESS + EPIC_BATTLE_HUNTRESS_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_MAGIC_ARROW_SKILL + ":+1&#128481; к атаке на ход\n" +
                "&#128167; (6) " + EPIC_BATTLE_SALVO_SKILL + ": атака 1-2-3 раза\n\n" +
                EPIC_BATTLE_TIGER + EPIC_BATTLE_TIGER_ICON + "\n" +
                "&#10084;20\n" +
                "&#128167; (3) " + EPIC_BATTLE_FURIOUS_STRIKE_SKILL + ": 2&#128481; " + EPIC_BATTLE_HP_ICON +
                "-2 если &#10084;>6, " + EPIC_BATTLE_HP_ICON + " +2, если &#10084;<=6\n" +
                "&#128167; (6) " + EPIC_BATTLE_BEAST_SWIPE_SKILL + ": 50%&#128481; от &#10084; каждому герою\n\n";
    }

    static String gameResults(EpicBattlePlayer first, EpicBattlePlayer second) {
        if (first.hero.isDead()) {
            return getFirstName(second.vkId) + " " + getLastName(second.vkId) + " - победитель";
        } else if (second.hero.isDead()) {
            return getFirstName(first.vkId) + " " + getLastName(first.vkId) + " - победитель";
        } else {
            return "Победителей нет!";
        }
    }

    static String scoreMessage(EpicBattlePlayer first, EpicBattlePlayer second) {
        if (first.vkId < second.vkId) {
            return getFirstName(first.vkId) + " (" + first.hero.icon + ") " + EPIC_BATTLE_HP_ICON +
                    first.hero.hp + " - " + EPIC_BATTLE_HP_ICON + second.hero.hp + " " +
                    getFirstName(second.vkId) + " (" + second.hero.icon + ") ";
        }
        return getFirstName(second.vkId) + " (" + second.hero.icon + ") " + EPIC_BATTLE_HP_ICON +
                second.hero.hp + " - " + EPIC_BATTLE_HP_ICON + first.hero.hp + " " +
                getFirstName(first.vkId) + " (" + first.hero.icon + ") ";
    }
}
