package com.nikolayzakharevich.games.dao;

public class DaoImplementationChooser {

    private final static String serverName = "localhost";
    private final static String databaseName = "chris_bot";
    private final static String username = "sa";
    private final static String password = "Ujvth_cbvgcjy2000";
    private static Dao msSqlDao = new MsSqlDatabase(serverName, databaseName, username, password);

    public static Dao getDao() {
        return msSqlDao;
    }
}
