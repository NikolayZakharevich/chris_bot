package com.nikolayzakharevich.games.dao;

public class DaoImplementationChooser {

  //  private final static String serverName = "157.230.232.210";
    private final static String serverName = "localhost";
    private final static int port = 6379;
    private final static String databaseName = "chris_bot";
    private final static String username = "sa";
    private final static String password = "Ujvth_cbvgcjy2000";
//    private static Dao msSqlDao = new MsSqlDao(serverName, databaseName, username, password);
    private static Dao redisDao = new RedisDao(serverName, port);

    public static Dao getDao(int chatId) {
        return new RedisDao(serverName, port, chatId);
    }
}
