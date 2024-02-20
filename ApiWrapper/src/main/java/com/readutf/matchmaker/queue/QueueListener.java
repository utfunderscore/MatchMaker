package com.readutf.matchmaker.queue;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readutf.matchmaker.api.ApiResponse;
import com.readutf.matchmaker.queue.events.QueueResultEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class QueueListener extends WebSocketClient {

    private final QueueEventHandler queueEventHandler;
    private final Gson gson;

    public QueueListener(String uri, QueueEventHandler queueEventHandler) {
        super(URI.create(uri));
        this.gson = new Gson();
        this.queueEventHandler = queueEventHandler;
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        ApiResponse<QueueResultEvent> queueResult = gson.fromJson(s, new TypeToken<>(){});
        if(queueResult.isSuccess()) {
            queueEventHandler.onEvent(queueResult.getData());
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
