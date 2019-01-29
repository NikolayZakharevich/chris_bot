package com.nikolayzakharevich.games.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nikolayzakharevich.stuff.AbstractGsonAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Game {

    protected final String name;
    protected List<Player<?>> players = new ArrayList<>();
    protected List<Player<?>> currentPlayers = new ArrayList<>();
    protected String keyboard;
    protected String message;
    protected boolean isClosed;
    private Date currentTime;

    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Player.class,
            new AbstractGsonAdapter<Player>()).create();

    Game(String name) {
        this.name = name;
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

    List<Player<?>> getCurrentPlayers() {
        return currentPlayers;
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
