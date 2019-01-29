package com.nikolayzakharevich.games.dao;

import com.google.gson.JsonParseException;
import com.nikolayzakharevich.games.service.Game;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao implements Dao {

    private String currentGameTypeKey = "current_game_type";
    private String currentGameKey = "current_game";
    private String closedGamesKey = "closed_games";
    private String messageKey = "message";
    private String keyboardKey = "keyboard";
    private JedisPool pool;
    private int chatId;

    RedisDao(String hostName, int port) {
        pool = new JedisPool(hostName, port);
    }

    RedisDao(String hostName, int port, int chatId) {
        this.chatId = chatId;
        currentGameTypeKey += chatId;
        currentGameKey += chatId;
        closedGamesKey += chatId;
        messageKey += chatId;
        keyboardKey += chatId;
        pool = new JedisPool(hostName, port);
    }

    @Override
    public String getCurrentGameType() {
        return get(currentGameTypeKey);
    }

    @Override
    public String getMessage() {
       return get(messageKey);
    }

    @Override
    public String getKeyboard() {
        return get(keyboardKey);
    }

    @Override
    public void saveMessage(String message) {
        save(messageKey, message);
    }

    @Override
    public void saveKeyboard(String keyboard) {
        save(keyboardKey, keyboard);
    }

    @Override
    public <T extends Game> T getCurrentGame(Class<T> clazz) {
        T game = null;
        try (Jedis jedis = pool.getResource()) {
            game = Game.fromJson(jedis.get(currentGameKey), clazz);
            return game;
        } catch (JsonParseException e) {
            // ignore
        }
        return game;
    }


    @Override
    public void saveGame(Game game) {
        save(currentGameTypeKey, game.getName());
        save(currentGameKey, game.toJson());
    }

    @Override
    public void closeGame(Game game) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(currentGameTypeKey);
            jedis.del(currentGameKey);
            jedis.sadd(closedGamesKey, game.toJson());
        } catch (JsonParseException e) {
            // ignore
        }
    }

    private String get(String key) {
        Jedis jedis = pool.getResource();
        String result = jedis.get(key);
        jedis.close();
        return result;
    }

    private void save(String key, String object) {
        Jedis jedis = pool.getResource();
        jedis.set(key, object);
        jedis.close();
    }
}

