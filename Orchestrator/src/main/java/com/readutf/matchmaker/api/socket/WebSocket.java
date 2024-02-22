package com.readutf.matchmaker.api.socket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WebSocket {

    private final Gson gson;
    private final String path;
    private final List<WsContext> contexts;

    public WebSocket(Gson gson, String path) {
        this.contexts = new ArrayList<>();
        this.gson = gson;
        this.path = path;
    }

    public void onConnect(WsContext wsContext) {};

    public void onMessage(WsContext wsContext) {};

    public void onClose(WsContext wsContext) {};

    public void send(Object object) {
        for (WsContext context : contexts) {
            context.send(object);
        }
    }

    public void send(Object object, Class<?> clazz) {
        for (WsContext context : contexts) {
            context.send(gson.toJson(object, clazz));
        }
    }

}
