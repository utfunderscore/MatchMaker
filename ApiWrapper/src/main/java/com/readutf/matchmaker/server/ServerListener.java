package com.readutf.matchmaker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readutf.matchmaker.shared.server.ServerUpdate;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerListener extends WebSocketClient {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(ServerUpdate.getAdapter())
            .create();

    private final ServerChangeHandler serverChangeHandler;

    private ServerListener(String uri, List<String> categories, ServerChangeHandler serverChangeHandler) {
        super(URI.create(uri + "/serverinfo/" + String.join(",", categories)));
        this.serverChangeHandler = serverChangeHandler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        ServerUpdate<?> queueResult = gson.fromJson(s, ServerUpdate.class);
        serverChangeHandler.handleUpdate(queueResult);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        if (e instanceof ConnectException) {
            System.out.println("retrying connection in 15 seconds...");

            executorService.schedule(() -> {
                System.out.println("reconnecting...");
                reconnect();
            }, 15, TimeUnit.SECONDS);
        }
    }

    public static ServerListener instance(String uri, List<String> categories, ServerChangeHandler serverChangeHandler) {
        return new ServerListener(uri, categories, serverChangeHandler);
    }

}
