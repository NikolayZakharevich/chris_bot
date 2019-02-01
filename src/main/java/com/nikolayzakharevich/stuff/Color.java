package com.nikolayzakharevich.stuff;

import java.util.Random;

public enum Color {

    BLUE("primary"),
    WHITE("default"),
    RED("negative"),
    GREEN("positive");

    private final String value;

    Color(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Color random() {
        int seed = new Random().nextInt(4);
        switch (seed) {
            case 0: return BLUE;
            case 1: return WHITE;
            case 2: return RED;
            case 3: return GREEN;
            default: return BLUE;
        }
    }

}