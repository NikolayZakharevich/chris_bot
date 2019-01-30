package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.exeptions.InvalidSkillUsageException;

import static com.nikolayzakharevich.games.GameConstants.*;

class EpicBattlePlayer extends Player<EpicBattle> {

    int MAX_HP;

    int hp;
    int mana;
    int souls;
    int arrows;
    int fireOrbs;
    int iceOrbs;
    Hero hero;

    static class Hero {
        String name;

    }


    abstract static class Skill {

        final String name;
        int manaCost;

        Skill(String name, int manaCost) {
            this.name = name;
            this.manaCost = manaCost;
        }

        void use(EpicBattlePlayer user, EpicBattlePlayer target) {
            if (user.mana < manaCost) {
                throw new InvalidSkillUsageException("Skill " + name  +", player has " + user.mana +
                        " mana, manacost is " + manaCost);
            }
            user.mana -= manaCost;
        }

        abstract void effect(EpicBattlePlayer user, EpicBattlePlayer target);
    }

    static class HammerPunch extends Skill {

        HammerPunch(String name) {
            super(EPIC_BATTLE_HUMMER_PUNCH_SKILL, 4);
        }

        @Override
        void effect(EpicBattlePlayer user, EpicBattlePlayer target) {
            target.hp -= 3;
        }
    }

    static class HolyLight extends Skill {

        HolyLight(String name) {
            super(EPIC_BATTLE_HUMMER_PUNCH_SKILL, 6);
        }

        @Override
        void effect(EpicBattlePlayer user, EpicBattlePlayer target) {
            user.hp = Math.max(user.MAX_HP, user.hp + 5);
        }
    }
    EpicBattlePlayer(int vkId) {
        super(vkId);
    }
}
