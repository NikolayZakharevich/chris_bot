package com.nikolayzakharevich.stuff;

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
}