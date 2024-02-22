package com.readutf.matchmaker.queue.socket;

import com.google.gson.Gson;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.shared.queue.QueueEvent;

public class QueueListenerSocket extends WebSocket {

    public QueueListenerSocket(Gson gson) {
        super(gson, "/queue/listener");
    }

    @Override
    public void send(Object object) {
        if(!(object instanceof QueueEvent)) throw new IllegalArgumentException("Object must be of type QueueEvent");
        super.send(object, QueueEvent.class);
    }
}
