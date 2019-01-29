package com.nikolayzakharevich.games.dao;

import static com.nikolayzakharevich.games.GameConstants.*;

import com.nikolayzakharevich.exeptions.DriverRegistrationFailedException;
import com.nikolayzakharevich.games.service.Game;

import java.sql.*;
import java.util.List;

public class MsSqlDao {

    private String serverName;
    private String databaseName;
    private String userName;
    private String password;
    private Connection connection;

    MsSqlDao(String serverName, String databaseName, String userName, String password) {
        this.serverName = serverName;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        String connectionString = "jdbc:sqlserver://" + this.serverName + ";databaseName=" + this.databaseName;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionString,
                    this.userName, this.password);
        } catch (SQLException e) {
            // ignore
        } catch (ClassNotFoundException e) {
            throw new DriverRegistrationFailedException();
        }
    }

    private void createTableForPlayers(String gameName) {
        String query;
        switch (gameName) {
            case EPIC_BATTLE_NAME:
                break;
            case ROCK_PAPER_SCISSORS_NAME:
                // TODO: 29.01.19  
                break;
            default:

        }
    }

    private ResultSet executeQuery(String queryString) {
        ResultSet resultSet = null;
        Statement statement;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryString);
        } catch (SQLException e) {
            // ignore
        }
        return resultSet;
    }

}
