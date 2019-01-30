package com.nikolayzakharevich.games.service;

public interface GameService {

    void init(String gameName, int userId);

    void process(String text, int userId, String payload);

    String getKeyboard();

    String getMessage();

    String getVoteMessage();

    String getVoteKeyboard();

    void startVoting(int userId);

    void processVote(String text, int userId);

    void checkVotingResults();
}
