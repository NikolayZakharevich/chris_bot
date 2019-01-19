package com.nikolayzakharevich;

import com.nikolayzakharevich.games.Game;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DialogHandler extends BotRequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BotRequestHandler.class);

    DialogHandler(VkApiClient apiClient, UserActor actor) {
        super(apiClient, actor);
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

    void tryKeyboard(int userId) {
        try {
            String keyboard = Keyboard.builder()
                    .addLabel(new Pair<>("ПУ", Color.WHITE))
                    .newRow()
                    .addLabel(new Pair<>("ТИ", Color.BLUE))
                    .newRow()
                    .addLabel(new Pair<>("Н", Color.RED))
                    .build();
            System.out.println(keyboard);
            apiClient.messages()
                    .send(actor)
                    .userId(userId)
                    .message("Aye")
                    .unsafeParam("keyboard", keyboard)
                    .execute();
        } catch (ApiException e) {
            LOG.error("Wrong user_id");
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    List<Game> getGameList() {
        return null;
    }

}

