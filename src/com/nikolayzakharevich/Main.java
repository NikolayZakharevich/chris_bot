package com.nikolayzakharevich;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import org.eclipse.jetty.server.Server;

public class Main {

    private static final int APP_ID = 6823497;
    private static final String CLIENT_SECRET = "lmSKXYYm63PpXNzCvV6U";
    private static final String ACCESS_TOKEN = "2587460b3baaefc7fe5639850e2219440217f2f3372ce054dcf4988785523ecdfab1353d39d501b1abffa";
    private static final int port = 8080;

    public static void main(String[] args) throws Exception {
        runServer();
    }

    private static void runServer() throws Exception {

        Server server = new Server(port);
        server.setHandler(new RequestHandler(new VkApiClient(new HttpTransportClient()),
                new GroupActor(APP_ID, ACCESS_TOKEN)));

        server.start();
        server.join();


    }
}
