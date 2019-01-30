package com.nikolayzakharevich.games.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nikolayzakharevich.stuff.AbstractGsonAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class Game {

    protected Random RANDOM = new Random();
    protected final String name;
    protected List<Player<?>> players = new ArrayList<>();
    protected Player<?> currentPlayer;
    protected String keyboard;
    protected String message;
    protected boolean isClosed;
    protected int chatId;
    private Date currentTime;

    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Player.class,
            new AbstractGsonAdapter<Player>()).create();

    Game(String name, int chatId) {
        this.name = name;
        this.chatId = chatId;
    }

    abstract void init(int initiatorId, int... playerIds);

    abstract void processMessage(String text, String payload);

    String getKeyboard() {
        return keyboard;
    }

    String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    List<Player<?>> getPlayers() {
        return players;
    }

    Player<?> getCurrentPlayer() {
        return currentPlayer;
    }

    boolean isClosed() {
        return isClosed;
    }

    public String toJson() {
        currentTime = new Date();
        return gson.toJson(this);
    }

    public static <T extends Game> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
