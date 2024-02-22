package com.readutf.matchmaker.queue;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueListener extends WebSocketClient {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final QueueEventHandler queueEventHandler;
    private final Gson gson;

    private QueueListener(String uri, QueueEventHandler queueEventHandler) {
        super(URI.create(uri + "/queue/listener"));

        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(QueueEvent.getAdapter())
                .create();
        this.queueEventHandler = queueEventHandler;
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("connected");
    }


    @Override
    public void onMessage(String s) {
        QueueEvent queueResult = gson.fromJson(s, QueueEvent.class);
        queueEventHandler.onEvent(queueResult);
    }

    @Override
    public void onClose(int i, String reason, boolean remote) {

        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + i + " Reason: "
                        + reason);

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

    public static QueueListener instance(String uri, QueueEventHandler queueEventHandler) {
        return new QueueListener(uri, queueEventHandler);
    }

}
