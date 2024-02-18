package com.readutf.matchmaker.api.socket;

import io.javalin.websocket.WsContext;
import lombok.Getter;

import java.util.List;

@Getter
public class WebSocket {

    private final String path;
    private final boolean keepAlive;
    private final List<WsContext> contexts;

    public WebSocket(String path, boolean keepAlive) {
        this.path = path;
        this.keepAlive = keepAlive;
        this.contexts = List.of();
    }

    public void onConnect(WsContext wsContext) {};

    public void onMessage(WsContext wsContext) {};

    public void onClose(WsContext wsContext) {};

    public void send(String message) {
        for (WsContext context : contexts) {
            context.send(message);
        }
    }

    public void sendJson(Object data) {
        for (WsContext context : contexts) {
            context.send(data);
        }
    }

}
