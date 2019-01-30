package com.nikolayzakharevich.games.client;

import static com.nikolayzakharevich.games.GameConstants.*;

import com.nikolayzakharevich.games.service.GameService;

public class GameStoppingClient extends GameClient {

    @Override
    public void init(int chatId, String gameName, int userId) {
        GameService service = getChatService(chatId);
        service.startVoting(userId);
    }

    @Override
    public void process(int chatId, String text, int userId, String payload) {
        GameService service = getChatService(chatId);
        if (payload.equalsIgnoreCase(VOTE_STOP_GAME)) {
            service.processVote(text, userId);
        } else if (payload.equalsIgnoreCase(VOTE_END_STOP_GAME)) {
            service.checkVotingResults();
        }
    }

    @Override
    public String getKeyboard(int chatId) {
        GameService service = getChatService(chatId);
        return service.getVoteKeyboard();
    }

    @Override
    public String getMessage(int chatId) {
        GameService service = getChatService(chatId);
        return service.getVoteMessage();
    }

}
