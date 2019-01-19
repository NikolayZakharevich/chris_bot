package com.nikolayzakharevich;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class RequestHandler extends AbstractHandler {

    private final static String CONFIRMATION_TYPE = "confirmation";
    private final static String MESSAGE_TYPE = "message_new";
    private final static String OK_BODY = "ok";
    private final static String CONFIRMATION_CODE = "4bd01262";
    private final static int CHAT_ID_SHIFT = 2000000000;


    private final DialogHandler dialogHandler;
    private final ChatHandler chatHandler;
    private final Gson gson;

    RequestHandler(VkApiClient client, UserActor actor) {
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
                    int userId = object.getAsJsonPrimitive("from_id").getAsInt();
                    int chatId = object.getAsJsonPrimitive("peer_id").getAsInt() - CHAT_ID_SHIFT;

                    if (chatId < 0) {
//                        dialogHandler.sayHi(userId);
                        dialogHandler.tryKeyboard(userId);
                    } else {
                        chatHandler.sayHi(chatId);
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
