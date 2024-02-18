package com.readutf.matchmaker.api.socket;

import io.javalin.Javalin;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class SocketManager {

    private final Javalin javalin;
    private final List<WebSocket> webSockets;

    public SocketManager(Javalin javalin, Timer timer) {
        this.javalin = javalin;
        this.webSockets = new ArrayList<>();
    }

    public WebSocket registerSocket(WebSocket webSocket) {
        webSockets.add(webSocket);

        System.out.println(webSocket.getPath());

        javalin.ws(webSocket.getPath(), wsConfig -> {
            wsConfig.onConnect(wsContext -> {
                wsContext.session.setIdleTimeout(Duration.ofMillis(-1));
                webSocket.getContexts().add(wsContext);
                webSocket.onConnect(wsContext);
            });
            wsConfig.onMessage(webSocket::onMessage);
            wsConfig.onClose(wsContext -> {
                webSocket.getContexts().remove(wsContext);
                webSocket.onClose(wsContext);
            });
        });

        return webSocket;
    }
}
