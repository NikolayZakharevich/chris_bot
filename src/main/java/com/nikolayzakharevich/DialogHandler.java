package com.nikolayzakharevich;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DialogHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);

    DialogHandler(VkApiClient apiClient, GroupActor actor) {
        super(apiClient, actor);
    }

    @Override
    void processMessage(int userId, int chatId, String text) {

    }

    @Override
    void processMessage(int userId, int chatId, String text, String payload) {

    }

    @Override
    void sayHi(int userId) {
        try {
            UserXtrCounters user = apiClient.users()
                    .get(actor)
                    .userIds(String.valueOf(userId))
                    .execute()
                    .get(0);

            String message = user.getFirstName() + " - вы пидор";
            apiClient.messages().send(actor)
                    .userId(userId)
                    .message(message)
                    .execute();
        } catch (ApiException e) {
            LOG.error("Wrong user_id to say hi");
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    void getGameList(int chatId) {

    }

}

