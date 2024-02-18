package com.readutf.matchmaker.api.socket;

import com.readutf.matchmaker.api.ApiResponse;
import io.javalin.websocket.WsContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WebSocket {

    private final String path;
    private final boolean keepAlive;
    private final List<WsContext> contexts;

    public WebSocket(String path, boolean keepAlive) {
        this.path = path;
        this.keepAlive = keepAlive;
        this.contexts = new ArrayList<>();
    }

    public void onConnect(WsContext wsContext) {};

    public void onMessage(WsContext wsContext) {};

    public void onClose(WsContext wsContext) {};

    public void send(ApiResponse<?> response) {
        for (WsContext context : contexts) {
            context.send(response);
        }
    }

}
