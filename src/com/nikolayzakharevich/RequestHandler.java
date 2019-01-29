package com.nikolayzakharevich;

import com.google.gson.*;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

public class RequestHandler extends AbstractHandler {

    private final static String CONFIRMATION_TYPE = "confirmation";
    private final static String MESSAGE_TYPE = "message_new";
    private final static String OK_BODY = "ok";
    private final static String CONFIRMATION_CODE = "4bd01262";
    private final static int CHAT_ID_SHIFT = 2000000000;
    private final static long MAX_MESSAGE_DELAY_MILLIS = 3000;


    private final DialogHandler dialogHandler;
    private final ChatHandler chatHandler;
    private final Gson gson;

    RequestHandler(VkApiClient client, GroupActor actor) {
        dialogHandler = new DialogHandler(client, actor);
        chatHandler = new ChatHandler(client, actor);
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new ServletException("This method is unsupported");
        }

        Reader reader = request.getReader();

        try {
            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);

            String type = requestJson.get("type").getAsString();

            if (type == null || type.isEmpty()) {
                throw new ServletException("No type in json");
            }

            final String responseBody;
            switch (type) {
                case CONFIRMATION_TYPE:
                    responseBody = CONFIRMATION_CODE;
                    break;
                case MESSAGE_TYPE:
                    JsonObject object = requestJson.getAsJsonObject("object");

                    long date = object.getAsJsonPrimitive("date").getAsLong() * 1000;
                    if (new Date().getTime() - date > MAX_MESSAGE_DELAY_MILLIS) {
                        return;
                    }

                    int userId = object.getAsJsonPrimitive("from_id").getAsInt();
                    int chatId = object.getAsJsonPrimitive("peer_id").getAsInt();
                    String text = object.getAsJsonPrimitive("text").getAsString();

                    BotRequestHandler handler;
                    if (chatId < CHAT_ID_SHIFT) {
                        handler = dialogHandler;
                    } else {
                        handler = chatHandler;
                        chatId -= CHAT_ID_SHIFT;
                    }

                    JsonPrimitive payload = object.getAsJsonPrimitive("payload");
                    if (object.getAsJsonPrimitive("payload") != null) {
                        handler.processMessage(userId, chatId, text, payload.getAsString());
                    } else {
                        handler.processMessage(userId, chatId, text);
                    }

                    responseBody = OK_BODY;
                    break;
                default:
                    responseBody = OK_BODY; // in case we get another event
                    break;
            }

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println(responseBody);
        } catch (JsonParseException e) {
            throw new ServletException("Incorrect json", e);
        }

    }
}
