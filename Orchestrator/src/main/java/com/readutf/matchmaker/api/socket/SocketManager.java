package com.readutf.matchmaker.api.socket;

import io.javalin.Javalin;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SocketManager extends TimerTask {

    private final Javalin javalin;
    private final List<WebSocket> webSockets;

    public SocketManager(Javalin javalin, Timer timer) {
        timer.scheduleAtFixedRate(this, 0, 30_000);
        this.javalin = javalin;
        this.webSockets = new ArrayList<>();
    }

    public WebSocket registerSocket(WebSocket webSocket) {
        webSockets.add(webSocket);

        javalin.ws(webSocket.getPath(), wsConfig -> {
            wsConfig.onConnect(wsContext -> {
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


    @Override
    public void run() {
        for (WebSocket webSocket : webSockets) {
            if(webSocket.isKeepAlive()) {
                webSocket.send("keepAlive");
            }
        }
    }
}
