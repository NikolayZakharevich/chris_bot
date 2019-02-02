package com.nikolayzakharevich.games.service;

import com.nikolayzakharevich.exeptions.InvalidSkillUsageException;

import java.util.HashMap;
import java.util.Map;

import static com.nikolayzakharevich.games.GameConstants.*;

class EpicBattlePlayer extends Player {

    Hero<? extends Hero> hero;

    abstract static class Hero<HeroType extends Hero> {

        final int MAX_HP;
        final int MAX_MANA = 6;
        final String name;
        final String icon;

        int hp;
        int mana;

        boolean repeatTurn;

        Map<String, Skill<HeroType>> skills = new HashMap<>();
        Skill<Hero> tap = new Tap();

        Hero(String name, String icon, int maxHp) {
            this.name = name;
            this.icon = icon;
            MAX_HP = maxHp;
            hp = maxHp;
        }

        String attributesInfo() {
            return mana + EPIC_BATTLE_MANA_ICON;
        }

        void addMana(int points) {
            mana = Math.min(MAX_MANA, mana + points);
        }

        void heal(int points) {
            hp = Math.min(MAX_HP, hp + points);
        }

        void doDamage(int damage) {
            hp = Math.max(0, hp - damage);
        }

        void useTap(Hero target) {
            tap.use(this, target);
        }

        abstract void useSkill(String skill, Hero target);

        void startOfTurnEffect(Hero enemy) {
            repeatTurn = false;
        }

        void endOfTurnEffect(Hero enemy) {}

        boolean isDead() {
            return hp <= 0;
        }
    }

    abstract static class Skill<HeroType extends Hero> {

        final String name;
        int manaCost;

        Skill(String name, int manaCost) {
            this.name = name;
            this.manaCost = manaCost;
        }

        void use(HeroType user, Hero target) {
            if (user.mana < manaCost) {
                throw new InvalidSkillUsageException("Skill " + name + ", player has " + user.mana +
                        " mana, manacost is " + manaCost);
            }
            user.mana -= manaCost;
            effect(user, target);
        }

        abstract void effect(HeroType user, Hero target);
    }

    static class Paladin extends Hero<Paladin> {

        Paladin() {
            super(EPIC_BATTLE_PALADIN, EPIC_BATTLE_PALADIN_ICON, 20);
            skills.put(EPIC_BATTLE_HUMMER_PUNCH_SKILL, new HammerPunch());
            skills.put(EPIC_BATTLE_HOLY_LIGHT_SKILL, new HolyLight());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }

    }

    static class Necromancer extends Hero<Necromancer> {

        int souls;

        Necromancer() {
            super(EPIC_BATTLE_NECROMANCER, EPIC_BATTLE_NECROMANCER_ICON, 20);
            skills.put(EPIC_BATTLE_SOUL_STEALING_SKILL, new SoulStealing());
            skills.put(EPIC_BATTLE_ASTRAL_EXPLOSION_SKILL, new AstralExplosion());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }

        @Override
        String attributesInfo() {
            return super.attributesInfo() + ", " + souls + EPIC_BATTLE_SOUL_ICON;
        }
    }

    static class Huntress extends Hero<Huntress> {

        int bonus;
        boolean magicArrowUsed;
        boolean salvoUsed;

        Huntress() {
            super(EPIC_BATTLE_HUNTRESS, EPIC_BATTLE_HUNTRESS_ICON, 20);
            skills.put(EPIC_BATTLE_MAGIC_ARROW_SKILL, new MagicArrow());
            skills.put(EPIC_BATTLE_SALVO_SKILL, new Salvo());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }

        @Override
        String attributesInfo() {
            return super.attributesInfo() + ", " + (bonus + 1) + EPIC_BATTLE_ARROW_ICON;
        }

        @Override
        void endOfTurnEffect(Hero enemy) {
            if (magicArrowUsed && !salvoUsed) {
                enemy.doDamage(1 + bonus);
            }
            magicArrowUsed = false;
            salvoUsed = false;
            bonus = 0;
        }
    }

    static class Tiger extends Hero<Tiger> {

        Tiger() {
            super(EPIC_BATTLE_TIGER, EPIC_BATTLE_TIGER_ICON, 20);
            skills.put(EPIC_BATTLE_FURIOUS_STRIKE_SKILL, new FuriousStrike());
            skills.put(EPIC_BATTLE_BEAST_SWIPE_SKILL, new BeastSwipe());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }
    }

    static class Mage extends Hero<Mage> {

        private int ice;
        private int fire;

        Mage() {
            super(EPIC_BATTLE_MAGE, EPIC_BATTLE_MAGE_ICON, 20);
            skills.put(EPIC_BATTLE_FIRE_ORB_SKILL, new FireOrb());
            skills.put(EPIC_BATTLE_ICE_ORB_SKILL, new IceOrb());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }

        @Override
        String attributesInfo() {
            return super.attributesInfo() + ", " + fire + EPIC_BATTLE_FIRE_ICON + ", " + ice + EPIC_BATTLE_ICE_ICON;
        }

        @Override
        void endOfTurnEffect(Hero enemy) {
            if (fire == 1 && ice == 0) {
                enemy.doDamage(Game.RANDOM.nextInt(2) + 1);
            } else if (fire == 0 && ice == 1) {
                heal(Game.RANDOM.nextInt(2) + 2);
            } else if (fire == 2 && ice == 0) {
                enemy.doDamage(Game.RANDOM.nextInt(2) + 2);
            } else if (fire == 1 && ice == 1) {
                enemy.doDamage(1);
                repeatTurn = true;
            } else if (fire == 3 && ice == 0) {
                enemy.doDamage(Game.RANDOM.nextInt(3) + 3);
            } else if (fire == 0 && ice == 2) {
                heal(Game.RANDOM.nextInt(2) + 4);
            }
            ice = 0;
            fire = 0;
        }
    }

    static class Demon extends Hero<Demon> {

        Demon() {
            super(EPIC_BATTLE_DEMON, EPIC_BATTLE_DEMON_ICON, 20);
            skills.put(EPIC_BATTLE_FUNERAL_FLOWERS_SKILL, new FuneralFlowers());
            skills.put(EPIC_BATTLE_FATE_TRAP_SKILL, new FateTrap());
        }

        @Override
        void useSkill(String skill, Hero target) {
            skills.get(skill).use(this, target);
        }
    }


    static class FuneralFlowers extends Skill<Demon> {

        FuneralFlowers() {
            super(EPIC_BATTLE_FUNERAL_FLOWERS_SKILL, 2);
        }

        @Override
        void effect(Demon user, Hero target) {
            if (target.hp % 2 == 0) {
                target.doDamage(3);
            } else {
                target.doDamage(1);
            }
        }
    }

    static class FateTrap extends Skill<Demon> {

        FateTrap() {
            super(EPIC_BATTLE_FATE_TRAP_SKILL, 5);
        }

        @Override
        void effect(Demon user, Hero target) {
            if (user.hp % 2 == 0) {
                target.doDamage(4);
            } else {
                user.heal(4);
            }
        }
    }


    static class FireOrb extends Skill<Mage> {

        FireOrb() {
            super(EPIC_BATTLE_FIRE_ORB_SKILL, 2);
        }

        @Override
        void effect(Mage user, Hero target) {
            user.fire++;
        }
    }

    static class IceOrb extends Skill<Mage> {

        IceOrb() {
            super(EPIC_BATTLE_ICE_ORB_SKILL, 3);
        }

        @Override
        void effect(Mage user, Hero target) {
            user.ice++;
        }
    }

    static class FuriousStrike extends Skill<Tiger> {

        FuriousStrike() {
            super(EPIC_BATTLE_FURIOUS_STRIKE_SKILL, 3);
        }

        @Override
        void effect(Tiger user, Hero target) {
            target.doDamage(2);
            if (user.hp <= 6) {
                user.heal(2);
            } else {
                user.doDamage(2);
            }
        }
    }

    static class BeastSwipe extends Skill<Tiger> {

        BeastSwipe() {
            super(EPIC_BATTLE_BEAST_SWIPE_SKILL, 5);
        }

        @Override
        void effect(Tiger user, Hero target) {
            user.doDamage(user.hp / 2);
            target.doDamage(target.hp / 2);
        }
    }

    static class MagicArrow extends Skill<Huntress> {

        MagicArrow() {
            super(EPIC_BATTLE_MAGIC_ARROW_SKILL, 2);
        }

        @Override
        void effect(Huntress user, Hero target) {
            user.magicArrowUsed = true;
            user.bonus++;
        }
    }

    static class Salvo extends Skill<Huntress> {

        Salvo() {
            super(EPIC_BATTLE_SALVO_SKILL, 4);
        }

        @Override
        void effect(Huntress user, Hero target) {
            user.salvoUsed = true;
            target.doDamage((Game.RANDOM.nextInt(3) + 1) * (user.bonus + 1));
            user.bonus = 0;
        }
    }

    static class Tap extends Skill<Hero> {

        Tap() {
            super(EPIC_BATTLE_TAP_SKILL, 0);
        }

        @Override
        void effect(Hero user, Hero target) {
            target.doDamage(1);
        }
    }

    static class HammerPunch extends Skill<Paladin> {

        HammerPunch() {
            super(EPIC_BATTLE_HUMMER_PUNCH_SKILL, 4);
        }

        @Override
        void effect(Paladin user, Hero target) {
            target.doDamage(3);
        }
    }

    static class HolyLight extends Skill<Paladin> {

        HolyLight() {
            super(EPIC_BATTLE_HOLY_LIGHT_SKILL, 6);
        }

        @Override
        void effect(Paladin user, Hero target) {
            user.heal(5);
        }
    }

    static class SoulStealing extends Skill<Necromancer> {

        SoulStealing() {
            super(EPIC_BATTLE_SOUL_STEALING_SKILL, 3);
        }

        @Override
        void effect(Necromancer user, Hero target) {
            user.souls++;
            target.doDamage(2);
        }
    }

    static class AstralExplosion extends Skill<Necromancer> {

        AstralExplosion() {
            super(EPIC_BATTLE_ASTRAL_EXPLOSION_SKILL, 6);
        }

        @Override
        void effect(Necromancer user, Hero target) {
            target.doDamage(user.souls + 2);
            user.souls = 0;
        }
    }

    EpicBattlePlayer(int vkId) {
        super(vkId);
    }
}
